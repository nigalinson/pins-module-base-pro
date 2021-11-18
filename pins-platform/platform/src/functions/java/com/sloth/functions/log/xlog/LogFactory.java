package com.sloth.functions.log.xlog;

import android.content.Context;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/7/22 15:43
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/22         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class LogFactory {

    public static XLogImpl.XLogFactory xLogFactory(Context context, String cachePath, String logPath){
        XLogImpl.XLogFactory factory = new XLogImpl.XLogFactory();
        factory.with(context, cachePath, logPath);
        return factory;
    }

    public static LoggerImpl.LoggerFactory loggerFactory(){
        return new LoggerImpl.LoggerFactory();
    }

}
