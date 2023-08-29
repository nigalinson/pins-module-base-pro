package com.sloth.rx;

import androidx.lifecycle.MutableLiveData;

public class LiveDataOb<T> extends Obx<T> {

    private final MutableLiveData<T> proxy;

    public LiveDataOb(MutableLiveData<T> proxy) {
        this.proxy = proxy;
    }

    @Override
    protected void onExe(T t) {
        proxy.setValue(t);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        proxy.setValue(null);
    }
}
