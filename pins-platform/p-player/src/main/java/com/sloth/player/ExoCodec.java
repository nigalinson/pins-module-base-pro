package com.sloth.player;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.video.VideoSize;
import com.sloth.platform.Platform;
import com.sloth.utils.StringUtils;

import java.io.File;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2023/4/21 15:03
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2023/4/21         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ExoCodec extends Codec<SimpleExoPlayer> implements Player.Listener {

    private static final String TAG = ExoCodec.class.getSimpleName();

    public ExoCodec(Context context) {
        super(context);
    }

    @Override
    protected SimpleExoPlayer initPlayerController(Context context) {
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context);
        renderersFactory.setEnableDecoderFallback(true);
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);
        SimpleExoPlayer mPlayer = new SimpleExoPlayer.Builder(context, renderersFactory).build();
        mPlayer.addListener(this);
        mPlayer.setRepeatMode(isLoop() ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
//        if(surfaceType == PlayerConst.SurfaceType.Surface.code){
//            mPlayer.setVideoSurfaceView(((SurfaceView)getPlayerView()));
//        }
        return mPlayer;
    }

    @Override
    protected void onSetupVideoUri(SimpleExoPlayer simpleExoPlayer, Uri uri) {
        simpleExoPlayer.stop(true);
        simpleExoPlayer.setMediaItem(MediaItem.fromUri(uri));
        simpleExoPlayer.prepare();
    }

    @Override
    protected void onSetupVideoUriLocal(SimpleExoPlayer simpleExoPlayer, String local) {
        simpleExoPlayer.stop(true);
        simpleExoPlayer.setMediaItem(MediaItem.fromUri(Uri.fromFile(new File(local))));
        simpleExoPlayer.prepare();
    }

    @Override
    protected void onBackendPlayer(SimpleExoPlayer simpleExoPlayer) {
        simpleExoPlayer.pause();
        simpleExoPlayer.seekTo(0);
    }

    @Override
    protected void onSetupVideoUriOnline(SimpleExoPlayer simpleExoPlayer, String online) {
        simpleExoPlayer.stop(true);
        simpleExoPlayer.setMediaItem(MediaItem.fromUri(Uri.parse(online)));
        simpleExoPlayer.prepare();
    }

    @Override
    protected void onEveryThingReady(SimpleExoPlayer simpleExoPlayer) {
        simpleExoPlayer.play();
    }

    @Override
    protected void onResetPlayer(SimpleExoPlayer simpleExoPlayer) {
        simpleExoPlayer.stop(true);
    }

    @Override
    protected void onStopPlayer(SimpleExoPlayer simpleExoPlayer) {
        simpleExoPlayer.pause();
        simpleExoPlayer.stop();
    }

    @Override
    protected void onReleasePlayer(SimpleExoPlayer simpleExoPlayer) {
        simpleExoPlayer.release();
    }

    @Override
    protected void onPlayerStart(SimpleExoPlayer simpleExoPlayer) {
        simpleExoPlayer.play();
    }

    @Override
    protected void onPlayerPause(SimpleExoPlayer simpleExoPlayer) {
        simpleExoPlayer.pause();
    }

    @Override
    protected boolean onGetPlaying(SimpleExoPlayer simpleExoPlayer) {
        return simpleExoPlayer.isPlaying();
    }

    @Override
    protected void onSeekTo(SimpleExoPlayer simpleExoPlayer, int dur) {
        simpleExoPlayer.seekTo(dur);
    }

    @Override
    protected int onGetCurrentPosition(SimpleExoPlayer simpleExoPlayer) {
        return (int) simpleExoPlayer.getCurrentPosition();
    }

    @Override
    protected void onSetVolume(SimpleExoPlayer simpleExoPlayer, float volume) {
        simpleExoPlayer.setVolume(volume);
    }

    @Override
    protected void onSetLooping(SimpleExoPlayer simpleExoPlayer, boolean loop) {
        simpleExoPlayer.setRepeatMode(loop ? Player.REPEAT_MODE_ALL : Player.REPEAT_MODE_OFF);
    }

    @Override
    protected void onBindSurface(SimpleExoPlayer simpleExoPlayer, Surface surface) {
        if(surface != null){
            simpleExoPlayer.setVideoSurface(surface);
        }else{
            simpleExoPlayer.clearVideoSurface();
        }
    }

    @Override
    public void onVideoSizeChanged(VideoSize videoSize) {
        triggerVideoSizeChanged(videoSize.width, videoSize.height);
    }

    @Override
    public void onPlaybackStateChanged(int state) {
        if(state == Player.STATE_READY){
            Platform.log().i(TAG, "视频就绪");
            triggerPlayerPrepared();
        }else if(state == Player.STATE_BUFFERING){
            Platform.log().i(TAG, "视频解码中");
            triggerPlayerBuffering();
        }else if(state == Player.STATE_ENDED){
            triggerPlayerEnd();
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        triggerPlayerError(
                error.type,
                StringUtils.notEmpty(error.getMessage()) ? error.getMessage() : "暂无报错信息"
        );
    }

}
