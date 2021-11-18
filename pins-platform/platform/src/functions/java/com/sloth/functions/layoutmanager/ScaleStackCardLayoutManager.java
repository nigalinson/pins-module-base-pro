package com.sloth.functions.layoutmanager;

import android.content.Context;
import android.view.View;

import com.rongyi.common.widget.recyclerview.layoutmanager.base.AbstractStackLayoutManager;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/3 17:41
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/3         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ScaleStackCardLayoutManager extends AbstractStackLayoutManager {

    public ScaleStackCardLayoutManager(Context context) {
        super(context);
    }

    public ScaleStackCardLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public ScaleStackCardLayoutManager(Context context, int itemOffset, int maxVisibleItemCount, float itemScale) {
        super(context, itemOffset, maxVisibleItemCount, itemScale);
    }

    public ScaleStackCardLayoutManager(Context context, int orientation, boolean reverseLayout, int itemOffset, int maxVisibleItemCount, float itemScale) {
        super(context, orientation, reverseLayout, itemOffset, maxVisibleItemCount, itemScale);
    }

    @Override
    protected void layoutChildPosition(View v, int position, int renderCount, int index, boolean isStackTop, float percent) {
        //初始化View，防止复用异常
        v.setScaleX(1);
        v.setScaleY(1);
        v.setAlpha(1);

        int anchorCenter = (getSpace() - getScrollItemSize()) / 2;
        if(isStackTop){
            //顶部
            int start = anchorCenter - getHighlightOffset();
            layoutWithStart(v, start);
            v.setScaleX(1);
            v.setScaleY(1);
            v.setAlpha(1 - percent);
        }else{
            float scale = (float) (Math.pow(getItemScale(), index) + ((float)1-getItemScale()) * percent);
            int scaleLossOffset = (int) (getScrollItemSize() * (1- scale) / 2);
            int start = (int) (anchorCenter + ((1 - percent) * getItemOffset()) + scaleLossOffset + ((index - 1) * getItemOffset()));
            layoutWithStart(v, start);
            v.setScaleX(scale);
            v.setScaleY(scale);
            float perAlphaGap = (float)1 / Math.max(renderCount, getMaxVisibleItemCount() - 1);
            v.setAlpha(percent * perAlphaGap + (1 - (perAlphaGap * index)));
        }
    }

    private void layoutWithStart(View v, int start) {
        if(canScrollHorizontally()){
            int top = (getVerticalSpace() - getItemHeight()) / 2;
            layoutDecoratedWithMargins(v, start, top, start + getItemWidth(), top + getItemHeight());
        }else{
            int left = (getHorizontalSpace() - getItemWidth()) / 2;
            layoutDecoratedWithMargins(v, left, start, left + getItemWidth(), start + getItemHeight());
        }
    }
}
