package com.sloth.architecture.mvvm.actions;

public class StringVmAction extends TypeVmAction<String>{

    public StringVmAction(String data) {
        super(data);
    }

    public StringVmAction(int actionCode, String data) {
        super(actionCode, data);
    }

}
