package com.sloth.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.sloth.functions.snapshot.SnapshotManager;
import com.sloth.pinsplatform.player.Player;
import com.sloth.pinsplatform.player.PlayerListener;
import com.sloth.tools.util.FileUtils;
import com.sloth.tools.util.LogUtils;
import com.sloth.tools.util.MediaUtils;
import com.sloth.tools.util.ViewUtils;
import com.sloth.tools.util.StringUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/14 10:58
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/14         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class NativeSurfacePlayer extends FrameLayout implements Player,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private static final String TAG = NativeSurfacePlayer.class.getSimpleName();

    private AppCompatImageView preView;

    private boolean loop = false;

    private PlayerListener mControllerListener;

    protected final AtomicBoolean playerPrepared = new AtomicBoolean(false);

    protected final AtomicBoolean surfacePrepared = new AtomicBoolean(false);

    protected final AtomicBoolean playWhenReady = new AtomicBoolean(false);
    /**
     * ??????????????????????????????onPrepared????????????????????????100ms??????
     * ??????????????????????????????????????????????????????????????????onPrepared?????????????????????????????????????????????
     */
    private long closePreviewDelay = 100;

    private VideoView videoView;

    //????????????????????????????????????????????????????????????????????????
    private String showingPreviewUrl;

    //?????????????????????
    private int scaleType;

    //????????????????????????????????????????????????????????????
    private boolean autoScaleViewPort;

    //??????????????????
    private String playingUri;

    public int getScaleType() {
        return scaleType;
    }

    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    public boolean isAutoScaleViewPort() {
        return autoScaleViewPort;
    }

    public void setAutoScaleViewPort(boolean b){
        this.autoScaleViewPort = b;
    }

    public void setClosePreviewDelay(long closePreviewDelay) {
        this.closePreviewDelay = closePreviewDelay;
    }

    public NativeSurfacePlayer(@NonNull Context context) {
        this(context, null);
    }

    public NativeSurfacePlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NativeSurfacePlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setBackgroundColor(Color.BLACK);

        this.videoView = new FixedVideoView(context);

        this.videoView.setOnPreparedListener(this);
        this.videoView.setOnCompletionListener(this);
        this.videoView.setOnErrorListener(this);
