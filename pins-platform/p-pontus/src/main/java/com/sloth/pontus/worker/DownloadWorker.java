package com.sloth.pontus.worker;

import com.sloth.platform.DownloadComponent;
import com.sloth.platform.Platform;
import com.sloth.utils.EncryptUtils;
import com.sloth.utils.FileUtils;
import com.sloth.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2022/3/25 18:00
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2022/3/25       Carl
 * Why & What is modified:
 *
 */
public class DownloadWorker {

    private static final String TAG = DownloadWorker.class.getSimpleName();

    private final AtomicBoolean enable = new AtomicBoolean(true);

    /**
     * 重试次数
     */
    private final int retryTimes;

    private final int concurrency;

    private final CentralCallbackProvider centralCallbackProvider;

    private final LinkedList<WorkerListenerAdapter> waiting = new LinkedList<>();
    private final LinkedList<WorkerListenerAdapter> listeners = new LinkedList<>();

    private final DownloadComponent mDownloadComponent;

    public DownloadWorker(DownloadComponent DownloadComponent, int concurrent, int retry, WorkCallback workCallback) {
        this.mDownloadComponent = DownloadComponent;
        this.concurrency = concurrent;
        this.retryTimes = retry;
        this.centralCallbackProvider = new CentralCallbackProvider() {
            @Override
            void finishMe(WorkerListenerAdapter adp) {
                listeners.remove(adp);
                takeFromWaiting();
            }
        };
        this.centralCallbackProvider.set(workCallback);
    }

    public void close(){
        listeners.clear();
        centralCallbackProvider.clear();
        mDownloadComponent.pauseAll();
        enable.set(false);
    }

    public void enqueue(Long id, String url, String localPath, String md5){
        Platform.log().d(TAG, "enqueue:" + id + "," + url);

        if(!alreadyDownloading(id, url)){
            WorkerListenerAdapter newAdp = new WorkerListenerAdapter(id, url, localPath, md5, centralCallbackProvider);
            if(listeners.size() < concurrency){
                listeners.offer(newAdp);

                //下载前移除历史数据(不支持断点续传)
                FileUtils.delete(localPath);
                newAdp.downloadTaskId = mDownloadComponent.createTask(url)
                        .addHeader("x-ms-version", "2015-04-05")
                        .setLocalPath(localPath)
                        .setCallbackProgressTimes(1000)
                        .setAutoRetryTimes(retryTimes)
                        .setListener(newAdp)
                        .start();
            }else{
                Platform.log().d(TAG, "任务已满：暂存" + id + "," + url);
                waiting.offer(newAdp);
            }
        }else{
            Platform.log().d(TAG, "任务已存在：" + id + "," + url);
        }
    }

    private void takeFromWaiting() {
        WorkerListenerAdapter adp = waiting.poll();
        if(adp != null){
            listeners.offer(adp);
            adp.downloadTaskId = mDownloadComponent.createTask(adp.url)
                    .addHeader("x-ms-version", "2015-04-05")
                    .setLocalPath(adp.local)
                    .setCallbackProgressTimes(1000)
                    .setAutoRetryTimes(retryTimes)
                    .setListener(adp)
                    .start();
        }
    }

    public void cancel(Long id){
        WorkerListenerAdapter adp = getListener(id);
        if(adp != null){
            mDownloadComponent.pause(adp.downloadTaskId);
            listeners.remove(adp);
        }
    }

    public void cancel(String url){
        WorkerListenerAdapter adp = getListener(url);
        if(adp != null){
            mDownloadComponent.pause(adp.downloadTaskId);
            listeners.remove(adp);
        }
    }

    public void cancelByLocal(String local){
        WorkerListenerAdapter adp = getListenerByLocal(local);
        if(adp != null){
            mDownloadComponent.pause(adp.downloadTaskId);
            listeners.remove(adp);
        }
    }

    public void cancelAll(){
        waiting.clear();
        listeners.clear();
        mDownloadComponent.pauseAll();
    }

    private WorkerListenerAdapter getListener(Long id) {
        for(WorkerListenerAdapter adp: listeners){
            if(adp.dbId.equals(id)){
                return adp;
            }
        }
        for(WorkerListenerAdapter adp: waiting){
            if(adp.dbId.equals(id)){
                return adp;
            }
        }
        return null;
    }

