package com.sloth.player;

import android.content.Context;
import android.net.Uri;
import android.view.Surface;
import com.annimon.stream.Stream;
import com.sloth.utils.FileUtils;
import com.sloth.platform.Platform;
import com.sloth.utils.MediaUtils;
import com.sloth.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import com.sloth.platform.PlayerComponent.RyPlayerListener;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2023/4/21 14:25
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2023/4/21         Carl            1.0                    1.0
 * Why & What is modified:
 * 纯播放器核心(不包含View)
 */
abstract class Codec<PlayerController> {

    private static final String TAG = Codec.class.getSimpleName();

    private final PlayerController playerController;

    private boolean loop = false;
    private float volume = 1.0f;
    private final List<RyPlayerListener> mControllerListenerList;

    //播放中的地址
    private String playingUri;

    /**
     * 播放器是否就绪
     */
    private final AtomicBoolean playerPrepared = new AtomicBoolean(false);
    /**
     * 就绪状态下是否开始播放
     */
    private final AtomicBoolean playWhenReady = new AtomicBoolean(false);
    /**
     * 解码器是否在运行——所有东西prepare并真实start后置为true；调用stop或surface die后，置为false
     * 非解码状态下，调用stop会报错，因此需要添加此状态来规避异常状态调用
     */
    private final AtomicBoolean isCodecWorking = new AtomicBoolean(false);
    /**
     * 播放页面是否就绪
     */
    private final AtomicBoolean surfacePrepared = new AtomicBoolean(false);

    public Codec(Context context) {
        playerController = initPlayerController(context);
        mControllerListenerList = new ArrayList<>();
    }

    protected abstract PlayerController initPlayerController(Context context);

    protected PlayerController getPlayer(){
        return playerController;
    }

    public void bindSurface(Surface surface){
        if(getPlayer() == null) return;
        if(surface != null){
            Platform.log().i(TAG, "bind Surface");
            setSurfacePrepared(true);
            onBindSurface(getPlayer(), surface);
            detectPlay();
        }else{
            Platform.log().i(TAG, "unbind Surface");
            setSurfacePrepared(false);
            pause();
            onBindSurface(getPlayer(), null);
        }
    }

    public void play(Uri uri) {
        if(getPlayer() == null) return;
        Platform.log().d(TAG, "链接：uri:" + uri.toString());
        String local = uri.toString();
        setPlayWhenReady(true);
        if(playingUri != null && playingUri.equals(local)){
            //prepared和url一致，直接尝试播放
            seekTo(0);
            detectPlay();
        }else{
            //换节目
            if(isPlaying()){
                reset();
            }
            setPlayerPrepared(false);
            if(StringUtils.notEmpty(local)){
                Platform.log().d(TAG, "置入播放链接！" +local);
                this.playingUri = local;
                onSetupVideoUri(getPlayer(), uri);
                detectPlay();
            }else{
                Platform.log().e(TAG, "无有效播放地址！");
            }
        }
    }

    public void play(String local) {
        play(local, null);
    }

    public void play(String local, String online) {
        if(getPlayer() == null) return;
        Platform.log().d(TAG, "play --> 链接：local:" + local + ", online:" + online);
        setPlayWhenReady(true);
        //已在播放中
        if(playingUri != null && (playingUri.equals(local) || playingUri.equals(online))){
            //prepared和url一致，直接尝试播放
            seekTo(0);
            detectPlay();
        }else{
            //换节目
            if(isPlaying()){
                reset();
            }
            setPlayerPrepared(false);
            if(StringUtils.notEmpty(local) && FileUtils.isFileExists(local)){
                Platform.log().d(TAG, "置入播放链接！" +local);
                this.playingUri = local;
                onSetupVideoUriLocal(getPlayer(), local);
                detectPlay();
            }else if(StringUtils.notEmpty(online)){
                Platform.log().d(TAG, "置入播放链接！" +online);
                this.playingUri = online;
                onSetupVideoUriOnline(getPlayer(), online);
                detectPlay();
            }else{
                Platform.log().e(TAG, "无有效播放地址！");
            }
        }
    }

