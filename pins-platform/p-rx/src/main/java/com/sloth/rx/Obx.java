package com.sloth.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class Obx<T> implements Observer<T> {

    private Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(T t) {
        onExe(t);
    }

    protected void onExe(T t) { }

    @Override
    public void onError(Throwable e) {
        if (this.disposable != null) {
            this.onUnSubscribe(this.disposable);
            this.disposable = null;
        }
    }

    @Override
    public void onComplete() {
        if(disposable != null){
            onUnSubscribe(disposable);
            disposable = null;
        }
    }

    protected void onUnSubscribe(Disposable disposable) { }

}

