package com.sloth.animator.transform;

import androidx.viewpager2.widget.ViewPager2;

public abstract class AbsVerticalAndHorizTransform implements ViewPager2.PageTransformer {

    /**
     * 方向
     */
    private @ViewPager2.Orientation int orientation = ViewPager2.ORIENTATION_HORIZONTAL;

    /**
     * 倒转动画
     */
    private boolean reverse = false;

    public void setOrientation(@ViewPager2.Orientation int orientation) {
        this.orientation = orientation;
    }

    public @ViewPager2.Orientation int orientation() {
        return orientation;
    }

    public boolean isHorizontal(){
        return orientation == ViewPager2.ORIENTATION_HORIZONTAL;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
