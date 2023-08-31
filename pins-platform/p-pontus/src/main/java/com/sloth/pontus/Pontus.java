package com.sloth.pontus;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.Platform;
import com.sloth.platform.ResourceManagerComponent;
import com.sloth.pontus.entity.ResourceEntity;
import com.sloth.pontus.entity.ResourceState;
import com.sloth.pontus.listener.DatabaseListener;
import com.sloth.pontus.worker.DownloadWorker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RouterService(interfaces = ResourceManagerComponent.class, key = ComponentTypes.ResourceManager.DEFAULT)
public class Pontus implements ResourceManagerComponent, DownloadWorker.WorkCallback {
    private static final String TAG = Pontus.class.getSimpleName();

    private final ResourceManagerConfig resourceManagerConfig;
    private final List<ResourceListener> globalListenerList = new ArrayList<>();
    private final Map<Long, List<ResourceListener>> idListenerList = new HashMap<>();
    private final ResourceDB resourceDB;
    private final DownloadWorker downloadWorker;

    public Pontus() {
        this(new PontusConfig());
    }

    public Pontus(ResourceManagerConfig resourceManagerConfig) {
        this.resourceManagerConfig = resourceManagerConfig;
        this.resourceDB = new ResourceDB(resourceManagerConfig.dbPath());
        this.downloadWorker = new DownloadWorker(resourceManagerConfig.downloadEngine(), resourceManagerConfig.concurrent(), resourceManagerConfig.retryTimes(), this);
    }

    @Override
    public void close(){
        clearGlobalListener();
        resourceDB.close();
        downloadWorker.close();
    }

    @Override
    public void addGlobalListener(ResourceListener resourceListener){
        int index = globalListenerList.indexOf(resourceListener);
        if(index == -1){
            globalListenerList.add(resourceListener);
        }
    }

    @Override
    public void removeGlobalListener(ResourceListener resourceListener){
        int index = globalListenerList.indexOf(resourceListener);
        if(index != -1){
            globalListenerList.remove(index);
        }
    }

    @Override
    public void clearGlobalListener(){
        globalListenerList.clear();
    }

    @Override
    public Request get(String url) {
        return new PontusRequest(this, resourceManagerConfig, url);
    }

    @Override
    public void submit(Request request) {
        resourceDB.getAndCreateIfNotExist(request.url(), request.path(), request.md5(),
                request.group(), request.additionInfo(), request.maxHotness(), new DatabaseListener() {
            @Override
            public void querySuccess(ResourceEntity resourceEntity) {
                request.setId(resourceEntity.getId());
                addIntoIdListener(resourceEntity.getId(), request.getListener());
                if(resourceEntity.getState() == ResourceState.STATE_READY){
                    notifySuccess(resourceEntity.getId(), resourceEntity.getOriginUrl(), resourceEntity.getLocalPath());
                }else{
                    downloadWorker.enqueue(resourceEntity.getId(), resourceEntity.getOriginUrl(), resourceEntity.getLocalPath(), resourceEntity.getMd5());
                }
            }

            @Override
            public void queryFailed(String errMsg) {
                if(request.getListener() != null){
                    request.getListener().onResourceFailed(-1L, request.url(), request.path(), errMsg);
                }
            }
        });
    }

    /**
     * 仅获取本地连接，不下载
     * @param url
     */
    @Override
    public void queryUrl(String url, ResourceListener resourceListener) {
        resourceDB.getOnlyIfExist(url, new DatabaseListener() {
            @Override
            public void querySuccess(ResourceEntity resourceEntity) {
                if (resourceListener != null) {
                    resourceListener.onResourceReady(resourceEntity.getId(), resourceEntity.getOriginUrl(), resourceEntity.getLocalPath());
                }
            }

            @Override
            public void queryFailed(String errMsg) {
                if(resourceListener != null){
                    resourceListener.onResourceFailed(-1L, url, null, errMsg);
                }
            }
        });
    }


