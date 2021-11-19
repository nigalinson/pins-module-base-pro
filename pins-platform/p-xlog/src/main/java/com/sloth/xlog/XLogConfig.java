package com.sloth.xlog;

import com.sloth.pinsplatform.log.Log;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/18 19:22
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface XLogConfig {

    @Log.Level int level();

    String cachePath();

    String logPath();

}
