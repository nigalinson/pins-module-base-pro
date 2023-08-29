package com.sloth.functions.log.logger;

import com.orhanobut.logger.Logger;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.LogComponent;

@RouterService(interfaces = LogComponent.class, key = ComponentTypes.Log.LOGGER)
public class LoggerComponent extends LogComponent.AbsLog implements LogComponent {

    @Override
    protected void debug(String tag, String msg) {
        Logger.t(tag).d(msg);
    }

    @Override
    protected void verbose(String tag, String msg) {
        Logger.t(tag).v(msg);
    }

    @Override
    protected void info(String tag, String msg) {
        Logger.t(tag).i(msg);
    }

    @Override
    protected void warn(String tag, String msg) {
        Logger.t(tag).w(msg);
    }

    @Override
    protected void error(String tag, String msg) {
        Logger.t(tag).e(msg);
    }

    @Override
    public void flush(boolean sync) {
        //Logger不需要flush
    }

    @Override
    public void exit() {
        //Logger不需要exit
    }
}
