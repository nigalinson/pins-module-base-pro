package com.sloth.download.fetch;

import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.DownloadComponent;
import com.sloth.platform.Platform;
import com.sloth.platform.PlatformConfig;
import com.sloth.utils.FileUtils;
import com.sloth.utils.Utils;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fetch下载器的各个方法的实际效果：
 *  ——> fetch.pause((int)taskId);  //暂停
 *  ——> fetch.resume((int)taskId); //继续
 *  ——> fetch.cancel((int)taskId); //从task移除，不删除缓存，不删除结果
 *  ——> fetch.remove((int)taskId); //从task移除，删除缓存，不删除结果
 *  ——> fetch.delete((int) taskId); //从task移除，删除缓存， 删除结果(要先pause)
 */
@RouterService(interfaces = DownloadComponent.class, key = ComponentTypes.Downloader.FETCH)
public class FetchDownloadComponent implements DownloadComponent {

    public static class Builder extends DownloadEngineBuilder<FetchDownloadComponent> {

        private boolean enableLog = false;

        public boolean isEnableLog() {
            return enableLog;
        }

        public Builder setEnableLog(boolean enableLog) {
            this.enableLog = enableLog;
            return this;
        }

        @Override
        public FetchDownloadComponent create() {
            return new FetchDownloadComponent(this);
        }
    }

    private final Fetch fetch;
    private final FetchWatcher fetchWatcher;
    private final Handler handler;

    private FetchDownloadComponent() {
        this(new Builder());
    }

    private FetchDownloadComponent(Builder builder) {
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(Utils.getApp())
                .setDownloadConcurrentLimit(builder.getConcurrency())
                .build();
        fetch = Fetch.Impl.getInstance(fetchConfiguration)
                .enableLogging(builder.isEnableLog());
        handler = new Handler(Looper.getMainLooper());
        this.fetchWatcher = new FetchWatcher(fetch);
        this.fetch.addListener(fetchWatcher);
    }

    @Override
    public DownloadTask createTask() {
        return new Task(fetch, fetchWatcher);
    }

    @Override
    public DownloadTask createTask(String url) {
        return new Task(url, fetch, fetchWatcher);
    }

    @Override
    public void cancelAll() {
        fetch.pauseAll();
        handler.postDelayed(fetch::deleteAll, 200);
    }

    @Override
    public void cancel(long taskId) {
        fetch.pause((int) taskId);
        handler.postDelayed(()-> fetch.delete((int)taskId), 200);
    }

    @Override
    public void pauseAll() {
        fetch.pauseAll();
    }

    @Override
    public void pause(long taskId) {
        fetch.pause((int)taskId);
    }

    @Override
    public void resumeAll() {
        fetch.resumeAll();
    }

    @Override
    public void resume(long taskId) {
        fetch.resume((int)taskId);
    }

    @Override
    public void destroy() {
        handler.removeCallbacks(null);
    }

    public static class Task extends DownloadComponent.DownloadTask {
        private static final String TAG = "Task";

        private final Fetch fetch;

        private final FetchWatcher fetchWatcher;

        public Task(Fetch fetch, FetchWatcher fetchWatcher) {
            this.fetch = fetch;
            this.fetchWatcher = fetchWatcher;
        }

        public Task(String url, Fetch fetch, FetchWatcher fetchWatcher) {
            super(url);
            this.fetch = fetch;
            this.fetchWatcher = fetchWatcher;
        }

        @Override
        protected long onStartTask() {
            String origin = getUrl();
            String localPath = getLocalPath();
            Platform.log().d(TAG, "--> 准备下载文件  --> url:" + origin + " --> 文件路径:" + localPath);

            File desFile = new File(localPath);

            File folderFile = desFile.getParentFile();
            if (!folderFile.exists()) {
                boolean isSuccess = folderFile.mkdirs();
                Platform.log().i(TAG, "--> 创建下载目录 :" + isSuccess);
                if (!isSuccess) {
                    return -1;
                }
            }

            Request request = new Request(getUrl(), getLocalPath());
            request.setPriority(Priority.HIGH);
            request.setNetworkType(NetworkType.ALL);
            request.setAutoRetryMaxAttempts(getRetryTimes());
            if(getHeaders() != null){
                for(String key: getHeaders().keySet()){
                    String v = getHeaders().get(key);
                    if(null != v && !"".equals(v)){
                        request.addHeader(key, v);
                    }
                }
            }

            fetch.enqueue(
                    request,
                    updatedRequest -> {
                        Platform.log().d(TAG, "updatedRequest:" + updatedRequest.getId());
                    },
                    error -> {
                        Platform.log().d(TAG, "error:" + error.name());
                        fetchWatcher.interrupt(request.getId(), error.getThrowable());
                    }
            );

            return request.getId();
        }