     private WorkerListenerAdapter getListener(String url) {
        for(WorkerListenerAdapter adp: listeners){
            if(adp.url.equals(url)){
                return adp;
            }
        }
         for(WorkerListenerAdapter adp: waiting){
             if(adp.url.equals(url)){
                 return adp;
             }
         }
        return null;
    }

    private WorkerListenerAdapter getListenerByLocal(String local) {
        for(WorkerListenerAdapter adp: listeners){
            if(adp.local != null && adp.local.equals(local)){
                return adp;
            }
        }
        for(WorkerListenerAdapter adp: waiting){
            if(adp.local != null && adp.local.equals(local)){
                return adp;
            }
        }
        return null;
    }

    private boolean alreadyDownloading(Long id, String url) {
        for(WorkerListenerAdapter adp: listeners){
            if(adp.dbId.equals(id) || adp.url.equals(url)){
                return true;
            }
        }
        for(WorkerListenerAdapter adp: waiting){
            if(adp.dbId.equals(id) || adp.url.equals(url)){
                return true;
            }
        }
        return false;
    }

    private static abstract class CentralCallbackProvider {

        private WorkCallback workCallback;

        private CentralCallbackProvider() {
        }

        private WorkCallback provide(){
            return workCallback;
        }

        private void set(WorkCallback workCallback){
            this.workCallback = workCallback;
        }

        private void clear(){
            workCallback = null;
        }

        abstract void finishMe(WorkerListenerAdapter adp);
    }

    private static final class WorkerListenerAdapter extends DownloadComponent.DownloadListener {

        private final Long dbId;
        private final String url;
        private final String local;
        private final String md5;
        private CentralCallbackProvider centralCallbackProvider;

        long downloadTaskId;

        public WorkerListenerAdapter(Long dbId, String url, String local, String md5, CentralCallbackProvider centralCallbackProvider) {
            this.dbId = dbId;
            this.url = url;
            this.local = local;
            this.md5 = md5;
            this.centralCallbackProvider = centralCallbackProvider;
        }

        @Override
        public void pending(DownloadComponent.DownloadTask baseDownloadTask) {
            super.pending(baseDownloadTask);
        }

        @Override
        public void connected(DownloadComponent.DownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
            Platform.log().d(TAG, "开始下载:" + dbId + "," + url);
            if(centralCallbackProvider.provide() != null){
                centralCallbackProvider.provide().size(dbId, totalBytes);
            }
        }

        @Override
        public void progress(DownloadComponent.DownloadTask task, int soFarBytes, int totalBytes) {
            super.progress(task, soFarBytes, totalBytes);
        }

        @Override
        public void completed(DownloadComponent.DownloadTask task) {
            super.completed(task);

            boolean validate = false;
            if(StringUtils.notTrimEmpty(md5)){
                //存在MD5
                if(md5.equalsIgnoreCase(new String(EncryptUtils.encryptMD5File(local), StandardCharsets.UTF_8))){
                    //MD5校验成功
                    validate = true;
                }else{
                    //MD5校验失败
                    validate = false;
                }
            }else{
                //不存在MD5
                validate = true;
            }

            if(centralCallbackProvider.provide() != null){
                centralCallbackProvider.provide().onWorkStateChanged(dbId, validate, url, local, validate ? "success" : "MD5校验失败");
                centralCallbackProvider.finishMe(this);
                centralCallbackProvider = null;
            }
        }

        @Override
        public void paused(DownloadComponent.DownloadTask task) {
            super.paused(task);
        }

        @Override
        public void error(DownloadComponent.DownloadTask task, Throwable ex) {
            super.error(task, ex);
            if(centralCallbackProvider.provide() != null){
                centralCallbackProvider.provide().onWorkStateChanged(dbId, false, url, local, ex.getMessage());
                centralCallbackProvider.finishMe(this);
                centralCallbackProvider = null;
            }
        }


        @Override
        public void warn(DownloadComponent.DownloadTask task, String warnMsg) {
            super.warn(task, warnMsg);
        }
    }

    /**
     * 下载回调
     */
    public interface WorkCallback {

        void onWorkStateChanged(Long id, boolean success, String url, String local, String errMsg);

        void size(Long id, int size);
    }

}
