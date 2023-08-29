package com.sloth.rx;

public abstract class DAUOb<T> extends Obx<T> {

    private boolean isDefault = true;

    @Override
    protected void onExe(T t) {
        if(isDefault){
            isDefault = false;
            onDefault(t);
        }else{
            onUpdate(t);
        }
    }

    public abstract void onDefault(T data);

    public abstract void onUpdate(T data);

}
