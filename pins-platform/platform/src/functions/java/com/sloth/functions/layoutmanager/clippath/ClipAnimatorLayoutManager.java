package com.sloth.functions.layoutmanager.clippath;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.rongyi.common.utils.RYViewUtils;
import com.rongyi.common.widget.ClipPathView;
import com.rongyi.common.widget.recyclerview.layoutmanager.ClipLayoutManager;
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
 * 只需要上下左右动效时，建议使用 {@link ClipLayoutManager}
 */
@SuppressLint("NewApi")
public class ClipAnimatorLayoutManager extends AbsSimpleComingAnimatorLayoutManager {

    private static final String TAG = ClipAnimatorLayoutManager.class.getSimpleName();

    private ClipPathView leavingView;

    private ClipPathTracker tracker;

    private boolean randomEffect = false;
    private ClipPathTracker.Mode[] randomPool;

    public ClipAnimatorLayoutManager(Context context, RecyclerView rv) {
        super(context, rv);
    }

    public ClipAnimatorLayoutManager(Context context, int orientation, boolean reverseLayout, RecyclerView rv) {
        super(context, orientation, reverseLayout, rv);
    }

    @Override
    protected void initThings(Context context) {
        super.initThings(context);
        leavingView = new ClipPathView(context);
    }

    public void setTracker(ClipPathTracker tracker) {
        this.tracker = tracker;
    }

    public void setRandomEffect(boolean randomEffect, ClipPathTracker.Mode... modes) {
        this.randomEffect = randomEffect;
        if(modes != null){
            randomPool = modes;
        }
    }

    @Override
    protected View complexExitView() {
        return leavingView;
    }

    @Override
    protected void beforeAttachComplexExitAnimation(View complexExitView, View view, Bitmap bitmap) {
        leavingView.setBitmap(bitmap != null ? bitmap : RYViewUtils.snapshot(view));
    }

    @Override
    protected void startMixedExitAnimationPlaying(View complexExitView, View view, Bitmap bitmap, Animator.AnimatorListener listener) {
        leavingView.setAnimatorListener(listener);
        tracker.setWidth(view.getWidth());
        tracker.setHeight(view.getHeight());

        if(randomEffect){
            if(randomPool != null){
                tracker.randomMode(randomPool);
            }else{
                tracker.randomMode();
            }
        }

        leavingView.startClipAnimator(tracker);
    }

}
