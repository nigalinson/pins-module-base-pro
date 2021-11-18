package com.sloth.functions.layoutmanager.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.rongyi.common.R;
import com.rongyi.common.animator.utils.floatinganimator.FloatingAnimatorAttacher;
import com.rongyi.common.utils.AutoDispose;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/12/7 16:28
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/12/7         Carl            1.0                    1.0
 * Why & What is modified:
 * 用于复杂过场动画的放置器，用floatwindow实现动画
 */
public abstract class AbsComplexSceneAnimatorLayoutManager extends AbstractAnimatorLayoutManager {

    private FloatingAnimatorAttacher floatingAnimatorAttacher;

    private final Animator.AnimatorListener defaultAnimStateListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
            onMixedAnimationStart(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            onMixedAnimationEnd(animation);
            detachComplexAnimator(complexEnterView());
            detachComplexAnimator(complexExitView());
            onAnimationComplete();
            afterMixedAnimationEnd(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            onMixedAnimationEnd(animation);
            detachComplexAnimator(complexEnterView());
            detachComplexAnimator(complexExitView());
            onAnimationComplete();
            afterMixedAnimationEnd(animation);
        }
    };

    private boolean hasComplexEnterAnimation = false;
    private boolean hasComplexExitAnimation = false;

    public AbsComplexSceneAnimatorLayoutManager(Context context, RecyclerView rv) {
        super(context, rv);
        initSetting(context, rv);
    }

    public AbsComplexSceneAnimatorLayoutManager(Context context, int orientation, boolean reverseLayout, RecyclerView rv) {
        super(context, orientation, reverseLayout, rv);
        initSetting(context, rv);
    }

    private void initSetting(Context context, RecyclerView rv){
        AutoDispose.fromPool(context.getClass().getSimpleName()).bind(context)
                .autoDispose(floatingAnimatorAttacher);
        floatingAnimatorAttacher = new FloatingAnimatorAttacher(context, rv);
    }

    @Override
    protected void onEnterAnimatorPlay(View view) {
        log("播放进入动画");
        Object cacheSnap = view.getTag(R.string.vh_tag_snapshot_still);
        beforeAttachComplexEnterAnimation(complexEnterView(), view, cacheSnap != null ? ((Bitmap) cacheSnap) : null);
        if(hasComplexEnterAnimator() && complexEnterView() != null){
            log("播放复杂进入动画");
            int anchorLeft = (int) mRecyclerView.getX();
            int anchorTop = (int) mRecyclerView.getY();
            attachComplexAnimator(complexEnterView(), anchorLeft, anchorTop, getItemWidth(), getItemHeight());
        }
        startMixedEnterAnimationPlaying(complexEnterView(), view, cacheSnap != null ? ((Bitmap) cacheSnap) : null, defaultAnimStateListener);
    }

    @Override
    protected void onExitAnimatorPlay(View view) {
        Object cacheSnap = view.getTag(R.string.vh_tag_snapshot_dynamic);
        beforeAttachComplexExitAnimation(complexExitView(), view, cacheSnap != null ? ((Bitmap) cacheSnap) : null);
        if(hasComplexExitAnimator() && complexExitView() != null){
            log("开始播放复杂退出动画");
            int anchorLeft = (int) mRecyclerView.getX();
            int anchorTop = (int) mRecyclerView.getY();
            attachComplexAnimator(complexExitView(), anchorLeft, anchorTop, getItemWidth(), getItemHeight());
        }
        startMixedExitAnimationPlaying(complexExitView(), view, cacheSnap != null ? ((Bitmap) cacheSnap) : null, defaultAnimStateListener);
    }

    protected boolean hasComplexEnterAnimator(){ return hasComplexEnterAnimation; }

    protected boolean hasComplexExitAnimator(){ return hasComplexExitAnimation; }

    public void setHasComplexEnterAnimation(boolean hasComplexEnterAnimation) {
        this.hasComplexEnterAnimation = hasComplexEnterAnimation;
    }

    public void setHasComplexExitAnimation(boolean hasComplexExitAnimation) {
        this.hasComplexExitAnimation = hasComplexExitAnimation;
    }

    protected abstract View complexEnterView();

    protected abstract View complexExitView();

    protected abstract void beforeAttachComplexEnterAnimation(View complexEnterView, View view, Bitmap bitmap);

    protected abstract void beforeAttachComplexExitAnimation(View complexExitView, View view, Bitmap bitmap);

    protected abstract void startMixedEnterAnimationPlaying(View complexEnterView, View view, Bitmap bitmap, Animator.AnimatorListener listener);

    protected abstract void startMixedExitAnimationPlaying(View complexExitView, View view, Bitmap bitmap, Animator.AnimatorListener listener);

    public void attachComplexAnimator(View view, int x, int y, int width, int height){
        if(floatingAnimatorAttacher != null && view != null){
            log("添加复杂View,x:" + x + "y:" + y + "width:" + width + "height:" + height);
            floatingAnimatorAttacher.attach(view, x, y, width, height);
        }
    }

    public void detachComplexAnimator(View view){
        if(floatingAnimatorAttacher != null && view != null){
            floatingAnimatorAttacher.detech(view);
        }
    }

    protected void onMixedAnimationStart(Animator animation) { }

    protected void onMixedAnimationEnd(Animator animation) { }

    protected void afterMixedAnimationEnd(Animator animation) { }
}
