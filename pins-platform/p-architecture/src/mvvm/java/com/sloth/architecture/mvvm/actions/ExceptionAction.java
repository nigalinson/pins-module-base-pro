package com.sloth.architecture.mvvm.actions;

import com.sloth.architecture.mvvm.VmAction;

public class ExceptionAction extends VmAction {

    private final Throwable throwable;

    public ExceptionAction(Throwable throwable) {
        super(-1);
        this.throwable = throwable;
    }

    public ExceptionAction(int actionCode, Throwable throwable) {
        super(actionCode);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}

