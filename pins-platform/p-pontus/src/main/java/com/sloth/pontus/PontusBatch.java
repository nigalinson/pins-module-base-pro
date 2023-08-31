package com.sloth.pontus;

import com.sloth.platform.ResourceManagerComponent;
import com.sloth.pontus.listener.ResourceListenerAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PontusBatch implements ResourceManagerComponent.Batch {

    private final AtomicInteger count;
    private final List<String> urls;
    private final List<String> locals;
    private final List<String> md5s;
    private final List<String> ready;
    private ResourceManagerComponent.BatchListener listener;

    private Pontus owner;

    public PontusBatch(List<String> urls, List<String> locals, List<String> md5s, ResourceManagerComponent.BatchListener listener) {
        this.urls = urls;
        this.locals = locals;
        this.md5s = md5s;
        this.listener = listener;
        count = new AtomicInteger(urls.size());
        this.ready = new ArrayList<>();
    }

    public PontusBatch by(Pontus pontus){
        this.owner = pontus;
        if(urls == null || urls.isEmpty()){
            if(listener != null){
                listener.onMultiResult(ready);
            }
            return this;
        }

        for(int i = 0; i < urls.size(); ++i){
            String url = urls.get(i);
            String local = (locals != null ? locals.get(i) : null);
            String md5 = (md5s != null ? md5s.get(i) : null);

            pontus.get(url).setPath(local).setMd5(md5).submit(new ResourceListenerAdapter(){
                @Override
                public void onResourceReady(Long resourceId, String url, String localPath) {
                    ready.add(url);
                    if(count.decrementAndGet() <= 0){
                        if(listener != null){
                            listener.onMultiResult(ready);
                        }
                    }
                }

                @Override
                public void onResourceFailed(Long resourceId, String url, String localPath, String errMsg) {
                    super.onResourceFailed(resourceId, url, localPath, errMsg);
                    if(count.decrementAndGet() <= 0){
                        if(listener != null){
                            listener.onMultiResult(ready);
                        }
                    }
                }
            });
        }
        return this;
    }

    /**
     * 终止下载，并取消旗监听
     */
    @Override
    public void cancel(){
        if(owner != null && urls.size() > 0){
            for(String url: urls){
                owner.cancel(url);
            }
        }
        owner = null;
        listener = null;
    }

    /**
     * 取消监听(会继续下载+回调就绪状态)，不回调外部监听
     *
     */
    @Override
    public void detach(){
        owner = null;
        listener = null;
    }


}
