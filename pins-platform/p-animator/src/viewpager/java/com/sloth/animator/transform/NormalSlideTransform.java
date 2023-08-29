package com.sloth.animator.transform;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import androidx.viewpager2.widget.ViewPager2;

public class NormalSlideTransform implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View view, float position) {
        //viewpager自带滑动layout，平移效果不需要实际执行内容，这里是为了填充transform数据，减少和其他类型的差异性，增强兼容性
    }
}
