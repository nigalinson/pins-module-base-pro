package com.sloth.architecture.mvvm;


public class VmAction {
    private int actionCode;

    public VmAction() {
        this.actionCode = -1;
    }

    public VmAction(int actionCode) {
        this.actionCode = actionCode;
    }

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }
}
