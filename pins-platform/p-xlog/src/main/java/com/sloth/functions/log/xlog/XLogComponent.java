package com.sloth.functions.log.xlog;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.log.DefaultLogBuilder;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.LogComponent;
import com.sloth.utils.ProcessUtils;
import com.sloth.utils.StringUtils;
import com.tencent.mars.xlog.Log;
import com.tencent.mars.xlog.Xlog;

@RouterService(interfaces = LogComponent.class, key = ComponentTypes.Log.X_LOG)
public class XLogComponent extends LogComponent.AbsLog {

    public XLogComponent() {
        this(new DefaultLogBuilder());
    }

    public XLogComponent(LogBuilder logBuilder) {
        String process = ProcessUtils.getCurrentProcessName();
        String mainProcess = ProcessUtils.getForegroundProcessName();
        boolean isMainProcess = (StringUtils.notEmpty(mainProcess) && mainProcess.equals(process));
        Log.d("xlog",  "进程：" + process + ",主进程：" + mainProcess + ",是否主进程：" + isMainProcess);

        String prefix = isMainProcess ? "ry" : validPrefix(process);

        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");
        Log.setLogImp(new Xlog());
        if (BuildConfig.DEBUG) {
            Log.setConsoleLogOpen(true);
            Log.appenderOpen(Xlog.LEVEL_ALL, Xlog.AppednerModeAsync, logBuilder.cacheFolder(),
                    logBuilder.fileFolder(), prefix, 0);
        } else {
            Log.setConsoleLogOpen(true);
            Log.appenderOpen(Xlog.LEVEL_ALL, Xlog.AppednerModeAsync, logBuilder.cacheFolder(),
                    logBuilder.fileFolder(), prefix, 0);
        }
    }

    private String validPrefix(String process) {
        if(null == process || "".equals(process)) return "remote";
        return process.replace(":", ".")
                .replace("/",".")
                .replace("\\",".");
    }

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
        Log.appenderFlushSync(sync);
    }

    @Override
    public void exit() {
        Log.appenderClose();
    }

}