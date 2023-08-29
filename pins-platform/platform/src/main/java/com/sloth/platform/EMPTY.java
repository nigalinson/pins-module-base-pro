package com.sloth.platform;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import com.sloth.functions.json.gson.GsonComponent;
import com.sloth.functions.log.logcat.LogcatComponent;

import java.util.List;

public class EMPTY {

    static final LogComponent LOG = new LogcatComponent();
    static final ImageLoaderComponent IMAGE = new EmptyImageLoader();
    static final PlayerComponent PLAYER = new EmptyPlayer();
    static final DownloadComponent DOWNLOADER = new EmptyDownloader();
    static final GsonComponent JSON = new GsonComponent();

    static final class EmptyImageLoader implements ImageLoaderComponent {

        static final class EmptyRq implements Rq {
            @Override
            public void cancel() { }
        }

        @Override
        public ImageLoaderComponent with(Context context) {
            return this;
        }

        @Override
        public ImageLoaderComponent load(String url) {
            return this;
        }

        @Override
        public ImageLoaderComponent load(int res) {
            return this;
        }

        @Override
        public ImageLoaderComponent loadLocal(String localPath) {
            return this;
        }

        @Override
        public ImageLoaderComponent load(Uri uri) {
            return this;
        }

        @Override
        public ImageLoaderComponent placeHolder(int resId) {
            return this;
        }

        @Override
        public ImageLoaderComponent error(int resId) {
            return this;
        }

        @Override
        public ImageLoaderComponent transition(Object transition) {
            return this;
        }

        @Override
        public ImageLoaderComponent transform(Object transform) {
            return this;
        }

        @Override
        public ImageLoaderComponent skipMemoryCache(boolean skipMemoryCache) {
            return this;
        }

        @Override
        public ImageLoaderComponent diskStrategy(Object diskStrategy) {
            return this;
        }

        @Override
        public ImageLoaderComponent apply(Object requestOptions) {
            return this;
        }

        @Override
        public <T> ImageLoaderComponent listener(LoadListener<T> loadListener) {
            return this;
        }

        @Override
        public Rq into(ImageView imageView) {
            return new EmptyRq();
        }

        @Override
        public <T> Rq into(LoadTarget<T> loadTarget) {
            return new EmptyRq();
        }
    }

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

}
