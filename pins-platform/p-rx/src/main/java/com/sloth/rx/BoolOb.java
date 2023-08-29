package com.sloth.rx;

import io.reactivex.annotations.NonNull;

public abstract class BoolOb extends Obx<Boolean> {

    @Override
    protected void onExe(Boolean aBoolean) {
        if(aBoolean){
            onTrue();
        }else{
            onFalse();
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        super.onError(e);
        onFalse();
    }

    protected void onTrue(){}

    protected void onFalse(){}
}
