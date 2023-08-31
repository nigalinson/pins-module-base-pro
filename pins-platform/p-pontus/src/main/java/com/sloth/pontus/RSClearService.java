package com.sloth.pontus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sloth.platform.Platform;

import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class RSClearService extends Service {

    private static final String TAG = RSClearService.class.getSimpleName();

    private Disposable loop;

    /**
     * 开启清理滞后时间
     * @return
     */
    protected long clearDelay(){
        return 60 * 1000;
    }

    /**
     * 清理间隔
     * @return
     */
    protected long clearInterval(){
        //默认12小时清理一次
        return 12 * 3600 * 1000;
    }

    /**
     * 跳过清理的组名
     * @return
     */
    protected String willNotClearGroup(){
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startClearLoop();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopClearLoop();
        super.onDestroy();
    }

    private void stopClearLoop(){
        if(loop != null && !loop.isDisposed()){
            loop.dispose();
            loop = null;
        }
    }

    private void startClearLoop(){
        stopClearLoop();
        Observable.interval(clearDelay()/*延迟1分钟开启清理*/, clearInterval(), TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        loop = d;
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        Platform.resourceManager().clearUntil(willNotClearGroup());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Platform.log().e(TAG, e.getMessage());
                    }

                    @Override
                    public void onComplete() { }
                });
    }

}
