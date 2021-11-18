package com.sloth.functions.layoutmanager;

import android.content.Context;
import android.view.View;

import com.rongyi.common.widget.recyclerview.layoutmanager.base.AbstractLineLayoutManager;

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

public class CarouselGalleryLayoutManager extends AbstractLineLayoutManager {

    private int itemSpace;
    private float minScale;
    private float moveSpeed;
    private boolean alphaItem = false;
    //默认0.3f
    private float mAlpha = 0.3f;
    //相对front的层叠比例，默认0，不层叠
    private float stackRatio = 0f;


    public CarouselGalleryLayoutManager(Context context, int itemSpace) {
        this(new Builder(context, itemSpace));
    }

    public CarouselGalleryLayoutManager(Context context, int itemSpace, int orientation) {
        this(new Builder(context, itemSpace).setOrientation(orientation));
    }

    public CarouselGalleryLayoutManager(Context context, int itemSpace, int orientation, boolean reverseLayout) {
        this(new Builder(context, itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public CarouselGalleryLayoutManager(Builder builder) {
        this(builder.context, builder.itemSpace, builder.minScale, builder.orientation,
                builder.maxVisibleItemCount, builder.moveSpeed, builder.distanceToBottom,
                builder.reverseLayout);
    }

    private CarouselGalleryLayoutManager(Context context, int itemSpace, float minScale, int orientation,
                                         int maxVisibleItemCount, float moveSpeed, int distanceToBottom,
                                         boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setEnableBringCenterToFront(true);
        setDistanceToBottom(distanceToBottom);
        setMaxVisibleItemCount(maxVisibleItemCount);
        this.itemSpace = itemSpace;
        this.minScale = minScale;
        this.moveSpeed = moveSpeed;
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public float getMinScale() {
        return minScale;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setItemSpace(int itemSpace) {
        assertNotInLayoutOrScroll(null);
        if (this.itemSpace == itemSpace) {
            return;
        }
        this.itemSpace = itemSpace;
        removeAllViews();
    }

    public void setMinScale(float minScale) {
        assertNotInLayoutOrScroll(null);
        if (minScale > 1f) {
            minScale = 1f;
        }
        if (this.minScale == minScale) {
            return;
        }
        this.minScale = minScale;
        requestLayout();
    }

    public void setMoveSpeed(float moveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.moveSpeed == moveSpeed) {
            return;
        }
        this.moveSpeed = moveSpeed;
    }

    public void setStackRatio(float stackRatio){
        assertNotInLayoutOrScroll(null);
        if(this.stackRatio == stackRatio){
            return;
        }
        this.stackRatio = stackRatio;
        requestLayout();
    }

    @Override
    protected float setInterval() {
        return (mDecoratedMeasurement - itemSpace);
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) {
        if(stackRatio > 0){
            if(canScrollHorizontally()){
                itemView.setTranslationX(-targetOffset * stackRatio);
            }else{
                itemView.setTranslationY(-targetOffset * stackRatio);
            }
        }

        float scale = calculateScale(targetOffset + mSpaceMain);
        itemView.setScaleX(scale);
        itemView.setScaleY(scale);
        if (alphaItem) {
            itemView.setAlpha(calAlpha(targetOffset));
        }
    }

    private float calAlpha(float targetOffset) {
        final float offset = Math.abs(targetOffset);
        float alpha = (mAlpha - 1f) / mInterval * offset + 1f;
        if (offset >= mInterval) {
            alpha = mAlpha;
        }
        return alpha;
    }

    @Override
    protected float getDistanceRatio() {
        if (moveSpeed == 0) {
            return Float.MAX_VALUE;
        }
        return 1 / moveSpeed;
    }

    @Override
    protected float setViewElevation(View itemView, float targetOffset) {
        return itemView.getScaleX() * 5;
    }

    private float calculateScale(float x) {
        float deltaX = Math.abs(x - (mOrientationHelper.getTotalSpace() - mDecoratedMeasurement) / 2f);
        return (minScale - 1) * deltaX / (mOrientationHelper.getTotalSpace() / 2f) + 1f;
    }

    public void setAlphaItem(boolean alphaItem) {
        this.alphaItem = alphaItem;
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public static class Builder {
        private static final float DEFAULT_SPEED = 1f;
        private static final float MIN_SCALE = 0.5f;

        private final Context context;
        private final int itemSpace;
        private int orientation;
        private float minScale;
        private float moveSpeed;
        private int maxVisibleItemCount;
        private boolean reverseLayout;
        private int distanceToBottom;

        public Builder(Context context, int itemSpace) {
            this.itemSpace = itemSpace;
            this.context = context;
            orientation = HORIZONTAL;
            minScale = MIN_SCALE;
            this.moveSpeed = DEFAULT_SPEED;
            reverseLayout = false;
            maxVisibleItemCount = AbstractLineLayoutManager.DETERMINE_BY_MAX_AND_MIN;
            distanceToBottom = AbstractLineLayoutManager.INVALID_SIZE;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
            return this;
        }

        public Builder setMinScale(float minScale) {
            this.minScale = minScale;
            return this;
        }

        public Builder setReverseLayout(boolean reverseLayout) {
            this.reverseLayout = reverseLayout;
            return this;
        }

        public Builder setMoveSpeed(float moveSpeed) {
            this.moveSpeed = moveSpeed;
            return this;
        }

        public Builder setMaxVisibleItemCount(int maxVisibleItemCount) {
            this.maxVisibleItemCount = maxVisibleItemCount;
            return this;
        }

        public Builder setDistanceToBottom(int distanceToBottom) {
            this.distanceToBottom = distanceToBottom;
            return this;
        }

        public CarouselGalleryLayoutManager build() {
            return new CarouselGalleryLayoutManager(this);
        }
    }
}