package com.sloth.functions.log.xlog;

import android.content.Context;
import com.orhanobut.logger.Logger;
import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sloth.pinsplatform.Log;
import com.sloth.tools.util.ProcessUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/18 18:45
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class XLogFactory {

    @RouterProvider
    public static Log build() {
        XLogConfig config = Router.getService(XLogConfig.class);
        String process = ProcessUtils.getCurrentProcessName();
        boolean isMainProcess = ProcessUtils.isMainProcess();
        Logger.t("XLogFactory").d( "进程：" + process +" 是否主进程：" + isMainProcess);
        return new XLogImpl(config.cachePath(), config.logPath(), isMainProcess ? "ry" : validPrefix(process));
    }

    private static String validPrefix(String process) {
        return process.replace(":", ".")
                .replace("/",".")
                .replace("\\",".");
    }

}