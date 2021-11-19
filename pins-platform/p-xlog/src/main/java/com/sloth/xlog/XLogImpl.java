package com.sloth.xlog;

import com.sankuai.waimai.router.Router;
import com.sankuai.waimai.router.annotation.RouterProvider;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.pinsplatform.Strategies;
import com.sloth.pinsplatform.log.Log;
import com.sloth.tools.util.GsonUtils;
import com.sloth.tools.util.ProcessUtils;
import com.tencent.mars.xlog.Xlog;

/**
 * Author:    ZhuWenWu
 * Version    V1.0
 * Date:      16/1/26  下午2:33.
 * Description: 日志打印帮助类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 16/1/26        ZhuWenWu            1.0                    1.0
 * Why & What is modified:
 * 2021/07/22 新增多进程支持，前缀为：
 * - 主进程ry_
 * - 其他进程 [进程名]_
 */
@RouterService(interfaces = Log.class, key = Strategies.LogEngine.XLOG, singleton = true)
public class XLogImpl implements Log {

    @RouterProvider
    public static XLogImpl provideXLog() {
        XLogConfig config = Router.getService(XLogConfig.class);
        assert config != null;
        String process = ProcessUtils.getCurrentProcessName();
        boolean isMainProcess = ProcessUtils.isMainProcess();
        System.out.println("初始化Xlog <--> 进程：" + process +" 是否主进程：" + isMainProcess);
        XLogImpl ins = new XLogImpl(config.cachePath(), config.logPath(), isMainProcess ? "log" : validPrefix(process));
        ins.setLevel(config.level());
        return ins;
    }

    private static String validPrefix(String process) {
        return process.replace(":", ".")
                .replace("/",".")
                .replace("\\",".");
    }

    private int level;

    /**
     * 初始化腾讯log文件
     *
     * @param cachePath 缓存目录
     * @param logPath   日志目录
     */
    public XLogImpl(String cachePath, String logPath, String prefix) {
        System.loadLibrary("c++_shared");
        System.loadLibrary("marsxlog");
        com.tencent.mars.xlog.Log.setLogImp(new Xlog());
        com.tencent.mars.xlog.Log.setConsoleLogOpen(true);
        com.tencent.mars.xlog.Log.appenderOpen(Xlog.LEVEL_ALL, Xlog.AppednerModeAsync, cachePath, logPath, prefix, 0);
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public void d(String tag, String msg) {
        if(level <= Log.D){
            com.tencent.mars.xlog.Log.d(tag, msg);
        }
    }

    @Override
    public void v(String tag, String msg) {
        if(level <= Log.V){
            com.tencent.mars.xlog.Log.v(tag, msg);
        }
    }

    @Override
    public void i(String tag, String msg) {
        if(level <= Log.I){
            com.tencent.mars.xlog.Log.i(tag, msg);
        }
    }

    @Override
    public void w(String tag, String msg) {
        if(level <= Log.W){
            com.tencent.mars.xlog.Log.w(tag, msg);
        }
    }

    @Override
    public void e(String tag, String msg) {
        if(level <= Log.E){
            com.tencent.mars.xlog.Log.e(tag, msg);
        }
    }

    @Override
    public void json(String tag, Object obj) {
        if(level <= Log.I){
            com.tencent.mars.xlog.Log.d(tag, GsonUtils.toJson(obj));
        }
    }

    @Override
    public void flush(boolean sync) {
        com.tencent.mars.xlog.Log.appenderFlushSync(sync);
    }

    @Override
    public void exit() {
        com.tencent.mars.xlog.Log.appenderClose();
    }

}