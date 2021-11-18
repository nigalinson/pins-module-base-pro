package com.sloth.functions.layoutmanager.particle;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.rongyi.common.animator.particle.fall.ParticleFallView;
import com.rongyi.common.utils.RYViewUtils;
import com.rongyi.common.widget.recyclerview.layoutmanager.base.AbsSimpleComingAnimatorLayoutManager;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/17 15:46
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/17         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class ParticleFallAnimatorLayoutManager extends AbsSimpleComingAnimatorLayoutManager {

    private static final String TAG = ParticleFallAnimatorLayoutManager.class.getSimpleName();

    private ParticleFallView playingView;

    private int itemSize = 10;
    //全屏时适合的个数
    private int xCount = 27, yCount = 48;
    private long duration = 3000;

    public ParticleFallAnimatorLayoutManager(Context context, RecyclerView rv) {
        super(context, rv);
    }

    public ParticleFallAnimatorLayoutManager(Context context, int orientation, boolean reverseLayout, RecyclerView rv) {
        super(context, orientation, reverseLayout, rv);
    }

    public void setItemSize(int itemSize) {
        this.itemSize = itemSize;
    }

    public void setXCount(int xCount) {
        this.xCount = xCount;
    }

    public void setYCount(int yCount) {
        this.yCount = yCount;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    protected void initThings(Context context) {
        super.initThings(context);
        playingView = new ParticleFallView(context);
    }

    @Override
    protected View complexExitView() {
        return playingView;
    }

    @Override
    protected void beforeAttachComplexExitAnimation(View complexExitView, View view, Bitmap bitmap) { }

    @Override
    protected void startMixedEnterAnimationPlaying(View complexEnterView, View view, Bitmap bitmap, Animator.AnimatorListener listener) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(complexEnterView, "alpha", 0.0f, 1.0f);
        anim.setDuration(1000);
        anim.start();
    }

    @Override
    protected void startMixedExitAnimationPlaying(View complexExitView, View view, Bitmap bitmap, Animator.AnimatorListener listener) {
        Rect rect = new Rect();
        view.getLocalVisibleRect(rect);
        Bitmap see = snap(view, bitmap);

        int bpX = see.getWidth();
        int bpY = see.getHeight();

        if(bpX < rect.width()){
            int gap = (rect.width() - bpX) / 2;
            rect.left = gap;
            rect.right = bpX + gap;
        }

        if(bpY < rect.height()){
            int gap = (rect.height() - bpY) / 2;
            rect.top = gap;
            rect.bottom = bpY + gap;
        }

        playingView.explode(rect, see, itemSize, xCount, yCount, duration, listener);
    }

    private Bitmap snap(View view, Bitmap snapshot) {
        return snapshot != null ? snapshot : RYViewUtils.snapshot(view);
    }

}
