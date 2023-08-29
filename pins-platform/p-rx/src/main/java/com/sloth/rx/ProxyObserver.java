package com.sloth.rx;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class ProxyObserver<T> extends Obx<T> {

    private final Observer<T> real;

    public ProxyObserver(Observer<T> real) {
        this.real = real;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if(real != null){
            real.onSubscribe(d);
        }
        super.onSubscribe(d);
    }

    @Override
    public void onNext(T t) {
        if(real != null){
            real.onNext(t);
        }
        super.onNext(t);
    }

    @Override
    public void onError(Throwable throwable) {
        if(real != null){
            real.onError(throwable);
        }
        super.onError(throwable);
    }

    @Override
    public void onComplete() {
        if(real != null){
            real.onComplete();
        }
        super.onComplete();
    }
}
