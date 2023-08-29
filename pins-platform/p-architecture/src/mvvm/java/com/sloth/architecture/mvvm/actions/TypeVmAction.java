package com.sloth.architecture.mvvm.actions;

import com.sloth.architecture.mvvm.VmAction;

public class TypeVmAction<T> extends VmAction {
    private T data;

    public TypeVmAction(T data) {
        super(-1);
        this.data = data;
    }

    public TypeVmAction(int actionCode, T data) {
        super(actionCode);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
