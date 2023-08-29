package com.sloth.animator.transform;

import android.view.View;
import androidx.annotation.NonNull;

public class Rotate3DTransform extends AbsVerticalAndHorizTransform {

    @Override
    public void transformPage(@NonNull View view, float position) {

        float stillDis = 0;
        if(isHorizontal()){
            stillDis = -view.getWidth() * position;
        }else{
            stillDis = -view.getHeight() * position;
        }

        if(position > 1 || position <= -1){
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            if(isHorizontal()){
                view.setRotationY(0.0f);
            }else{
                view.setRotationX(0.0f);
            }
            view.setAlpha(0.0f);
        }else {
            if(position <= 0) {
                float ratio = 1f + 0.3f * dob(position);
                view.setScaleX(ratio);
                view.setScaleY(ratio);
                if(isHorizontal()){
                    view.setRotationY(90f * dob(position));
                    view.setTranslationX(stillDis + (dob(position) * view.getWidth() / 2)/* - (0.3f * dob(position) * view.getWidth())*/);
                }else{
                    view.setRotationX(-90f * dob(position));
                    view.setTranslationY(stillDis + (dob(position) * view.getHeight() / 2)/* - (0.3f * dob(position) * view.getHeight())*/);
                }
            }else if(position <= 1) {
                float ratio = 1.0f - 0.3f * dob(position);
                view.setScaleX(ratio);
                view.setScaleY(ratio);
                if(isHorizontal()){
                    view.setRotationY(90f * dob(position));
                    view.setTranslationX(stillDis + (dob(position) * view.getWidth() / 2)/* - (0.3f * dob(position) * view.getWidth())*/);
                }else{
                    view.setRotationX(-90f * dob(position));
                    view.setTranslationY(stillDis + (dob(position) * view.getHeight() / 2)/* - (0.3f * dob(position) * view.getHeight())*/);
                }
            }
            view.setAlpha(1.0f);
        }
    }

    private float dob(float position) {
        if(position == 0) return 0;
        return (float) (Math.sqrt(Math.abs(position)) * (position / Math.abs(position)));
    }
}
