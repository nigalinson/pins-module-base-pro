package com.sloth.functions;

import com.sankuai.waimai.router.Router;
import com.sloth.pinsplatform.Log;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/18 18:36
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class FuncCenter {

    public static final class Funcs {
        public static final class Log {
            public static final String Logger = "Logger";
            public static final String xLog = "xLog";
        }

    }

    //=========================================log============================================
    //region log

    public static Log log(){
        return Router.getService(Log.class);
    }

    //endregion log


}
