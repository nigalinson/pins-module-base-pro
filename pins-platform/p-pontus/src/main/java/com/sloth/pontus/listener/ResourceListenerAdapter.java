package com.sloth.pontus.listener;

import com.sloth.platform.ResourceManagerComponent;

public class ResourceListenerAdapter implements ResourceManagerComponent.ResourceListener {

    @Override
    public void onResourceReady(Long resourceId, String url, String localPath) { }

    @Override
    public void onResourceFailed(Long resourceId, String url, String localPath, String errMsg) { }
}
