package com.sloth.functions.log;

import com.sloth.platform.LogComponent;
import com.sloth.utils.AppUtils;
import com.sloth.utils.Utils;

public class DefaultLogBuilder implements LogComponent.LogBuilder {

    @Override
    public String cacheFolder() {
        return Utils.getApp().getCacheDir() + "/log/";
    }

    @Override
    public String fileFolder() {
        return Utils.getApp().getExternalCacheDir() + "/log/" + AppUtils.getAppName() + "/";
    }

}
