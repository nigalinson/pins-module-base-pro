package com.sloth.pontus;

import com.sloth.platform.DownloadComponent;
import com.sloth.platform.Platform;
import com.sloth.platform.ResourceManagerComponent;
import com.sloth.utils.Utils;

public class PontusConfig implements ResourceManagerComponent.ResourceManagerConfig {

    @Override
    public String dbPath() {
        return baseFolder() + "resource-db";
    }

    @Override
    public String baseFolder() {
        return Utils.getApp().getExternalCacheDir().getAbsolutePath() + "/resource/";
    }

    @Override
    public boolean madClear() {
        return false;
    }

    @Override
    public long clearThresholdSize() {
        return 10 * 1024 * 1024 * 1024L;
    }

    @Override
    public long clearUntilSize() {
        return 5 * 1024 * 1024 * 1024L;
    }

    @Override
    public int clearFileNumsEveryTime() {
        return 10;
    }

    @Override
    public int concurrent() {
        return 10;
    }

    @Override
    public int retryTimes() {
        return 3;
    }

    @Override
    public boolean hashFileName() {
        return false;
    }

    @Override
    public boolean withSuffix() {
        return true;
    }

    @Override
    public DownloadComponent downloadEngine() {
        return Platform.download();
    }
}
