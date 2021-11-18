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
 * 扇形展开
 */
public class FanShapeGalleryLayoutManager extends AbstractGalleryLayoutManager {

    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;

    private int verticalOffset = 0;

    //扇形展开半径
    private int radius = 100;

    private int align = ALIGN_LEFT;

    public FanShapeGalleryLayoutManager(Context context) {
        super(context);
    }

    public FanShapeGalleryLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getVerticalOffset() {
        return verticalOffset;
    }

    public void setVerticalOffset(int verticalOffset) {
        this.verticalOffset = verticalOffset;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    @SuppressLint("NewApi")
    @Override
    protected void layoutChildPosition(View v, int position, int renderCount, int index, boolean isStackTop, float percent) {
        //offset _ not>>index offset >> 0
        //scale _ not>>index  SCALE >> 1
        //alpha - not>>index  ALPHA>>1

        int totalWidth = getHorizontalSpace();
        int totalHeight = getVerticalSpace();
        int itemWidth = getItemWidth();
        int itemHeight = getItemHeight();

        if(align == ALIGN_LEFT){
            float perAnchor = 180f / renderCount;
            float anchor = position * perAnchor;
            int xFloat = (int) (radius * Math.sin(Math.toRadians(anchor)));
            int yFloat = (int) (radius * Math.cos(Math.toRadians(anchor)));

            int l = xFloat - (itemWidth / 2) + verticalOffset;
            int t = totalHeight / 2 - yFloat - (itemHeight / 2);
            v.layout(l, t, l + itemWidth, t + itemHeight);
        }else{
            float perAnchor = 180f / renderCount;
            float anchor = position * perAnchor;
            int xFloat = (int) (radius * Math.sin(Math.toRadians(anchor)));
            int yFloat = (int) (radius * Math.cos(Math.toRadians(anchor)));

            int l = totalWidth - xFloat - (itemWidth / 2) - verticalOffset;
            int t = totalHeight / 2 - yFloat - (itemHeight / 2);
            v.layout(l, t, l + itemWidth, t + itemHeight);
        }

    }
}
