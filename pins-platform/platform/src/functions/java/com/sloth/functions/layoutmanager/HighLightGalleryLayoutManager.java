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
public class HighLightGalleryLayoutManager extends AbstractGalleryLayoutManager {

    private int itemOffset = 0;

    private int mainVerticalOffset = 0;

    private int besideVerticalOffset = 0;

    private float itemScale = 1;

    private float itemAlpha = 1;

    public HighLightGalleryLayoutManager(Context context) {
        super(context);
    }

    public HighLightGalleryLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public void setItemOffset(int itemOffset) {
        this.itemOffset = itemOffset;
    }

    public int getMainVerticalOffset() {
        return mainVerticalOffset;
    }

    public void setMainVerticalOffset(int mainVerticalOffset) {
        this.mainVerticalOffset = mainVerticalOffset;
    }

    public int getBesideVerticalOffset() {
        return besideVerticalOffset;
    }

    public void setBesideVerticalOffset(int besideVerticalOffset) {
        this.besideVerticalOffset = besideVerticalOffset;
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
        //offset _ not>>index offset >> 0
        //scale _ not>>index  SCALE >> 1
        //alpha - not>>index  ALPHA>>1

        v.setTranslationX(0);
        v.setTranslationY(0);
        v.setAlpha(1);
        v.setScaleX(1);
        v.setScaleY(1);
        v.setZ(0);

        if(canScrollHorizontally()){
            renderHor(v, position, renderCount, index, isStackTop, percent);
        }else {
            renderVer(v, position, renderCount, index, isStackTop, percent);
        }
    }

    @SuppressLint("NewApi")
    private void renderHor(View v, int position, int renderCount, int index, boolean isStackTop, float percent) {
        int normalWidth = getItemWidth();
        int normalHeight = getItemHeight();
        int scaleWidth = (int) (normalWidth * itemScale);
        int scaleHeight = (int) (normalHeight * itemScale);

        int centerLeft = (getHorizontalSpace() - normalWidth) / 2;
        int centerTop = (getVerticalSpace() - normalHeight) / 2;

        int left = (int) (centerLeft + (normalWidth * index)) - (int)(normalWidth * percent);
        int top = 0;
        layoutDecoratedWithMargins(v, left, top, left + normalWidth, top + normalHeight);

        int middleGap = (scaleWidth - normalWidth) / 2;
        int transX = 0;
        int transY = 0;
        float scale = 1;
        float alpha = 1;
        if(index == 0){
            transX = (int) (-middleGap * percent) + (int)(-itemOffset * percent);
            transY = (int) (mainVerticalOffset * (1 - percent) + besideVerticalOffset * (percent));
            scale = (float) Math.pow(itemScale, 1 - percent);
            alpha = itemAlpha + ((1 - itemAlpha) * (1 - percent));
        }else if(index == 1){
            transX = (int) (middleGap * (1 - percent)) + (int)(itemOffset * (1-percent));
            transY = (int) (mainVerticalOffset * percent + besideVerticalOffset * (1 - percent));
            scale = (float) Math.pow(itemScale, percent);
            alpha = itemAlpha + ((1 - itemAlpha) * percent);
        }else{
            transX = (int) ((index > 0 ? 1 : -1) * middleGap);
            transY = besideVerticalOffset;

            if(index > 0){
                transX = transX + (itemOffset * index) + (int)(-itemOffset * percent);
            }else{
                transX = transX + (itemOffset * index) + (int)(-itemOffset * percent);
            }

            scale = 1;
            alpha = itemAlpha;
        }

        v.setTranslationX(transX);
        v.setTranslationY(transY);
        v.setPivotY(0);
        v.setScaleX(scale);
        v.setScaleY(scale);
        v.setAlpha(alpha);

        v.setZ(((percent <= 0.5f && index == 0) || (percent > 0.5f && index == 1)) ? 1f : 0f);
    }

    @SuppressLint("NewApi")
    private void renderVer(View v, int position, int renderCount, int index, boolean isStackTop, float percent) {
        int normalWidth = getItemWidth();
        int normalHeight = getItemHeight();
        int scaleWidth = (int) (normalWidth * itemScale);
        int scaleHeight = (int) (normalHeight * itemScale);

        int centerLeft = (getHorizontalSpace() - normalWidth) / 2;
        int centerTop = (getVerticalSpace() - normalHeight) / 2;

        int left = 0;
        int top = (int) (centerTop + (normalHeight * index)) - (int)(normalHeight * percent);
        layoutDecoratedWithMargins(v, left, top, left + normalWidth, top + normalHeight);

        int middleGap = (scaleHeight - normalHeight) / 2;
        int transX = 0;
        int transY = 0;
        float scale = 1;
        float alpha = 1;
        if(index == 0){
            transX = (int) (-middleGap * percent) + (int)(-itemOffset * percent);
            transY = (int) (mainVerticalOffset * (1 - percent) + besideVerticalOffset * (percent));
            scale = (float) Math.pow(itemScale, 1 - percent);
            alpha = itemAlpha + ((1 - itemAlpha) * (1 - percent));
        }else if(index == 1){
            transX = (int) (middleGap * (1 - percent)) + (int)(itemOffset * (1-percent));
            transY = (int) (mainVerticalOffset * percent + besideVerticalOffset * (1 - percent));
            scale = (float) Math.pow(itemScale, percent);
            alpha = itemAlpha + ((1 - itemAlpha) * percent);
        }else{
            transX = (int) ((index > 0 ? 1 : -1) * middleGap);
            transY = besideVerticalOffset;

            if(index > 0){
                transX = transX + (itemOffset * index) + (int)(-itemOffset * percent);
            }else{
                transX = transX + (itemOffset * index) + (int)(-itemOffset * percent);
            }

            scale = 1;
            alpha = itemAlpha;
        }

        v.setTranslationX(transY);
        v.setTranslationY(transX);
        v.setPivotX(0);
        v.setScaleX(scale);
        v.setScaleY(scale);
        v.setAlpha(alpha);

        v.setZ(((percent <= 0.5f && index == 0) || (percent > 0.5f && index == 1)) ? 1f : 0f);
    }
}
