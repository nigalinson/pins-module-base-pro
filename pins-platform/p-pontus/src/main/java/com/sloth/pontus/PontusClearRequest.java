package com.sloth.pontus;

import com.sloth.platform.ResourceManagerComponent;

import java.util.List;

public class PontusClearRequest implements ResourceManagerComponent.ClearRequest {

    @Override
    public int byHotness() {
        return 10;
    }

    @Override
    public List<String> byAdditions() {
        return null;
    }

    @Override
    public List<String> excepts() {
        return null;
    }
}