    public void prepare(String local, String online) {
        if(getPlayer() == null) return;
        Platform.log().d(TAG, "prepare --> 链接：local:" + local + ", online:" + online);
        setPlayWhenReady(false);
        //已在播放中
        if(playingUri != null && (playingUri.equals(local) || playingUri.equals(online))){
            //同一个地址
            seekTo(0);
            pause();
        }else{
            //换节目
            if(isPlaying()){
                reset();
            }
            setPlayerPrepared(false);
            if(StringUtils.notEmpty(local) && FileUtils.isFileExists(local)){
                Platform.log().d(TAG, "置入播放链接！" +local);
                this.playingUri = local;
                onSetupVideoUriLocal(getPlayer(), local);
                detectPlay();
            }else if(StringUtils.notEmpty(online)){
                Platform.log().d(TAG, "置入播放链接！" +online);
                this.playingUri = online;
                onSetupVideoUriOnline(getPlayer(), online);
                detectPlay();
            }else{
                Platform.log().e(TAG, "无有效播放地址！");
            }
        }
    }

    public String getPlayingUri(){
        return playingUri;
    }

    public void reset() {
        if(getPlayer() == null) return;
        onResetPlayer(getPlayer());
    }

    public void start() {
        if(getPlayer() == null) return;
        Platform.log().d(TAG, "开启播放");
        setPlayWhenReady(true);
        detectPlay();
    }

    public void pause() {
        if(getPlayer() == null) return;
        if(isPlayerPrepared() && isSurfacePrepared() && isPlaying()){
            onPlayerPause(getPlayer());
        }
    }

    public void stop(){
        if(getPlayer() == null) return;
        Platform.log().d(TAG, "停止播放");
        playingUri = null;
        if(isCodecWorking()){
            onStopPlayer(getPlayer());
        }
        setCodecWorking(false);
        setPlayWhenReady(false);
        setPlayerPrepared(false);
    }

    public void release() {
        if(getPlayer() != null){
            onReleasePlayer(getPlayer());
        }
        setCodecWorking(false);
        setPlayWhenReady(false);
        setPlayerPrepared(false);
    }

    public boolean isPlaying() {
        if(getPlayer() == null) return false;
        if(!isPlayerPrepared() || !isSurfacePrepared() || !isCodecWorking()){
            return false;
        }
        return onGetPlaying(getPlayer());
    }

    public boolean isPrepared() {
        return playerPrepared.get();
    }

    public void seekTo(int dur) {
        if(getPlayer() == null) return;
        if(isPlayerPrepared()){
            onSeekTo(getPlayer(), dur);
        }
    }
    public int getCurrentPosition() {
        if(getPlayer() == null) return 0;
        if(!isPlaying()){
            return 0;
        }
        return onGetCurrentPosition(getPlayer());
    }

    public void setVolume(float v) {
        if(getPlayer() == null) return;
        this.volume = v;
        if(isPlayerPrepared()){
            onSetVolume(getPlayer(), volume);
        }
    }

    public void forward(boolean forward, int duration) {
        if(null == playingUri || "".equals(playingUri)){ return; }
        long total = MediaUtils.getMediaDuration(playingUri);
        long now = getCurrentPosition();
        now = (forward ? now + duration : now - duration);
        now = Math.max(0, now);
        if(now > total){
            now = 0;
        }
        seekTo((int)now);
    }

    public void setListener(RyPlayerListener controllerListener){
        mControllerListenerList.clear();

        if(controllerListener == null) return;
        mControllerListenerList.add(controllerListener);
    }

    public void addListener(RyPlayerListener controllerListener){

        if(controllerListener == null) return;

        if(!mControllerListenerList.contains(controllerListener)){
            mControllerListenerList.add(controllerListener);
        }
    }

    public void removeListener(RyPlayerListener controllerListener){
        if(controllerListener == null) return;

        int index = mControllerListenerList.indexOf(controllerListener);
        if(index != -1){
            mControllerListenerList.remove(index);
        }
    }

    public void loop(boolean loop) {
        this.loop = loop;
        if(isPlayerPrepared()){
            onSetLooping(getPlayer(), loop);
        }
    }

