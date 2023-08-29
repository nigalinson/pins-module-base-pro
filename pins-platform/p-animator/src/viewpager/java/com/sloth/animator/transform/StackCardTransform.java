package com.sloth.animator.transform;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

public class StackCardTransform extends AbsVerticalAndHorizTransform {

    private static final String TAG = StackCardTransform.class.getSimpleName();

    private static final float SCALE = 0.85f;
    private static final float OFFSET_RATIO = 0.1f;
    private static final int STEP_COUNT = 3;

    private int stepCount;
    private float stepScale;
    private float stepOffsetRatio;

    private float stepAlpha;

    public StackCardTransform() {
        this(STEP_COUNT);
    }

    public StackCardTransform(int stepCount) {
        this(stepCount, SCALE, OFFSET_RATIO);
    }

    public StackCardTransform(int stepCount, float stepScale, float stepOffsetRatio) {
        this.stepCount = stepCount;
        this.stepScale = stepScale;
        this.stepOffsetRatio = stepOffsetRatio;
        stepAlpha = 1f / stepCount;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        int w = view.getWidth();
        int h = view.getHeight();
        boolean isHorizon = isHorizontal();
        float transOffset = 0;
        //offset keep x or y still
        float translate = 0;
        float alpha = 1f;
        float scale = 1f;
        float z = 0f;

        if(isHorizon){
            translate = -w * position;
            transOffset = w * stepOffsetRatio;
        }else{
            translate = -h * position;
            transOffset = h * stepOffsetRatio;
        }

        translate += (transOffset * position);

        if(position <= -1){
            translate = 0f;
            alpha = 0f;
            z = -1f;
            scale = 1f;
        }else if(position <= 0){
            translate = 0f;
            alpha = 1f + position;
            z = stepCount;
            scale = 1f;
        }else{
            translate += (transOffset * position);

            //alpha
            // 1- (step * p)
            alpha = 1 - (stepAlpha * Math.abs(position));
            alpha = Math.min(Math.max(alpha, 0f), 1f);

            z = stepCount - (float)Math.ceil(position);

            //scale
            float startScale = (float)Math.pow(stepScale, Math.ceil(position));
            float endScale = (float)Math.pow(stepScale, Math.floor(position));
            scale = startScale + (endScale - startScale) * itemPercent(position);
        }

        //平移
        if(isHorizon){
            view.setTranslationX(translate);
        }else{
            view.setTranslationY(translate);
        }

        //透明度
        view.setAlpha(alpha);

        //Z軸
        ViewCompat.setTranslationZ(view, z);

        //scale

        view.setScaleX(scale);
        view.setScaleY(scale);
    }

    private float itemPercent(float position){
        float fractional = position - (float)Math.floor(position);
        return fractional == 0f ? 0f : (1f - fractional);
    }

}
