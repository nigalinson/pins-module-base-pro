package com.sloth.platform;

import android.graphics.Bitmap;
import android.net.Uri;
import java.util.List;

public interface PlayerComponent {

    interface RyPlayerListener {

        void onPlayerPrepared();

        void onPlayerBuffering();

        void onPlayerSizeChanged(int width, int height);

        void onPlayerEnd();

        void onPlayerError(int code, String msg);

    }

    void setListener(RyPlayerListener playerListener);

    void addListener(RyPlayerListener playerListener);

    void removeListener(RyPlayerListener playerListener);

    /**
     * fixme 获取视频封面使用了剪裁的ffmpeg库，在真机上出现过闪退，谨慎使用
     * @param localPath
     */
    void preView(String localPath);

    /**
     * 加载本地图片为封面
     *
     * @param coverImagePath 封面地址
     */
    void loadCover(String coverImagePath);

    /**
     * 加载打包素材作为封面
     *
     * @param coverImageResPath 素材ID
     */
    void loadCover(int coverImageResPath);

    /**
     * 加载bitmap为封面
     * @param coverBitmap
     */
    void loadCover(Bitmap coverBitmap);

    /**
     * 调整视窗大小适配给定的大小
     *
     * @param videoWidth  给定的宽
     * @param videoHeight 给定的高
     */
    void applyPlayerViewScaleType(int videoWidth, int videoHeight);

    void play(Uri uri);

    void play(String local);

    void play(String local, String online);

    void prepare(String local, String online);

    void reset();

    void start();

    void pause();

    void stop();

    void release();

    void clearSurface();

    void forward(boolean forward, int duration);

    boolean isLoop();

    void loop(boolean loop);

    void setScaleType(int scaleType);

    boolean isPrepared();

    boolean isPlaying();

    void seekTo(int position);

    int getCurrentPosition();

    void setVolume(float v);

    /**
     * 播放器核心配置
     * fixme 部分机型上使用多解码器换绑SurfaceView会出现绿屏等奇怪现象，需要根据机器测试情况酌情使用
     * @param codecCount 允许的解码器个数
     */
    void setCodecCount(int codecCount);

    /**
     * 使用额外的解码器预加载视频
     * 使用前请先开启
     * {@link PlayerComponent#setCodecCount}
     * 为
     * > 1
     *
     * @param localPaths
     */
    void prepareWithAdditionalCodecs(List<String> localPaths);

}