        @Override
        protected void onTaskIdGenerated(long taskId) {
            fetchWatcher.register(this);
        }
    }

    private static final class FetchWatcher implements FetchListener {
        private static final String TAG = "FetchWatcher";

        private final Map<Long, Task> registeredTasks = new HashMap<>();

        private final Fetch fetch;

        public FetchWatcher(Fetch fetch) {
            this.fetch = fetch;
        }

        void register(Task tk){
            registeredTasks.put(tk.getTaskId(), tk);
        }

        void unregister(Task tk){
            registeredTasks.remove(tk.getTaskId());
        }

        void unregister(long tkId){
            registeredTasks.remove(tkId);
        }

        void unregisterAll(){
            registeredTasks.clear();
        }

        void interrupt(int taskId, Throwable throwable){
            Task tk = registeredTasks.get((long) taskId);
            if(tk != null){
                tk.notifyError(tk, throwable);
                unregister(tk);
            }
        }

        @Override
        public void onAdded(@NonNull Download download) {
            Platform.log().d(TAG, "onAdded: " + download.getId());
            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                tk.notifyPending(tk);
            }
        }

        @Override
        public void onCancelled(@NonNull Download download) {
            Platform.log().d(TAG, "onCancelled: " + download.getId());
            fetch.delete(download.getId());
            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                tk.notifyError(tk, new RuntimeException("cancel download!"));
                unregister(tk);
            }
        }

        @Override
        public void onCompleted(@NonNull Download download) {
            Platform.log().d(TAG, "onCompleted: " + download.getId());

            //下载完成后从任务列表中删除
            fetch.remove(download.getId());

            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                tk.notifyCompleted(tk);
                unregister(tk);
            }
        }

        @Override
        public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int i) {
            Platform.log().d(TAG, "onDownloadBlockUpdated: " + download.getId());
        }

        @Override
        public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {
            Platform.log().d(TAG, "onError: " + download.getId() + ", " + (throwable != null ? throwable.getMessage() : "未知异常"));

            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                tk.notifyError(tk, throwable);
                unregister(tk);
            }
        }

        @Override
        public void onPaused(@NonNull Download download) {
            Platform.log().d(TAG, "onPaused: " + download.getId());
            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                //普通暂停操作
                tk.notifyPaused(tk);
            }
        }

        @Override
        public void onProgress(@NonNull Download download, long l, long l1) {
            long total = download.getTotal();
            long soFar = total * download.getProgress() / 100;
            Platform.log().d(TAG, download.getId() + ", onProgress: "  + "," + soFar + "," + total + "," + (total != 0 ? (1f * soFar / total) : 0));
            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                tk.notifyProgress(tk, (int)soFar, (int)total);
            }
        }

        @Override
        public void onQueued(@NonNull Download download, boolean b) {
            Platform.log().d(TAG, "onQueued: " + download.getId());
        }

        @Override
        public void onRemoved(@NonNull Download download) {
            Platform.log().d(TAG, "onRemoved: " + download.getId());
            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                tk.notifyError(tk, new RuntimeException("remove download!"));
                unregister(tk);
            }
        }

        @Override
        public void onDeleted(@NonNull Download download) {
            Platform.log().d(TAG, "onDeleted: " + download.getId());
            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                FileUtils.delete(tk.getLocalPath());
                Platform.log().d(TAG, "delete file: " + tk.getLocalPath());
                tk.notifyError(tk, new RuntimeException("delete download!"));
                unregister(tk);
            }
        }

        @Override
        public void onResumed(@NonNull Download download) {
            Platform.log().d(TAG, "onResumed: " + download.getId());
        }

        @Override
        public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> list, int i) {
            Platform.log().d(TAG, "onStarted: " + download.getId() + "," + i);
            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                tk.notifyConnected(tk, "", false, 0, 0);
            }
        }

        @Override
        public void onWaitingNetwork(@NonNull Download download) {
            Platform.log().d(TAG, "onWaitingNetwork: " + download.getId());
            Task tk = registeredTasks.get((long) download.getId());
            if(tk != null){
                tk.notifyWarn(tk, "network slow !");
            }
        }
    }

}
