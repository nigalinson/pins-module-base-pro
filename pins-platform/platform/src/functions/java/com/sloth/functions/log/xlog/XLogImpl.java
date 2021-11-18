package com.sloth.functions.log.xlog;

import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.functions.FuncCenter;
import com.sloth.pinsplatform.Log;
import com.sloth.tools.util.GsonUtils;
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
@RouterService(interfaces = Log.class, key = FuncCenter.Funcs.Log.xLog, singleton = true)
public class XLogImpl implements Log {

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
    public void d(String tag, String msg) {
        com.tencent.mars.xlog.Log.d(tag, msg);
    }

    @Override
    public void v(String tag, String msg) {
        com.tencent.mars.xlog.Log.v(tag, msg);
    }

    @Override
    public void i(String tag, String msg) {
        com.tencent.mars.xlog.Log.i(tag, msg);
    }

    @Override
    public void w(String tag, String msg) {
        com.tencent.mars.xlog.Log.w(tag, msg);
    }

    @Override
    public void e(String tag, String msg) {
        com.tencent.mars.xlog.Log.e(tag, msg);
    }

    @Override
    public void json(String tag, Object obj) {
        com.tencent.mars.xlog.Log.d(tag, GsonUtils.toJson(obj));
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