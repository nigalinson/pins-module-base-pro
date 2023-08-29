package com.sloth.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.view.Surface;

import com.sloth.platform.Platform;
import com.sloth.utils.Utils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2023/4/21 15:17
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2023/4/21         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class NativeCodec extends Codec<MediaPlayer> implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnVideoSizeChangedListener{

    private static final String TAG = NativeCodec.class.getSimpleName();

    private CompletionProxy completionProxy;

    public NativeCodec(Context context) {
        super(context);
    }

    @Override
    protected MediaPlayer initPlayerController(Context context) {
        try {
            completionProxy = new CompletionProxy();
            MediaPlayer mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setOnCompletionListener(completionProxy);
            mMediaPlayer.setOnPreparedListener(completionProxy);
            mMediaPlayer.setOnVideoSizeChangedListener(completionProxy);
            setUpListener();
            return mMediaPlayer;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onSetupVideoUri(MediaPlayer mediaPlayer, Uri uri) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(Utils.getApp(), uri);
            mediaPlayer.prepareAsync();
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
    }

    @Override
    protected void onSetupVideoUriLocal(MediaPlayer mediaPlayer, String local) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(local);
            mediaPlayer.prepareAsync();
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

    }

    @Override
    protected void onBackendPlayer(MediaPlayer mediaPlayer) {
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
    }

    @Override
    protected void onSetupVideoUriOnline(MediaPlayer mediaPlayer, String online) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(online);
            mediaPlayer.prepareAsync();
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
    }

    @Override
    protected void onEveryThingReady(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    protected void onResetPlayer( MediaPlayer mediaPlayer) {
        mediaPlayer.reset();
    }

    @Override
    protected void onStopPlayer(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
    }

    @Override
    protected void onReleasePlayer(MediaPlayer mediaPlayer) {
        completionProxy.remove(this);
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    @Override
    protected void onPlayerStart(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    protected void onPlayerPause( MediaPlayer mediaPlayer) {
        mediaPlayer.pause();
    }

    @Override
    protected boolean onGetPlaying(MediaPlayer mediaPlayer) {
        return mediaPlayer.isPlaying();
    }

    @Override
    protected void onSeekTo(MediaPlayer mediaPlayer, int dur) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaPlayer.seekTo(dur, MediaPlayer.SEEK_CLOSEST);
        }else{
            mediaPlayer.seekTo(dur);
        }
    }

    @Override
    protected int onGetCurrentPosition(MediaPlayer mediaPlayer) {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    protected void onSetVolume(MediaPlayer mediaPlayer, float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    @Override
    protected void onSetLooping(MediaPlayer mediaPlayer, boolean loop) {
        mediaPlayer.setLooping(loop);
    }

    @Override
    protected void onBindSurface(MediaPlayer mediaPlayer, Surface surface) {
        mediaPlayer.setSurface(surface);
    }

    private void setUpListener() {
        completionProxy.addCompletionListener(this);
        completionProxy.addPrepareListener(this);
        completionProxy.addVideoSizeChangeListener(this);
        completionProxy.addErrorListener(this);
    }


    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Platform.log().i(TAG, "video size changed:" + width + "," + height);
        triggerVideoSizeChanged(width, height);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        triggerPlayerPrepared();
        mediaPlayer.setOnInfoListener((mp, what, extra) -> {
            if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                triggerPlayerBuffering();
            }
            return false;
        });
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        triggerPlayerEnd();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        triggerPlayerError(what, "video error");
        return true;
    }


    /**
     * 防止内存泄漏
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
            return true;
        }
    }

}
