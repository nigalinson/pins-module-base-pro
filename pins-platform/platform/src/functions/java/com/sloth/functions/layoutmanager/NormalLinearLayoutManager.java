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

public class NormalLinearLayoutManager extends AbstractLineLayoutManager {

    private float moveSpeed;

    public NormalLinearLayoutManager(Context context) {
        this(new Builder(context));
    }

    public NormalLinearLayoutManager(Context context, int orientation) {
        this(new Builder(context).setOrientation(orientation));
    }

    public NormalLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        this(new Builder(context).setOrientation(orientation).setReverseLayout(reverseLayout));
    }

    public NormalLinearLayoutManager(Builder builder) {
        this(builder.context, builder.orientation,
                builder.maxVisibleItemCount, builder.moveSpeed, builder.reverseLayout);
    }

    private NormalLinearLayoutManager(Context context, int orientation, int maxVisibleItemCount,
                                      float moveSpeed, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setEnableBringCenterToFront(true);
        setMaxVisibleItemCount(maxVisibleItemCount);
        this.moveSpeed = moveSpeed;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        assertNotInLayoutOrScroll(null);
        if (this.moveSpeed == moveSpeed) {
            return;
        }
        this.moveSpeed = moveSpeed;
    }

    @Override
    protected float setInterval() {
        return mDecoratedMeasurement;
    }

    @Override
    protected void setItemViewProperty(View itemView, float targetOffset) { }

    @Override
    protected float getDistanceRatio() {
        if (moveSpeed == 0) {
            return Float.MAX_VALUE;
        }
        return 1 / moveSpeed;
    }

    public static class Builder {
        private static final float DEFAULT_SPEED = 1f;

        private final Context context;
        private int orientation;
        private float moveSpeed;
        private int maxVisibleItemCount;
        private boolean reverseLayout;

        public Builder(Context context) {
            this.context = context;
            orientation = HORIZONTAL;
            this.moveSpeed = DEFAULT_SPEED;
            reverseLayout = false;
            maxVisibleItemCount = AbstractLineLayoutManager.DETERMINE_BY_MAX_AND_MIN;
        }

        public Builder setOrientation(int orientation) {
            this.orientation = orientation;
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

        public NormalLinearLayoutManager build() {
            return new NormalLinearLayoutManager(this);
        }
    }
}