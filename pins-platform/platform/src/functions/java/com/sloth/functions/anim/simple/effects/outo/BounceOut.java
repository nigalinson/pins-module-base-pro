package com.sloth.functions.anim.simple.effects.outo;

import android.animation.Animator;
import android.animation.AnimatorSet;
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
 * 中心晃动
 */
public class BounceOut implements Effect {

    @Override
    public Animator create(View target, long dur) {

        ValueAnimator scaleXPart = ValueAnimator.ofFloat(
                   1f, 0.9f, 1.1f, 0.8f, 1.2f, 0f
        );
        scaleXPart.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXPart.addUpdateListener(animation -> target.setScaleX((Float) animation.getAnimatedValue()));

        ValueAnimator scaleYPart = ValueAnimator.ofFloat(
                1f, 0.9f, 1.1f, 0.8f, 1.2f, 0f
        );
        scaleYPart.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYPart.addUpdateListener(animation -> target.setScaleY((Float) animation.getAnimatedValue()));

        AnimatorSet set = new AnimatorSet();
        set.setDuration(dur);
        set.playTogether(scaleXPart, scaleYPart);

        return set;
    }
}