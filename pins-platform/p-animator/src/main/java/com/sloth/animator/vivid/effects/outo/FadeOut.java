package com.sloth.animator.vivid.effects.outo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.sloth.animator.vivid.Effect;

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
 * 闪光灯
 */
public class FadeOut implements Effect {

    @Override
    public Animator create(View target, long dur) {
        ValueAnimator animator = ValueAnimator.ofFloat(
                1f, 0f
        );
        animator.setDuration(dur);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> target.setAlpha((Float) animation.getAnimatedValue()));
        return animator;
    }
}
