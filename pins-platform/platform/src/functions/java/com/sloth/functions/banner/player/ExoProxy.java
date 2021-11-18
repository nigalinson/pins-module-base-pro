package com.sloth.functions.banner.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rongyi.common.R;
import com.rongyi.common.widget.player.PlayerConst;
import com.rongyi.common.widget.player.RyPlayer;
import com.rongyi.common.widget.player.RyPlayerListener;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/10 10:41
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/10         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ExoProxy extends PlayerProxy implements RyPlayerListener {

    private static final String TAG = ExoProxy.class.getSimpleName();

    private final RyPlayer ryPlayer;

    public ExoProxy(Context context, PlayerConfig playerConfig) {
        super(playerConfig);

        boolean surface = (playerConfig == null || playerConfig.getSurfaceType() == PlayerConst.SurfaceType.Surface.code);
        boolean fitXy = (playerConfig == null || playerConfig.getScaleType() == PlayerConst.ScaleType.FIT_XY.code);
        boolean autoScaleViewPort = (playerConfig != null && playerConfig.isAutoScaleViewPort());

        ryPlayer = (RyPlayer) LayoutInflater.from(context).inflate(R.layout.layout_ry_player, null);
        ryPlayer.setScaleType(fitXy ? PlayerConst.ScaleType.FIT_XY.code : PlayerConst.ScaleType.FIT_CENTER.code);
        ryPlayer.setSurfaceType(surface ? PlayerConst.SurfaceType.Surface.code : PlayerConst.SurfaceType.Texture.code);
        ryPlayer.setAutoScaleViewPort(autoScaleViewPort);
        ryPlayer.loop(playerConfig.isLoop());
        //手动初始化播放器
        ryPlayer.initStructures();

        ryPlayer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ryPlayer.setListener(this);
    }

    @Override
    public View attachingView() {
        return ryPlayer;
    }

    @Override
    public void preView(String localPath) {
        if(ryPlayer != null){
            ryPlayer.preView(localPath);
        }
    }

    @Override
    public void setVideoSize(int w, int h) {
        if(ryPlayer != null){
            ryPlayer.setVideoSize(w, h);
        }
    }

    @Override
    public void loop(boolean loop) {
        if(ryPlayer != null){
            ryPlayer.loop(loop);
        }
    }

    @Override
    public void seekTo(int dur) {
        if(ryPlayer != null){
            ryPlayer.seekTo(dur);
        }
    }

    @Override
    public void play(String localPath, String webUrl) {
        if(ryPlayer != null){
            ryPlayer.play(localPath, webUrl);
        }
    }

    @Override
    public void stop() {
        if(ryPlayer != null){
            ryPlayer.stop();
        }
    }

    @Override
    public void resume() {
        if(ryPlayer != null){
            ryPlayer.resume();
        }
    }

    @Override
    public void pause() {
        if(ryPlayer != null){
            ryPlayer.pause();
        }
    }

    @Override
    public void volume(float l, float r) {
        if(ryPlayer != null){
            ryPlayer.setVolume(l, r);
        }
    }

    @Override
    public void release() {
        if(ryPlayer != null){
            ryPlayer.release();
        }
    }

    @Override
    public long getCurrentPosition() {
        if(ryPlayer != null){
            return ryPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void forward(boolean forward, int dur) {
        if(ryPlayer != null){
            ryPlayer.forward(forward, dur);
        }
    }

    @Override
    public boolean isPlaying() {
        return ryPlayer != null && ryPlayer.isPlaying();
    }

    @Override
    public void onPlayerPrepared() {
        triggerPlayerPrepared();
    }

    @Override
    public void onPlayerSizeChanged(int width, int height) { }

    @Override
    public void onPlayerEnd() {
        triggerPlayerEnd();
    }

    @Override
    public void onPlayerError(int code, String msg) {
        triggerPlayerError(msg);
    }
}
