package com.sloth.download.liulisho;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.DownloadComponent;
import com.sloth.platform.PlatformConfig;
import com.sloth.utils.Utils;
import java.util.HashMap;
import java.util.Map;

/**
 * 流利说的下载器综合性能在实用中表现是最好的，但是在某些网络状态下，出现过下载中断，并完全没有任何回调的情况。
 * fixme 有空的话看看源码能不能修复
 */
@RouterService(interfaces = DownloadComponent.class, key = ComponentTypes.Downloader.LIU_LI_SHO)
public class LiuLiShoDownloadComponent implements DownloadComponent {
    private static final String TAG = "LiuLiShoDownloadEngine";

    private final Map<Long, DownloadTask> idTaskCache;

    public LiuLiShoDownloadComponent() {
        this(new Builder());
    }

    public LiuLiShoDownloadComponent(Builder builder) {
        FileDownloader.setup(Utils.getApp());
        FileDownloader.getImpl().setMaxNetworkThreadCount(builder.getConcurrency());
        idTaskCache = new HashMap<>();
    }

    @Override
    public DownloadTask createTask() {
        return new Task(idTaskCache);
    }

    @Override
    public DownloadTask createTask(String url) {
        return new Task(idTaskCache, url);
    }

    @Override
    public void cancelAll() {
        FileDownloader.getImpl().clearAllTaskData();
        idTaskCache.clear();
    }

    @Override
    public void cancel(long taskId) {
        if(idTaskCache.containsKey(taskId)){
            DownloadTask task = idTaskCache.get(taskId);
            if(task != null){
                FileDownloader.getImpl().clear((int) taskId, task.getLocalPath());
            }else{
                pause(taskId);
            }
            idTaskCache.remove(taskId);
        }else{
            pause(taskId);
        }
    }

    @Override
    public void pauseAll() {
        FileDownloader.getImpl().pauseAll();
    }

    @Override
    public void pause(long taskId) {
        FileDownloader.getImpl().pause((int)taskId);
    }

    @Override
    public void resumeAll() {
        for(Long key: idTaskCache.keySet()){
            DownloadTask task = idTaskCache.get(key);
            if(task != null){
                task.start();
            }
        }
    }

    @Override
    public void resume(long taskId) {
        DownloadTask task = idTaskCache.get(taskId);
        if(task != null){
            task.start();
        }
    }

    @Override
    public void destroy() {
        idTaskCache.clear();
    }

    public static class Task extends DownloadComponent.DownloadTask {

        private final Map<Long, DownloadTask> idTaskCache;

        private Task(Map<Long, DownloadTask> tmp) {
            idTaskCache = tmp;
        }

        private Task(Map<Long, DownloadTask> tmp, String url) {
            super(url);
            idTaskCache = tmp;
        }

        @Override
        protected void onTaskIdGenerated(long taskId) {
            super.onTaskIdGenerated(taskId);
            idTaskCache.put(taskId, this);
        }

        @Override
        protected long onStartTask() {
            BaseDownloadTask llsTask = FileDownloader.getImpl().create(getUrl());
            if(getHeaders() != null){
                for(String k: getHeaders().keySet()){
                    llsTask.addHeader(k, getHeaders().get(k));
                }
            }
            return llsTask.setPath(getLocalPath())
                    .setCallbackProgressTimes((int) getCallbackProgressTimes())
                    .setAutoRetryTimes(getRetryTimes())
                    .setListener(new ListenerAdapter(this, getListener()))
                    .start();
        }

        private void onFinish(){
            idTaskCache.remove(getTaskId());
        }

    }

    private static class ListenerAdapter extends FileDownloadListener {

        private final Task tk;

        private final DownloadComponent.DownloadListener proxy;

        public ListenerAdapter(Task task, DownloadListener proxy) {
            this.tk = task;
            this.proxy = proxy;
        }

        @Override
        protected void pending(BaseDownloadTask var1, int var2, int var3){
            if(proxy != null){
                proxy.pending(tk);
            }
        }

        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            if(proxy != null){
                proxy.connected(tk, etag, isContinue, soFarBytes, totalBytes);
            }
        }

        protected void progress(BaseDownloadTask var1, int var2, int var3){
            if(proxy != null){
                proxy.progress(tk, var2, var3);
            }
        }

        protected void retry(BaseDownloadTask task, Throwable ex, int retryingTimes, int soFarBytes) {
            if(proxy != null){
                proxy.retry(tk, ex, retryingTimes, soFarBytes);
            }
        }

        protected void completed(BaseDownloadTask var1){
            tk.onFinish();
            if(proxy != null){
                proxy.completed(tk);
            }
        }

        protected void paused(BaseDownloadTask var1, int var2, int var3){
            if(proxy != null){
                proxy.paused(tk);
            }
        }

        protected void error(BaseDownloadTask var1, Throwable var2){
            tk.onFinish();
            if(proxy != null){
                proxy.error(tk, var2);
            }
        }

        protected void warn(BaseDownloadTask var1){
            if(proxy != null){
                proxy.warn(tk, "");
            }
        }
    }

    public static final class Builder extends DownloadComponent.DownloadEngineBuilder<LiuLiShoDownloadComponent>{

        @Override
        public LiuLiShoDownloadComponent create() {
            return new LiuLiShoDownloadComponent(this);
        }
    }

}
