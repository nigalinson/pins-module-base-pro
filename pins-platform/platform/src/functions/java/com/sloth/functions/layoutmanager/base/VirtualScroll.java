package com.sloth.functions.layoutmanager.base;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/7 17:02
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/7         Carl            1.0                    1.0
 * Why & What is modified:
 */
public interface VirtualScroll {

    int getScrollOffset();

    int getScrollItemOffset();

    float getScrollPercent();

    int getScrollItemSize();

    int firstPos();

}
