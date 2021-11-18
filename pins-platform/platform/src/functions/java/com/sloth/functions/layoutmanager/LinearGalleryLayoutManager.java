package com.sloth.functions.layoutmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import com.rongyi.common.widget.recyclerview.layoutmanager.base.AbstractGalleryLayoutManager;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/2/2 10:37
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/2/2         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class LinearGalleryLayoutManager extends AbstractGalleryLayoutManager {

    private int itemOffset = 0;

    private float itemScale = 1;

    private float itemAlpha = 1;

    public LinearGalleryLayoutManager(Context context) {
        super(context);
    }

    public LinearGalleryLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setItemOffset(int itemOffset) {
        this.itemOffset = itemOffset;
    }

    public void setItemScale(float itemScale) {
        this.itemScale = itemScale;
    }

    public void setItemAlpha(float itemAlpha) {
        this.itemAlpha = itemAlpha;
    }

    @SuppressLint("NewApi")
    @Override
    protected void layoutChildPosition(View v, int position, int renderCount, int index, boolean isStackTop, float percent) {
        v.setTranslationX(0);
        v.setTranslationY(0);
        v.setAlpha(1);
        v.setScaleX(1);
        v.setScaleY(1);
        v.setZ(0);

        int anchorLeft = (getHorizontalSpace() - getItemWidth()) / 2;
        int anchorTop = (getVerticalSpace() - getItemHeight()) / 2;
        layoutDecoratedWithMargins(v, anchorLeft, anchorTop, anchorLeft + getItemWidth(), anchorTop + getItemHeight());

        float trans = (getScrollItemSize() + itemOffset) * index - (getScrollItemSize() + itemOffset) * percent;

        if(canScrollHorizontally()){
            v.setTranslationX(trans);
        }else{
            v.setTranslationY(trans);
        }

        float s = (float) ((Math.pow(itemScale, Math.abs(index)) * (1-percent)) + (Math.pow(itemScale, Math.abs(index - 1)) * percent));
        v.setScaleX(s);
        v.setScaleY(s);

        float a = (float) ((Math.pow(itemAlpha, Math.abs(index)) * (1-percent)) + (Math.pow(itemAlpha, Math.abs(index - 1)) * percent));
        v.setAlpha(Math.max(a, 0));

        if(percent <= 0.5f){
            if(index == 0){
                v.setZ(1f);
            }
        }else if(percent > 0.5f){
            if(index == 1){
                v.setZ(1f);
            }
        }

    }
}
