package com.sloth.functions.layoutmanager.base;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/3 13:56
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/3         Carl            1.0                    1.0
 * Why & What is modified:
 * Recyclerview翻页监听
 */
public interface OnRecyclerViewPageChangeListener {

    default void beforePageChanged(int position){}

    void onPageChanged(int position);

    default void onScrollStateChanged(RecyclerView recyclerView, int state){}

    default void onScrolled(RecyclerView recyclerView, int dx, int dy){}
}
