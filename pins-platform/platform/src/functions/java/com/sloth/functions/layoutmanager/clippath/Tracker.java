package com.sloth.functions.layoutmanager.clippath;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/1/26 16:17
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/1/26         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface Tracker<P> {
    P track(float fraction);

    long duration();

}
