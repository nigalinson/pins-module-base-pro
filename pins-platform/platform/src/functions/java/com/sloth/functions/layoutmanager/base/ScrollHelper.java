package com.sloth.functions.layoutmanager.base;

import android.view.animation.DecelerateInterpolator;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rongyi.common.R;

/**
 * Author:    CaoKang
 * Version    V1.0
 * Date:      2018/7/25
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2018/7/25      CaoKang            1.0                    1.0
 * Why & What is modified:
 */

public class ScrollHelper {

    public static void smoothScrollToPosition(RecyclerView recyclerView, int targetPosition) {
        smoothScrollToPosition(recyclerView, targetPosition, RecyclerView.UNDEFINED_DURATION);
    }

    public static void smoothScrollToPosition(RecyclerView recyclerView, int targetPosition, int duration) {
        if(recyclerView.getLayoutManager() instanceof PositionScrollable){
            if(recyclerView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE){
                //静止状态下才允许翻到下一页
                return;
            }

            final int delta = ((PositionScrollable)recyclerView.getLayoutManager()).distanceToTargetPosition(targetPosition);
            if (((LinearLayoutManager)recyclerView.getLayoutManager()).getOrientation() == RecyclerView.VERTICAL) {
                recyclerView.smoothScrollBy(0, delta, new DecelerateInterpolator(), duration);
            } else {
                recyclerView.smoothScrollBy(delta, 0, new DecelerateInterpolator(), duration);
            }
        }else if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            recyclerView.setTag(R.integer.vp2_scroll_duration, duration);
            ((LinearLayoutManager)recyclerView.getLayoutManager()).smoothScrollToPosition(recyclerView, null, targetPosition);
        }
    }

    public static void scrollToPosition(RecyclerView recyclerView, int targetPosition) {
        if(recyclerView.getLayoutManager() instanceof PositionScrollable){
            final int delta = ((PositionScrollable)recyclerView.getLayoutManager()).distanceToTargetPosition(targetPosition);
            if (((LinearLayoutManager)recyclerView.getLayoutManager()).getOrientation() == RecyclerView.VERTICAL) {
                recyclerView.scrollBy(0, delta);
            } else {
                recyclerView.scrollBy(delta, 0);
            }
        }else if(recyclerView.getLayoutManager() instanceof LinearLayoutManager){
            ((LinearLayoutManager)recyclerView.getLayoutManager()).scrollToPosition(targetPosition);
        }
    }

}