package com.sloth.pinsdemo;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.pinsplatform.Strategies;
import com.sloth.xlog.DefaultXLogConfig;
import com.sloth.xlog.XLogConfig;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/19 14:15
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/19         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class Configs {

    public static final String LOG_ENGINE = Strategies.LogEngine.XLOG;

    @RouterService(interfaces = XLogConfig.class, singleton = true, defaultImpl = true)
    public static class AppXLogConfig extends DefaultXLogConfig {
        public AppXLogConfig() { }
    }

}
