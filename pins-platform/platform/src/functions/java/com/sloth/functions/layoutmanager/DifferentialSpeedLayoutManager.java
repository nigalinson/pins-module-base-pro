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
 * 差速器切换 - 第一页略微覆盖第二页 - 其他类似普通linearLayout
 */
public class DifferentialSpeedLayoutManager extends AbstractBannerLayoutManager {

    public DifferentialSpeedLayoutManager(Context context) {
        super(context);
    }

    public DifferentialSpeedLayoutManager(Context context, int orientation, boolean reverseLayout) {
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

        int anchorLeft = (getHorizontalSpace() - getItemWidth()) / 2;
        int anchorTop = (getVerticalSpace() - getItemHeight()) / 2;

        if(isStackTop){
            float realPercent = (1.0f - Math.min(1.2f - percent, 1.0f)) / 0.8f;
            int left = (int) (anchorLeft - getItemWidth() * realPercent);
            layoutDecoratedWithMargins(v, left, anchorTop, left + getItemWidth(), anchorTop + getItemHeight());

            float ratio = 1f - 0.1f * realPercent;
            v.setScaleX(ratio);
            v.setScaleY(ratio);
        }else{
            int left = (int) (anchorLeft + ((1f - percent) * getItemWidth()));
            layoutDecoratedWithMargins(v, left, anchorTop, left + getItemWidth(), anchorTop + getItemHeight());
        }
    }

    private void layoutVertical(View v, boolean isStackTop, float percent) {
        v.setScaleX(1);
        v.setScaleY(1);

        int anchorLeft = (getHorizontalSpace() - getItemWidth()) / 2;
        int anchorTop = (getVerticalSpace() - getItemHeight()) / 2;

        if(isStackTop){
            float realPercent = (1.0f - Math.min(1.2f - percent, 1.0f)) / 0.8f;
            int top = (int) (anchorTop - getItemHeight() * realPercent);
            layoutDecoratedWithMargins(v, anchorLeft, top, anchorLeft + getItemWidth(), top + getItemHeight());

            float ratio = 1f - 0.1f * realPercent;
            v.setScaleX(ratio);
            v.setScaleY(ratio);
        }else{
            int top = (int) (anchorTop + ((1f - percent) * getItemHeight()));
            layoutDecoratedWithMargins(v, anchorLeft, top, anchorLeft + getItemWidth(), top + getItemHeight());
        }
    }

}
