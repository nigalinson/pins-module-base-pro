package com.sloth.xlog;

import com.sloth.pinsplatform.log.Log;
import com.sloth.tools.util.Utils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 14:10
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class DefaultXLogConfig implements XLogConfig {

    public DefaultXLogConfig() { }

    @Override
    public int level() {
        return Log.E;
    }

    @Override
    public String cachePath() {
        return Utils.getApp().getCacheDir().getAbsolutePath() + "/xlog/";
    }

    @Override
    public String logPath() {
        return Utils.getApp().getExternalCacheDir().getAbsolutePath() + "/xlog/";
    }

}
