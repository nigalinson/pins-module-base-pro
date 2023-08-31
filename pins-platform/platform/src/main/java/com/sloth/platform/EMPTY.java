package com.sloth.platform;

import android.graphics.Bitmap;
import android.net.Uri;
import com.sloth.functions.json.gson.GsonComponent;
import com.sloth.functions.log.logcat.LogcatComponent;

import java.util.List;

public class EMPTY {

    static final LogComponent LOG = new LogcatComponent();
    static final PlayerComponent PLAYER = new EmptyPlayer();
    static final DownloadComponent DOWNLOADER = new EmptyDownloader();
    static final GsonComponent JSON = new GsonComponent();
    static final ResourceManagerComponent RESOURCE_MANAGER = new EmptyResourceManager();

    static final class EmptyPlayer implements PlayerComponent {

        @Override
        public void setListener(RyPlayerListener playerListener) {

        }

        @Override
        public void addListener(RyPlayerListener playerListener) {

        }

        @Override
        public void removeListener(RyPlayerListener playerListener) {

        }

        @Override
        public void preView(String localPath) {

        }

        @Override
        public void loadCover(String coverImagePath) {

        }

        @Override
        public void loadCover(int coverImageResPath) {

        }

        @Override
        public void loadCover(Bitmap coverBitmap) {

        }

        @Override
        public void applyPlayerViewScaleType(int videoWidth, int videoHeight) {

        }

        @Override
        public void play(Uri uri) {

        }

        @Override
        public void play(String local) {

        }

        @Override
        public void play(String local, String online) {

        }

        @Override
        public void prepare(String local, String online) {

        }

        @Override
        public void reset() {

        }

        @Override
        public void start() {

        }

        @Override
        public void pause() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void release() {

        }

        @Override
        public void clearSurface() {

        }

        @Override
        public void forward(boolean forward, int duration) {

        }

        @Override
        public boolean isLoop() {
            return false;
        }

        @Override
        public void loop(boolean loop) {

        }

        @Override
        public void setScaleType(int scaleType) {

        }

        @Override
        public boolean isPrepared() {
            return false;
        }

        @Override
        public boolean isPlaying() {
            return false;
        }

        @Override
        public void seekTo(int position) {

        }

        @Override
        public int getCurrentPosition() {
            return 0;
        }

        @Override
        public void setVolume(float v) {

        }

        @Override
        public void setCodecCount(int codecCount) {

        }

        @Override
        public void prepareWithAdditionalCodecs(List<String> localPaths) {

        }
    }

    static final class EmptyDownloader implements DownloadComponent {

        static final class EmptyTask extends DownloadTask {
            @Override
            protected long onStartTask() {
                return 0;
            }
        }

        @Override
        public DownloadTask createTask() {
            return new EmptyTask();
        }

        @Override
        public DownloadTask createTask(String url) {
            return new EmptyTask();
        }

        @Override
        public void cancelAll() {

        }

        @Override
        public void cancel(long taskId) {

        }

        @Override
        public void pauseAll() {

        }

        @Override
        public void pause(long taskId) {

        }

        @Override
        public void resumeAll() {

        }

        @Override
        public void resume(long taskId) {

        }

        @Override
        public void destroy() {

        }
    }

    static final class EmptyResourceManager implements ResourceManagerComponent {

        static final class EmptyRequest implements Request {

            @Override
            public Request setPath(String path) {
                return null;
            }

            @Override
            public Request setMd5(String md5) {
                return null;
            }

            @Override
            public Request setGroup(String group) {
                return null;
            }

            @Override
            public Request setAdditionInfo(String additionInfo) {
                return null;
            }

            @Override
            public Request setMaxHotness(boolean maxHotness) {
                return null;
            }

            @Override
            public Request submit(ResourceListener resourceListener) {
                return null;
            }

            @Override
            public void setId(Long id) {

            }

            @Override
            public Long getId() {
                return null;
            }

            @Override
            public String url() {
                return null;
            }

            @Override
            public String path() {
                return null;
            }

            @Override
            public String md5() {
                return null;
            }

            @Override
            public String group() {
                return null;
            }

            @Override
            public String additionInfo() {
                return null;
            }

            @Override
            public boolean maxHotness() {
                return false;
            }

            @Override
            public ResourceListener getListener() {
                return null;
            }

            @Override
            public void cancel() {

            }

            @Override
            public void detach() {

            }
        }

        @Override
        public void addGlobalListener(ResourceListener resourceListener) {

        }

        @Override
        public void removeGlobalListener(ResourceListener resourceListener) {

        }

        @Override
        public void clearGlobalListener() {

        }

        @Override
        public Request get(String url) {
            return new EmptyRequest();
        }

        @Override
        public Batch batch(List<String> urls, List<String> locals, List<String> md5, BatchListener listener) {
            return new Batch() {
                @Override
                public void cancel() { }

                @Override
                public void detach() { }
            };
        }

        @Override
        public void submit(Request request) {

        }

        @Override
        public void queryUrl(String url, ResourceListener resourceListener) {

        }

        @Override
        public void cancel(String url) {

        }

        @Override
        public void cancelLocalPath(String localName) {

        }

        @Override
        public void cancelAll() {

        }

        @Override
        public void removeAllListeners() {

        }

        @Override
        public void removeListener(ResourceListener resourceListener) {

        }

        @Override
        public void removeListeners(Long id) {

        }

        @Override
        public void clearResource(ClearRequest clearRequest, ClearListener clearListener) {

        }

        @Override
        public void close() {

        }
    }

}
