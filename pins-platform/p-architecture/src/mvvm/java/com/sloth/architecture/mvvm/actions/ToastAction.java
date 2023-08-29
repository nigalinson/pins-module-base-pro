package com.sloth.architecture.mvvm.actions;

import com.sloth.architecture.mvvm.VmAction;

public class ToastAction extends VmAction {
    private String data;

    public ToastAction(String data) {
        super(-1);
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