    public boolean isLoop(){
        return loop;
    }

    private void detectPlay(){
        if(!isPlayerPrepared()){
            Platform.log().e(TAG, "播放器暂未prepared");
            return;
        }

        if(!isSurfacePrepared()){
            Platform.log().e(TAG, "surface暂未就绪");
            return;
        }

        if(!isPlayWhenReady()){
            Platform.log().e(TAG, "一切就绪，但用户未触发播放");
            if(isPlaying()){
                //因为状态为不允许播放，如果发现在播放中，顺便暂停播放
                setCodecWorking(true);
                pause();
            }
            return;
        }

        if(isPlaying()){
            Platform.log().e(TAG, "已经在播放中");
            setCodecWorking(true);
            return;
        }

        setCodecWorking(true);
        if(getPlayer() != null){
            onEveryThingReady(getPlayer());
        }
    }

    protected void setPlayerPrepared(boolean bol){
        playerPrepared.set(bol);
    }

    protected boolean isPlayerPrepared(){
        return playerPrepared.get();
    }

    protected void setSurfacePrepared(boolean bol){
        surfacePrepared.set(bol);
    }

    protected boolean isSurfacePrepared(){
        return surfacePrepared.get();
    }

    protected void setPlayWhenReady(boolean bol){
        playWhenReady.set(bol);
    }

    protected boolean isPlayWhenReady(){
        return playWhenReady.get();
    }

    private void setCodecWorking(boolean bol){
        isCodecWorking.set(bol);
    }

    private boolean isCodecWorking(){
        return isCodecWorking.get();
    }

    protected abstract void onSetupVideoUri(PlayerController playerController, Uri uri);

    protected abstract void onSetupVideoUriOnline(PlayerController playerController,  String online);

    protected abstract void onSetupVideoUriLocal(PlayerController playerController,  String local);

    protected abstract void onBackendPlayer(PlayerController playerController);

    protected abstract void onEveryThingReady(PlayerController playerController);

    protected abstract void onResetPlayer(PlayerController playerController);

    protected abstract void onStopPlayer(PlayerController playerController);

    protected abstract void onReleasePlayer(PlayerController playerController);

    protected abstract void onPlayerStart(PlayerController playerController);

    protected abstract void onPlayerPause(PlayerController playerController);

    protected abstract boolean onGetPlaying(PlayerController playerController);

    protected abstract void onSeekTo(PlayerController playerController,  int dur);

    protected abstract int onGetCurrentPosition(PlayerController playerController);

    protected abstract void onSetVolume(PlayerController playerController,  float volume);

    protected abstract void onSetLooping(PlayerController playerController,  boolean loop);

    protected abstract void onBindSurface(PlayerController playerController, Surface surface);

    protected void triggerPlayerPrepared(){
        Platform.log().i(TAG, "视频就绪");
        setPlayerPrepared(true);

        if(getPlayer() != null){
            onSetLooping(getPlayer(), isLoop());
            onSetVolume(getPlayer(), volume);
        }

        detectPlay();

        Stream.of(mControllerListenerList).forEach(RyPlayerListener::onPlayerPrepared);
    }

    protected void triggerPlayerBuffering(){
        Stream.of(mControllerListenerList).forEach(RyPlayerListener::onPlayerBuffering);
    }

    protected void triggerVideoSizeChanged(int w, int h){
        Platform.log().d(TAG, "onVideoSizeChanged:" + w + "," + h);
        Stream.of(mControllerListenerList).forEach(lis-> lis.onPlayerSizeChanged(w, h));
    }

    protected void triggerPlayerEnd(){
        Platform.log().i(TAG, "播放结束");
        setPlayerPrepared(false);
        setCodecWorking(false);
        setPlayWhenReady(false);
        playingUri = null;
        Stream.of(mControllerListenerList).forEach(RyPlayerListener::onPlayerEnd);
    }

    protected void triggerPlayerError(int errCode, String errMsg){
        setPlayerPrepared(false);
        setCodecWorking(false);
        Stream.of(mControllerListenerList).forEach(lis->lis.onPlayerError(errCode, errMsg));
    }

}
