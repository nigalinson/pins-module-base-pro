package com.sloth.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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
public class NativeTexturePlayer extends FrameLayout implements Player,
        TextureView.SurfaceTextureListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnVideoSizeChangedListener{

    private static final String TAG = NativeTexturePlayer.class.getSimpleName();

    private AppCompatImageView preView;

    private boolean loop = false;

    private PlayerListener mControllerListener;

    protected final AtomicBoolean playerPrepared = new AtomicBoolean(false);

    protected final AtomicBoolean surfacePrepared = new AtomicBoolean(false);

    protected final AtomicBoolean playWhenReady = new AtomicBoolean(false);

    private MediaPlayer mMediaPlayer;
    private TextureView mPreview;

    /**
     * ??????????????????????????????onPrepared????????????????????????100ms??????
     * ??????????????????????????????????????????????????????????????????onPrepared?????????????????????????????????????????????
     */
    private long closePreviewDelay = 100;

    //????????????????????????????????????????????????????????????????????????
    private String showingPreviewUrl;

    //??????surface?????????????????????
    private Object surfaceReference;

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

    public NativeTexturePlayer(@NonNull Context context) {
        this(context, null);
    }

    public NativeTexturePlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NativeTexturePlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setBackgroundColor(Color.BLACK);

        mPreview = new TextureView(context.getApplicationContext());
        mPreview.setSurfaceTextureListener(this);
        try {
            if(mMediaPlayer == null){
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setOnCompletionListener(completionProxy);
                mMediaPlayer.setOnPreparedListener(completionProxy);
                mMediaPlayer.setOnVideoSizeChangedListener(completionProxy);
                setUpListener();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        param.gravity = Gravity.CENTER;
        addView(mPreview, param);

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
                        applyVidRatio(vidWidth, vidHeight);
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
        if(mMediaPlayer != null){
            if(StringUtils.notEmpty(local) && FileUtils.isFileExists(local)){
                LogUtils.d(TAG, "?????????????????????" +local);
                this.playingUri = local;
                try {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(local);
                    mMediaPlayer.prepareAsync();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    triggerPlayerError(-1, e.getMessage());
                } catch (SecurityException e) {
                    e.printStackTrace();
                    triggerPlayerError(-1, e.getMessage());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    triggerPlayerError(-1, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    triggerPlayerError(-1, e.getMessage());
                }
                playWhenReady.set(true);
                detectPlay();
            }else if(StringUtils.notEmpty(online)){
                LogUtils.d(TAG, "?????????????????????" +online);
                this.playingUri = online;
                try {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(online);
                    mMediaPlayer.prepareAsync();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    triggerPlayerError(-1, e.getMessage());
                } catch (SecurityException e) {
                    e.printStackTrace();
                    triggerPlayerError(-1, e.getMessage());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    triggerPlayerError(-1, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    triggerPlayerError(-1, e.getMessage());
                }
                playWhenReady.set(true);
                detectPlay();
            }else{
                LogUtils.e(TAG, "????????????????????????");
                playWhenReady.set(false);
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

        if(mMediaPlayer != null){
            mMediaPlayer.start();
        }
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
    public void stop(){
        LogUtils.d(TAG, "????????????");
        playWhenReady.set(false);
        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }
            mMediaPlayer.stop();
        }
    }

    @Override
    public void resume() {
        if(mMediaPlayer != null){
            mMediaPlayer.start();
        }
    }

    @Override
    public void pause() {
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
        }
    }

    @Override
    public boolean isPlaying() {
        if(mMediaPlayer != null){
            mMediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(int dur) {
        if(mMediaPlayer != null){
            mMediaPlayer.seekTo(dur);
        }
    }

    @Override
    public int getCurrentPosition() {
        if(mMediaPlayer != null){
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void setVolume(float l, float r) {
        if(mMediaPlayer != null){
            mMediaPlayer.setVolume(l, r);
        }
    }

    @Override
    public void release() {
        completionProxy.remove(this);
        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
            }
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        surfaceReference = null;
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

    private void setUpListener() {
        completionProxy.addCompletionListener(this);
        completionProxy.addPrepareListener(this);
        completionProxy.addVideoSizeChangeListener(this);
        completionProxy.addErrorListener(this);
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
        if(mMediaPlayer == null){ return; }

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
            LogUtils.d(TAG, "surface:" + mPreview.getWidth() + "," + mPreview.getHeight());
            float surfaceRatio = containerRatio();
            float vidRatio = 1.0f * videoWidth / videoHeight;
            if(vidRatio > surfaceRatio){
                //?????????????????????????????????width???????????????
                int width = getWidth();
                int height = (int) (width / vidRatio);
                LayoutParams params = (LayoutParams) mPreview.getLayoutParams();
                params.width = width;
                params.height = height;
                mPreview.setLayoutParams(params);
            }else if(vidRatio < surfaceRatio){
                //?????????????????????????????????height???????????????
                int height = getHeight();
                int width = (int) (height * vidRatio);
                LayoutParams params = (LayoutParams) mPreview.getLayoutParams();
                params.width = width;
                params.height = height;
                mPreview.setLayoutParams(params);
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

    //=========================================player callback============================================
    //region player callback

    public static CompletionProxy completionProxy = new CompletionProxy();

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        LogUtils.i(TAG, "????????????");
        playerPrepared.set(true);
        detectPlay();
        mediaPlayer.setLooping(isLoop());

        mMediaPlayer.setOnInfoListener((mp, what, extra) -> {
            triggerPlayerPrepared();
            return false;
        });
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        LogUtils.i(TAG, "????????????");

        triggerPlayerEnd();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        triggerPlayerError(what, "video error");
        return false;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        LogUtils.i(TAG, "video size changed:" + width + "," + height);
        triggerPlayerSizeChanged(width, height);
        applyVidRatio(width, height);
    }

    //endregion player callback


    //=========================================texture============================================
    //region texture

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        LogUtils.i(TAG, "Texture??????");
        Surface surfaceHolder = new Surface(surface);
        surfaceReference = surfaceHolder;
        if(mMediaPlayer != null){
            mMediaPlayer.setSurface(surfaceHolder);
        }

        surfacePrepared.set(true);
        detectPlay();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        LogUtils.i(TAG, "Texture??????");
        if(mMediaPlayer != null){
            mMediaPlayer.setSurface(null);
        }
        surfacePrepared.set(false);
        //????????????
        if(mMediaPlayer != null){
            mMediaPlayer.pause();
            mMediaPlayer.stop();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) { }

    //endregion texture

    //=========================================listener============================================
    //region listener

    /**
     * ??????????????????
     */
    private static class CompletionProxy implements MediaPlayer.OnCompletionListener,
            MediaPlayer.OnPreparedListener,
            MediaPlayer.OnVideoSizeChangedListener,
            MediaPlayer.OnErrorListener {

        private final Set<MediaPlayer.OnCompletionListener> completionListeners = new HashSet<>();
        private final Set<MediaPlayer.OnPreparedListener> preparedListeners = new HashSet<>();
        private final Set<MediaPlayer.OnVideoSizeChangedListener> videoSizeChangedListeners = new HashSet<>();
        private final Set<MediaPlayer.OnErrorListener> errorListeners = new HashSet<>();

        public void addCompletionListener(MediaPlayer.OnCompletionListener tmp){
            completionListeners.add(tmp);
        }

        public void addPrepareListener(MediaPlayer.OnPreparedListener tmp){
            preparedListeners.add(tmp);
        }

        public void addVideoSizeChangeListener(MediaPlayer.OnVideoSizeChangedListener tmp){
            videoSizeChangedListeners.add(tmp);
        }

        public void addErrorListener(MediaPlayer.OnErrorListener tmp){
            errorListeners.add(tmp);
        }

        public void remove(Object lis){
            if(lis instanceof MediaPlayer.OnCompletionListener){
                completionListeners.remove(lis);
            }
            if(lis instanceof MediaPlayer.OnPreparedListener){
                preparedListeners.remove(lis);
            }
            if(lis instanceof MediaPlayer.OnVideoSizeChangedListener){
                videoSizeChangedListeners.remove(lis);
            }
            if(lis instanceof MediaPlayer.OnErrorListener){
                errorListeners.remove(lis);
            }
        }

        public void clear(){
            completionListeners.clear();
            preparedListeners.clear();
            videoSizeChangedListeners.clear();
            errorListeners.clear();
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            for(MediaPlayer.OnCompletionListener item: completionListeners){
                item.onCompletion(mp);
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            for(MediaPlayer.OnPreparedListener item: preparedListeners){
                item.onPrepared(mp);
            }
        }

        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            for(MediaPlayer.OnVideoSizeChangedListener item: videoSizeChangedListeners){
                item.onVideoSizeChanged(mp, width, height);
            }
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            for(MediaPlayer.OnErrorListener item: errorListeners){
                item.onError(mp, what, extra);
            }
            return false;
        }
    }

    //endregion listener

}
