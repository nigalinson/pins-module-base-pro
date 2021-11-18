package com.sloth.functions.adapter;

/**
 * Author:    hs
 * Version    V1.0
 * Date:      2018/10/18
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/10/18          hs        1.0                    1.0
 * Why & What is modified:
 */
public interface OnItemDataListener<T> {
    void OnItemClick(T position);
}
