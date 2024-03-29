package com.sloth.animator.vivid.effects;

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
 * 中心旋转（逆时针）
 */
public class Rotate implements Effect {

    private boolean clockwise = true;

    public Rotate() {
    }

    public Rotate(boolean clockwise) {
        this.clockwise = clockwise;
    }

    @Override
    public Animator create(View target, long dur) {
        target.post(() -> {
            int width = target.getWidth();
            int height = target.getHeight();
            target.setPivotX(width / 2f);
            target.setPivotY(height / 2f);
        });
        ValueAnimator animator = ValueAnimator.ofFloat(
                0f, (clockwise ? 360f : -360f)
        );
        animator.setDuration(dur);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> target.setRotation((Float) animation.getAnimatedValue()));
        return animator;
    }
}
