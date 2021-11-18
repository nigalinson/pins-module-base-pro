package com.sloth.functions.layoutmanager;

import android.content.Context;
import android.view.View;

import com.rongyi.common.widget.recyclerview.layoutmanager.base.AbstractBannerLayoutManager;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/7 16:28
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/7         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ZoomLayoutManager extends AbstractBannerLayoutManager {

    public ZoomLayoutManager(Context context) {
        super(context);
    }

    public ZoomLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    protected void layoutChildPosition(View v, int position, int renderCount, int index, boolean isStackTop, float percent) {

        v.setScaleX(1);
        v.setScaleY(1);
        v.setAlpha(1);

        int anchorLeft = (getHorizontalSpace() - getItemWidth()) / 2;
        int anchorTop = (getVerticalSpace() - getItemHeight()) / 2;
        layoutDecoratedWithMargins(v, anchorLeft, anchorTop, anchorLeft + getItemWidth(), anchorTop + getItemHeight());

        if(isStackTop){
            float ratio = 1- (0.5f * percent);
            v.setScaleX(ratio);
            v.setScaleY(ratio);
            v.setAlpha(1-percent);
        }else{
            float ratio = 0.5f + 0.5f * percent;
            v.setScaleX(ratio);
            v.setScaleY(ratio);
            v.setAlpha(percent);
        }
    }
}
