package com.sloth.architecture.mvp;

import androidx.annotation.NonNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/5/12 15:19
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/5/12         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class ExclusiveObserver<T> implements Observer<T> {

    private final Observer<T> proxy;

    public ExclusiveObserver(Observer<T> proxy) {
        this.proxy = proxy;
    }

    @Override
    public void onSubscribe(@androidx.annotation.NonNull Disposable d) {
        proxy.onSubscribe(d);
    }

    @Override
    public void onNext(@androidx.annotation.NonNull T t) {
        proxy.onNext(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onExclusiveEnd();
        proxy.onError(e);
    }

    @Override
    public void onComplete() {
        onExclusiveEnd();
        proxy.onComplete();
    }

    public abstract void onExclusiveEnd();
}
