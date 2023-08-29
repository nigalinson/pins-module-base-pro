package com.sloth.rx;

import com.sloth.platform.Platform;

import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class RepeatFunc<T> implements Function<Observable<T>, Observable<?>> {
    private final int mMaxRetries;//重试次数
    private final int mRetryDelayMillis;//每次重试延时时间
    private int mRetryCount;//当前重试次数

    public RepeatFunc(int maxRetries, int retryDelayMillis) {
        mMaxRetries = maxRetries;
        mRetryDelayMillis = retryDelayMillis;
    }

    @Override
    public Observable<?> apply(@NonNull Observable<T> observable) {
        return observable.flatMap((Function<T, ObservableSource<?>>) origin -> {

            if (++mRetryCount <= mMaxRetries) {
                Platform.log().d("RepeatWithDelay", "正在重复执行： " + mRetryDelayMillis + " 秒之后执行, 当前重复次数: " + mRetryCount);
                return Observable.timer(mRetryDelayMillis, TimeUnit.MILLISECONDS);
            }
            return Observable.empty();
        });
    }
}
