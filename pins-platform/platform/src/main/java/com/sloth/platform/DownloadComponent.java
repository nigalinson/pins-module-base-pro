package com.sloth.platform;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface DownloadComponent {

    abstract class DownloadEngineBuilder<Eg extends DownloadComponent> {
        private int concurrency = 15;

        public DownloadEngineBuilder<Eg> setConcurrency(int concurrency) {
            this.concurrency = concurrency;
            return this;
        }

        public int getConcurrency() {
            return concurrency;
        }

        public abstract Eg create();
    }

    DownloadTask createTask();

    DownloadTask createTask(String url);

    /**
     * 取消并删除所有进行中的任务
     * 如果同时创建和删除，可能导致紊乱
     */
    void cancelAll();

    /**
     * 取消并删除任务
     * 如果同时创建和删除，可能导致紊乱
     * @param taskId
     */
    void cancel(long taskId);

    /**
     * 暂停所有任务
     */
    void pauseAll();

    /**
     * 暂停任务
     * @param taskId
     */
    void pause(long taskId);

    /**
     * 启动所有暂停中的下载任务
     */
    void resumeAll();

    /**
     * 启动暂停中的下载任务
     * @param taskId
     */
    void resume(long taskId);

    /**
     * 销毁
     */
    void destroy();

    abstract class DownloadTask {

        private DownloadListener mDownloadListener;
        private int retryTimes = 3;
        private long callbackProgressTimes = 1000L;
        private final AtomicInteger remainRetryTimes = new AtomicInteger(3);
        private String url, localPath;

        private final Map<String, String> headers = new HashMap<>();

        private long taskId = -1;

        protected DownloadTask() { }

        protected DownloadTask(String url) {
            this.url = url;
        }

        public DownloadTask setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public DownloadTask setUrl(String url, String localPath) {
            this.url = url;
            this.localPath = localPath;
            return this;
        }

        public DownloadTask setLocalPath(String localPath) {
            this.localPath = localPath;
            return this;
        }

        public String getLocalPath() {
            return localPath;
        }

        public DownloadTask setCallbackProgressTimes(long callbackProgressTimes) {
            this.callbackProgressTimes = callbackProgressTimes;
            return this;
        }

        public long getCallbackProgressTimes() {
            return callbackProgressTimes;
        }

        public DownloadTask addHeader(String k, String v){
            headers.put(k, v);
            return this;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public DownloadTask setAutoRetryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
            remainRetryTimes.set(retryTimes);
            return this;
        }

        public int getRetryTimes() {
            return retryTimes;
        }

        public int useRetryTime() {
            return Math.max(0, remainRetryTimes.decrementAndGet());
        }

        public DownloadTask setListener(DownloadListener listener) {
            this.mDownloadListener = listener;
            return this;
        }

        public DownloadListener getListener() {
            return mDownloadListener;
        }

        public long getTaskId(){
            return taskId;
        }

        ////////////////////////////////////////////////////////////////////////////////////////
        //region notify listener

        public void notifyPending(DownloadTask task){
            if(mDownloadListener != null){
                mDownloadListener.pending(task);
            }
        }

        public void notifyConnected(DownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes){
            if(mDownloadListener != null){
                mDownloadListener.connected(task, etag, isContinue, soFarBytes, totalBytes);
            }
        }

        public void notifyProgress(DownloadTask task,  int soFarBytes, int totalBytes){
            if(mDownloadListener != null){
                mDownloadListener.progress(task, soFarBytes, totalBytes);
            }
        }

        public void notifyRetry(DownloadTask task, Throwable ex, int retryingTimes, int soFarBytes){
            if(mDownloadListener != null){
                mDownloadListener.retry(task, ex, retryingTimes, soFarBytes);
            }
        }

        public void notifyCompleted(DownloadTask task){
            if(mDownloadListener != null){
                mDownloadListener.completed(task);
            }
        }

        public void notifyPaused(DownloadTask task){
            if(mDownloadListener != null){
                mDownloadListener.paused(task);
            }
        }

        public void notifyError(DownloadTask task, Throwable ex){
            if(mDownloadListener != null){
                mDownloadListener.error(task, ex);
            }
        }

        public void notifyWarn(DownloadTask task, String warnMsg){
            if(mDownloadListener != null){
                mDownloadListener.warn(task, warnMsg);
            }
        }

        //endregion notify listener
        ////////////////////////////////////////////////////////////////////////////////////////

        public long start(){
            taskId = onStartTask();
            onTaskIdGenerated(taskId);
            return taskId;
        }

        protected abstract long onStartTask();

        protected void onTaskIdGenerated(long taskId) { }

    }

    class DownloadListener {

        private static final String TAG = "DownloadListener";

        public void pending(DownloadTask task){
            Platform.log().d(TAG, "pending");
        }

        public void connected(DownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes){
            Platform.log().d(TAG, "connected, ETag:" + etag + ",soFar:" + soFarBytes + ", total:" + totalBytes);
        }

        public void progress(DownloadTask task,  int soFarBytes, int totalBytes){
            Platform.log().d(TAG, "progress, soFar:" + soFarBytes + ", total:" + totalBytes);
        }

        public void retry(DownloadTask task, Throwable ex, int retryingTimes, int soFarBytes){
            Platform.log().e(TAG, "retry," + ex.getMessage());
        }

        public void completed(DownloadTask task){
            Platform.log().d(TAG, "complete");
        }

        public void paused(DownloadTask task){
            Platform.log().d(TAG, "paused");
        }

        public void error(DownloadTask task, Throwable ex){
            Platform.log().e(TAG, "error," + ex.getMessage());
        }

        public void warn(DownloadTask task, String warnMsg){
            Platform.log().w(TAG, "warn," + warnMsg);
        }
    }

}
