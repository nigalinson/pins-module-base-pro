package com.sloth.architecture.mvvm.actions;

public class LongVmAction extends TypeVmAction<Long>{

    public LongVmAction(Long data) {
        super(data);
    }

    public LongVmAction(int actionCode, Long data) {
        super(actionCode, data);
    }

}