//        this.videoView.setZOrderOnTop(true);
//        this.videoView.setZOrderMediaOverlay(true);
        this.videoView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//        this.videoView.requestFocus();
        this.videoView.setBackgroundColor(Color.TRANSPARENT);

        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        param.gravity = Gravity.CENTER;
        addView(videoView, param);

        preView = new AppCompatImageView(context);
        preView.setScaleType(ImageView.ScaleType.FIT_XY);
        preView.setAlpha(0.99f);
        addView(preView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    }

    @Override
    public void setListener(PlayerListener controllerListener){
        this.mControllerListener = controllerListener;
    }

    @Override
    public void loop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop(){
        return loop;
    }

    @Override
    public void preView(String localPath) {
        if(StringUtils.notEmpty(localPath)){
            LogUtils.d(TAG, "??????????????????" + localPath);
            //???????????????
            if(preView.getVisibility() != View.VISIBLE){
                preView.setVisibility(View.VISIBLE);
            }
            if(!localPath.equals(showingPreviewUrl)){
                SnapshotManager.instance().snapshot(localPath).subscribe(new DisposableObserver<Bitmap>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Bitmap bitmap) {
                        applyVidRatio(bitmap.getWidth(), bitmap.getHeight());
                        showingPreviewUrl = localPath;
                        preView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        LogUtils.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() { }
                });
            }

        }else{
            //???????????????
            LogUtils.d(TAG, "?????????????????????");
            ViewUtils.releaseImageViewResource(preView);
            showingPreviewUrl = null;
            preView.setImageBitmap(null);
        }
    }

    @Override
    public void setVideoSize(int w, int h) {
        applyVidRatio(w, h);
    }

    @Override
    public void play(String local) {
        play(local, null);
    }

    @Override
    public void play(String local, String online) {
        LogUtils.d(TAG, "?????????local:" + local + ", online:" + online);
        if(videoView != null){
            if(StringUtils.notEmpty(local) && FileUtils.isFileExists(local)){
                LogUtils.d(TAG, "?????????????????????" +local);
                this.playingUri = local;
                videoView.setVideoPath(local);
                playWhenReady.set(true);
                detectPlay();
            }else if(StringUtils.notEmpty(online)){
                LogUtils.d(TAG, "?????????????????????" +online);
                this.playingUri = online;
                videoView.setVideoPath(online);
                playWhenReady.set(true);
                detectPlay();
            }else{
                LogUtils.e(TAG, "????????????????????????");
            }
        }
    }

    private void detectPlay(){
        if(!playerPrepared.get()){
            LogUtils.e(TAG, "???????????????prepared");
            return;
        }

        if(!surfacePrepared.get()){
            LogUtils.e(TAG, "surface????????????");
            return;
        }

        if(!playWhenReady.get()){
            LogUtils.e(TAG, "???????????????????????????????????????");
            if(isPlaying()){
                //??????????????????????????????????????????????????????????????????????????????
                stop();
            }
            return;
        }

        if(isPlaying()){
            LogUtils.e(TAG, "??????????????????");
            return;
        }

        if(videoView != null){
            videoView.start();
        }
    }

    @Override
    public void stop(){
        LogUtils.d(TAG, "????????????");
        playWhenReady.set(false);
        if(videoView != null) {
            videoView.pause();
        }
    }

    @Override
    public void resume() {
        if(videoView != null){
            videoView.resume();
        }
    }

    @Override
    public void pause() {
        if(videoView != null){
            videoView.pause();
        }
    }

    @Override
    public boolean isPlaying() {
        if(videoView != null){
            videoView.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(int dur) {
        if(videoView != null){
            videoView.seekTo(dur);
        }
    }

    @Override
    public int getCurrentPosition() {
        if(videoView != null){
            return (int) videoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void setVolume(float l, float r) {
        LogUtils.e(TAG, "videoView????????????????????????");
    }

    @Override
    public void forward(boolean forward, int duration) {
        if(null == playingUri || "".equals(playingUri)){ return; }
        long total = MediaUtils.getDefaultEngine().duration(playingUri);
        long now = getCurrentPosition();
        now = (forward ? now + duration : now - duration);
        now = Math.max(0, now);
        if(now > total){
            now = 0;
        }
        seekTo((int)now);
    }

    @Override
    public void release() {
        if(videoView != null){
            videoView.stopPlayback();
            videoView.setOnCompletionListener(null);
            videoView.setOnPreparedListener(null);
            videoView.setOnErrorListener(null);
        }
    }

    private void triggerPlayerPrepared(){
        if(preView.getVisibility() == View.VISIBLE){
            preView.setVisibility(View.GONE);
        }
        if(mControllerListener != null){
            mControllerListener.onPlayerPrepared();
        }
    }

    private void triggerPlayerSizeChanged(int w, int h){
        if(mControllerListener != null){
            mControllerListener.onPlayerSizeChanged(w, h);
        }
    }

    private void triggerPlayerEnd(){
        if(mControllerListener != null){
            mControllerListener.onPlayerEnd();
        }
    }

    private void triggerPlayerError(int errCode, String errMsg){
        if(mControllerListener != null){
            mControllerListener.onPlayerError(errCode, errMsg);
        }
    }

    private int vidWidth = -1, vidHeight = -1;
    private Disposable applyRatioDispose;
    private void applyVidRatio(int videoWidth, int videoHeight) {
        if(videoWidth == -1 || videoHeight == -1){ return; }
        if(videoWidth == getWidth() && videoHeight == getHeight()){ return; }
        this.vidWidth = videoWidth;
        this.vidHeight = videoHeight;

        scheduleApplyRatioTask(videoWidth, videoHeight);
    }

    private void scheduleApplyRatioTask(int videoWidth, int videoHeight) {
        //????????????????????????
        if(videoView == null){ return; }

        if(autoScaleViewPort){
            //???????????????????????????????????????????????????????????????????????????????????????????????????
            cancelApplyRatioTask();
            applyRatioDispose = Observable.timer(200, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> realApplyVideoSize(videoWidth, videoHeight));
        }else{
            realApplyVideoSize(videoWidth, videoHeight);
        }
    }

    private void realApplyVideoSize(int videoWidth, int videoHeight) {
        LogUtils.d(TAG, "onVideoSizeChanged:" + videoWidth + "," + videoHeight);
        if(scaleType == PlayerConst.ScaleType.FIT_CENTER.code){
            //????????????
            LogUtils.d(TAG, "surface:" + videoView.getWidth() + "," + videoView.getHeight());
            float surfaceRatio = containerRatio();
            float vidRatio = 1.0f * videoWidth / videoHeight;
            if(vidRatio > surfaceRatio){
                //?????????????????????????????????width???????????????
                int width = getWidth();
                int height = (int) (width / vidRatio);
                LayoutParams params = (LayoutParams) videoView.getLayoutParams();
                params.width = width;
                params.height = height;
                videoView.setLayoutParams(params);
            }else if(vidRatio < surfaceRatio){
                //?????????????????????????????????height???????????????
                int height = getHeight();
                int width = (int) (height * vidRatio);
                LayoutParams params = (LayoutParams) videoView.getLayoutParams();
                params.width = width;
                params.height = height;
                videoView.setLayoutParams(params);
            }else{
                //????????????????????????????????????
            }
        }else if(scaleType == PlayerConst.ScaleType.FIT_XY.code){
            //????????????????????????????????????
        }
    }

    private void cancelApplyRatioTask() {
        if(applyRatioDispose != null && !applyRatioDispose.isDisposed()){
            applyRatioDispose.dispose();
        }
    }

    private float containerRatio() {
        return 1.0f * getWidth() / getHeight();
    }

    //=========================================View callback============================================
    //region View callback

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(autoScaleViewPort){
            applyVidRatio(vidWidth, vidHeight);
        }
    }

    //endregion View callback

    //=========================================player callback============================================
    //region player callback

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        LogUtils.i(TAG, "????????????");
        playerPrepared.set(true);
        surfacePrepared.set(true);
        detectPlay();
        mediaPlayer.setLooping(isLoop());
        applyVidRatio(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());

        videoView.setOnInfoListener((mp, what, extra) -> {
            triggerPlayerPrepared();
            return false;
        });
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        LogUtils.i(TAG, "????????????");

//        if(isLoop()){
//            play(localSource, onlineSource);
//        }

        triggerPlayerEnd();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        triggerPlayerError(what, "video error");
        return false;
    }

    //endregion player callback

}
