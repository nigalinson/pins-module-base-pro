package com.sloth.architecture.mvvm.actions;

import com.sloth.architecture.mvvm.VmAction;

public class NetErrorAction extends VmAction {

    private final Throwable throwable;

    public NetErrorAction(Throwable throwable) {
        super(-1);
        this.throwable = throwable;
    }

    public NetErrorAction(int actionCode, Throwable throwable) {
        super(actionCode);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}

