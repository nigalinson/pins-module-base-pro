package com.sloth.functions.log.logcat;

import android.util.Log;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.LogComponent;

@RouterService(interfaces = LogComponent.class, key = ComponentTypes.Log.LOGCAT)
public class LogcatComponent extends LogComponent.AbsLog implements LogComponent {

    @Override
    protected void debug(String tag, String msg) {
        Log.d(tag, msg);
    }

    @Override
    protected void verbose(String tag, String msg) {
        Log.v(tag, msg);
    }

    @Override
    protected void info(String tag, String msg) {
        Log.i(tag, msg);
    }

    @Override
    protected void warn(String tag, String msg) {
        Log.w(tag, msg);
    }

    @Override
    protected void error(String tag, String msg) {
        Log.e(tag, msg);
    }

    @Override
    public void flush(boolean sync) {
        //Log不需要flush
    }

    @Override
    public void exit() {
        //Log不需要exit
    }
}
