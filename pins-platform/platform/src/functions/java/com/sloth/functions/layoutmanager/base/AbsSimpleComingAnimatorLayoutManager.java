package com.sloth.functions.layoutmanager.base;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/3/18 14:32
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/3/18         Carl            1.0                    1.0
 * Why & What is modified:
 */
public abstract class AbsSimpleComingAnimatorLayoutManager extends AbsComplexSceneAnimatorLayoutManager {

    private static final String TAG = AbsSimpleComingAnimatorLayoutManager.class.getSimpleName();

    private AppCompatImageView comingView;

    public AbsSimpleComingAnimatorLayoutManager(Context context, RecyclerView rv) {
        super(context, rv);
        initThings(context);
    }

    public AbsSimpleComingAnimatorLayoutManager(Context context, int orientation, boolean reverseLayout, RecyclerView rv) {
        super(context, orientation, reverseLayout, rv);
        initThings(context);
    }

    protected void initThings(Context context) {
        comingView = new AppCompatImageView(context);
        comingView.setScaleType(AppCompatImageView.ScaleType.FIT_CENTER);
        setHasComplexEnterAnimation(false);
        setHasComplexExitAnimation(true);
    }

    @Override
    protected View complexEnterView() {
        return comingView;
    }

    @Override
    protected void beforeAttachComplexEnterAnimation(View complexEnterView, View view, Bitmap bitmap) {
        if(bitmap != null){
            setHasComplexEnterAnimation(true);
            comingView.setImageBitmap(bitmap);
        }else{
            setHasComplexEnterAnimation(false);
            comingView.setImageBitmap(null);
        }
    }

    @Override
    protected void startMixedEnterAnimationPlaying(View complexEnterView, View view, Bitmap bitmap, Animator.AnimatorListener listener) { }

    @Override
    protected void onMixedAnimationStart(Animator animation) {
        super.onMixedAnimationStart(animation);
        getExitView().setAlpha(0f);
        //实际使用visible性能更佳，但是textureView播放视频时，如果invisible会导致无法加载视频，因此使用alpha加快加载速度
        getEnterView().setAlpha(hasComplexEnterAnimator() ? 0.1f : 1f);
    }

    @Override
    protected void onMixedAnimationEnd(Animator animation) {
        super.onMixedAnimationEnd(animation);
        getExitView().setAlpha(1f);
        getEnterView().setAlpha(1f);
    }
}
