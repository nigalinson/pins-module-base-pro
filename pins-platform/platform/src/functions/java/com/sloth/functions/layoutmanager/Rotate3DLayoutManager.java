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
public class Rotate3DLayoutManager extends AbstractBannerLayoutManager {

    public Rotate3DLayoutManager(Context context) {
        super(context);
    }

    public Rotate3DLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    protected void layoutChildPosition(View v, int position, int renderCount, int index, boolean isStackTop, float percent) {
        if(canScrollHorizontally()){
            layoutHorizontal(v, isStackTop, percent);
        }else{
            layoutVertical(v, isStackTop, percent);
        }
    }

    private void layoutHorizontal(View v, boolean isStackTop, float percent) {
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAlpha(1);
        v.setRotationY(0);

        int anchorLeft = (getHorizontalSpace() - getItemWidth()) / 2;
        int anchorTop = (getVerticalSpace() - getItemHeight()) / 2;

        if(isStackTop){
            int left = (int) (anchorLeft - getItemWidth() * percent);
            layoutDecoratedWithMargins(v, left, anchorTop, left + getItemWidth(), anchorTop + getItemHeight());

            float ratio = 1f - 0.5f * percent;
            v.setScaleX(ratio);
            v.setScaleY(ratio);

            v.setRotationY(-60f * percent);
            v.setAlpha(1-percent);
        }else{
            int left = (int) (anchorLeft + ((1f - percent) * getItemWidth()));
            layoutDecoratedWithMargins(v, left, anchorTop, left + getItemWidth(), anchorTop + getItemHeight());

            float ratio = 0.8f + 0.2f * percent;
            v.setScaleX(ratio);
            v.setScaleY(ratio);
            v.setAlpha(percent);
        }
    }

    private void layoutVertical(View v, boolean isStackTop, float percent) {
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAlpha(1);
        v.setRotationX(0);

        int anchorLeft = (getHorizontalSpace() - getItemWidth()) / 2;
        int anchorTop = (getVerticalSpace() - getItemHeight()) / 2;

        if(isStackTop){
            int top = (int) (anchorTop - getItemHeight() * percent);
            layoutDecoratedWithMargins(v, anchorLeft, top, anchorLeft + getItemWidth(), top + getItemHeight());

            float ratio = 1f - 0.5f * percent;
            v.setScaleX(ratio);
            v.setScaleY(ratio);

            v.setRotationX(60f * percent);
            v.setAlpha(1-percent);
        }else{
            int top = (int) (anchorTop + ((1f - percent) * getItemHeight()));
            layoutDecoratedWithMargins(v, anchorLeft, top, anchorLeft + getItemWidth(), top + getItemHeight());

            float ratio = 0.8f + 0.2f * percent;
            v.setScaleX(ratio);
            v.setScaleY(ratio);
            v.setAlpha(percent);
        }
    }

}
