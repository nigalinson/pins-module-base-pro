package com.sloth.pinsplatform;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/11/18 18:32
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/11/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface Log {

    void d(String tag, String msg);

    void v(String tag, String msg);

    void i(String tag, String msg);

    void w(String tag, String msg);

    void e(String tag, String msg);

    void json(String tag, Object obj);

    default void flush(boolean sync){ }

    default void exit(){ }

}
