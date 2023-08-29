package com.sloth.architecture.mvvm.actions;

public class IntVmAction extends TypeVmAction<Integer>{

    public IntVmAction(Integer data) {
        super(data);
    }

    public IntVmAction(int actionCode, Integer data) {
        super(actionCode, data);
    }

}
