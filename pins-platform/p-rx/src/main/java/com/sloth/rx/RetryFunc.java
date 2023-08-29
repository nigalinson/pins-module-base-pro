package com.sloth.rx;

import com.sloth.platform.Platform;

import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

public class RetryFunc implements Function<Observable<? extends Throwable>, Observable<?>> {
    private final int mMaxRetries;//重试次数
    private final int mRetryDelayMillis;//每次重试延时时间
    private int mRetryCount;//当前重试次数

    public RetryFunc(int maxRetries, int retryDelayMillis) {
        mMaxRetries = maxRetries;
        mRetryDelayMillis = retryDelayMillis;
    }

    @Override
    public Observable<?> apply(@NonNull Observable<? extends Throwable> observable) throws Exception {
        return observable.flatMap((Function<Throwable, ObservableSource<?>>) throwable -> {
            Throwable e = throwable;
            while (e.getCause() != null) {
                throwable = e;
                e = e.getCause();
            }
            if (++mRetryCount <= mMaxRetries) {
                Platform.log().d("RetryWithDelay", "服务器超时错误,将在 " + mRetryDelayMillis + " 秒之后重试, 当前重试次数: " + mRetryCount);
                return Observable.timer(mRetryDelayMillis, TimeUnit.MILLISECONDS);
            }
            return Observable.error(throwable);
        });
    }
}
