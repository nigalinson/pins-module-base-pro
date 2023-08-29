package com.sloth.architecture.mvvm.actions;

public class BoolVmAction extends TypeVmAction<Boolean>{

    public BoolVmAction(Boolean data) {
        super(data);
    }

    public BoolVmAction(int actionCode, Boolean data) {
        super(actionCode, data);
    }

}
