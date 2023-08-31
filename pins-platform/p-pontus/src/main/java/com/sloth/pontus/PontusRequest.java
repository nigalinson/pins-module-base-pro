package com.sloth.pontus;

import com.sloth.platform.ResourceManagerComponent;

public class PontusRequest implements ResourceManagerComponent.Request {

    private final ResourceManagerComponent owner;
    private final ResourceManagerComponent.ResourceManagerConfig config;
    private final String url;
    private String path;
    private String md5;
    private String group;
    private String additionInfo;
    private boolean maxHotness;
    private ResourceManagerComponent.ResourceListener listener;
    private Long id;

    public PontusRequest(ResourceManagerComponent rmc, ResourceManagerComponent.ResourceManagerConfig rmconfig, String url) {
        this.owner = rmc;
        this.config = rmconfig;
        this.url = url;
    }

    @Override
    public ResourceManagerComponent.Request setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public ResourceManagerComponent.Request setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    @Override
    public ResourceManagerComponent.Request setGroup(String group) {
        this.group = group;
        return this;
    }

    @Override
    public ResourceManagerComponent.Request setAdditionInfo(String additionInfo) {
        this.additionInfo = additionInfo;
        return this;
    }

    @Override
    public ResourceManagerComponent.Request setMaxHotness(boolean maxHotness) {
        this.maxHotness = maxHotness;
        return this;
    }

    @Override
    public ResourceManagerComponent.Request submit(ResourceManagerComponent.ResourceListener resourceListener) {
        this.listener = resourceListener;
        owner.submit(this);
        return this;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String url() {
        return url;
    }

    @Override
    public String path() {
        return path;
    }

    @Override
    public String md5() {
        return md5;
    }

    @Override
    public String group() {
        return group;
    }

    @Override
    public String additionInfo() {
        return additionInfo;
    }

    @Override
    public boolean maxHotness() {
        return maxHotness;
    }

    @Override
    public ResourceManagerComponent.ResourceListener getListener() {
        return listener;
    }

    @Override
    public void cancel() {
        owner.cancel(url);
        owner.removeListener(listener);
        listener = null;
    }

    @Override
    public void detach() {
        owner.removeListener(listener);
        listener = null;
    }

}
