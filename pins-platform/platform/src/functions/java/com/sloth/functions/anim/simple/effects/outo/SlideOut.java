package com.sloth.functions.anim.simple.effects.outo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.rongyi.common.animator.remind.Effect;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2021/6/4 17:58
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/6/4         Carl            1.0                    1.0
 * Why & What is modified:
 * 滑轨
 */
public class SlideOut implements Effect {

    @Override
    public Animator create(View target, long dur) {
        ValueAnimator animator = ValueAnimator.ofFloat(
                0f, -1080f
                );
        animator.setDuration(dur * 2);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> target.setTranslationX((Float) animation.getAnimatedValue()));
        return animator;
    }
}
