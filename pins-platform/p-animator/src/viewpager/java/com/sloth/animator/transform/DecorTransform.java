package com.sloth.animator.transform;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.viewpager2.widget.ViewPager2;

/**
 * 需要依赖代理类来完成动效绘制的transform
 */
public interface DecorTransform extends ViewPager2.PageTransformer {
    FrameLayout createDecor(Context context);
}
