package com.sloth.functions.log.logger;

import com.orhanobut.logger.Logger;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.FuncCenter;
import com.sloth.pinsplatform.Log;
import com.sloth.tools.util.GsonUtils;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/22 16:31
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/22         Carl            1.0                    1.0
 * Why & What is modified:
 */
@RouterService(interfaces = Log.class, key = FuncCenter.Funcs.Log.Logger, singleton = true)
public class LoggerImpl implements Log {

    private LoggerImpl() { }

    @Override
    public void d(String tag, String msg) {
        Logger.t(tag).d(msg);
    }

    @Override
    public void v(String tag, String msg) {
        Logger.t(tag).v(msg);
    }

    @Override
    public void i(String tag, String msg) {
        Logger.t(tag).i(msg);
    }

    @Override
    public void w(String tag, String msg) {
        Logger.t(tag).w(msg);
    }

    @Override
    public void e(String tag, String msg) {
        Logger.t(tag).e(msg);
    }

    @Override
    public void json(String tag, Object obj) {
        Logger.t(tag).json(GsonUtils.toJson(obj));
    }

    @Override
    public void flush(boolean sync) { }

    @Override
    public void exit() { }
}