    /**
     * 会删除已下载的文件，谨慎使用
     * @param url
     */
    @Override
    public void cancel(String url){
        resourceDB.getOnlyIfExist(url, new DatabaseListener() {
            @Override
            public void querySuccess(ResourceEntity resourceEntity) {
                downloadWorker.cancel(resourceEntity.getId());
                notifyFailed(resourceEntity.getId(), resourceEntity.getOriginUrl(), resourceEntity.getLocalPath(), "user cancel !");
            }

            @Override
            public void queryFailed(String errMsg) {
                downloadWorker.cancel(url);
            }
        });
    }

    @Override
    public void cancelLocalPath(String localName) {
        resourceDB.getByLocalOnlyIfExist(localName, new DatabaseListener() {
            @Override
            public void querySuccess(ResourceEntity resourceEntity) {
                downloadWorker.cancel(resourceEntity.getId());
                notifyFailed(resourceEntity.getId(), resourceEntity.getOriginUrl(), resourceEntity.getLocalPath(), "user cancel !");
            }

            @Override
            public void queryFailed(String errMsg) {
                downloadWorker.cancelByLocal(localName);
            }
        });
    }

    /**
     * 会删除所有已下载内容，谨慎使用
     */
    @Override
    public void cancelAll(){
        removeAllListeners();
        downloadWorker.cancelAll();
    }

    @Override
    public void removeAllListeners(){
        idListenerList.clear();
        globalListenerList.clear();
    }

    @Override
    public void removeListener(ResourceListener resourceListener) {
        if(resourceListener == null) return;
        for(Long id: idListenerList.keySet()){
            List<ResourceListener> list = idListenerList.get(id);
            if(list != null){
                list.remove(resourceListener);
            }
        }
    }

    @Override
    public void removeListeners(Long id) {
        List<ResourceListener> list = idListenerList.get(id);
        if(list != null){
            list.clear();
        }
    }

    @Override
    public void clearResource(ClearRequest clearRequest, ClearListener clearListener) {
        resourceDB.removeUntil(clearRequest, resourceManagerConfig, clearListener);
    }

    @Override
    public void size(Long id, int size) {
        resourceDB.size(id, size);
    }

    @Override
    public void onWorkStateChanged(Long id, boolean success, String url, String local, String errMsg) {
        Platform.log().d(TAG, "on WorkCenter callback:" + id + ",success:" + success + "," + url);
        if(success){
            resourceDB.updateState(id, ResourceState.STATE_READY);
            notifySuccess(id, url, local);
        }else{
            resourceDB.updateState(id, ResourceState.STATE_INIT);
            notifyFailed(id, url, local, errMsg);
        }
    }

    private void addIntoIdListener(Long id, ResourceListener resourceListener) {
        if(resourceListener == null){ return; }

        removeListener(resourceListener);

        List<ResourceListener> idT = idListenerList.get(id);
        if(idT != null){
            int index = idT.indexOf(resourceListener);
            if(index == -1){
                idT.add(resourceListener);
            }
        }else{
            idT = new ArrayList<>();
            idT.add(resourceListener);
            idListenerList.put(id, idT);
        }
    }

    private void notifySuccess(Long id, String url, String local) {
        List<ResourceListener> idT = idListenerList.get(id);
        if(idT != null){
            Iterator<ResourceListener> iterator = idT.iterator();
            while(iterator.hasNext()){
                ResourceListener lis = iterator.next();
                lis.onResourceReady(id, url, local);
                iterator.remove();
            }
            idListenerList.remove(id);
        }
        for(ResourceListener lis: globalListenerList){
            lis.onResourceReady(id, url, local);
        }
    }

    private void notifyFailed(Long id, String url, String local, String error) {
        List<ResourceListener> idT = idListenerList.get(id);
        if(idT != null){
            Iterator<ResourceListener> iterator = idT.iterator();
            while(iterator.hasNext()){
                ResourceListener lis = iterator.next();
                lis.onResourceFailed(id, url, local, error);
                iterator.remove();
            }
            idListenerList.remove(id);
        }
        for(ResourceListener lis: globalListenerList){
            lis.onResourceFailed(id, url, local, error);
        }
    }

}
