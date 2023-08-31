package com.sloth.pontus.widget.banner;

import com.sloth.functions.listeners.OnlineResource;

public abstract class CacheResource implements OnlineResource {

    private String local;

    @Override
    public String localPath() {
        return local;
    }

    public void setLocal(String local){
        this.local = local;
    }

}
