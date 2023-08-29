package com.sloth.animator.transform;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

public class Card3DSlipTransform extends AbsVerticalAndHorizTransform {

    @Override
    public void transformPage(@NonNull View view, float position) {
        if(isReverse()){
            transReverse(view, position);
        }else{
            transformNormal(view, position);
        }
    }

    private void transReverse(View view, float position) {
        //offset keep x or y still
        float stillDis = 0;
        if(isHorizontal()){
            stillDis = -view.getWidth() * position;
            view.setPivotX(view.getWidth());
            view.setPivotY(view.getHeight() / 2f);
        }else{
            stillDis = -view.getHeight() * position;
            view.setPivotX(view.getWidth() / 2f);
            view.setPivotY(view.getHeight());
        }

        if(position > 1 || position <= -1){
            float ratio = 1.0f;
            view.setScaleX(ratio);
            view.setScaleY(ratio);
            if(isHorizontal()){
                view.setRotationY(0f);
                view.setTranslationX(stillDis);
            }else{
                view.setRotationX(0f);
                view.setTranslationY(stillDis);
            }
            view.setAlpha(0);
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {

                if(isHorizontal()){
                    view.setTranslationX(stillDis - view.getWidth() * position);
                }else{
                    view.setTranslationY(stillDis - view.getHeight() * position);
                }
                view.setScaleX(1f);
                view.setScaleY(1f);
                view.setRotationX(0f);
                view.setRotationY(0f);
                view.setAlpha(1 + position);
                ViewCompat.setTranslationZ(view, 1f);
            }else if(position <= 1) {
                float ratio = 1f - 0.5f * position;
                view.setScaleX(ratio);
                view.setScaleY(ratio);
                if(isHorizontal()){
                    view.setTranslationX(stillDis);
                    view.setRotationY(-30f * position);
                }else{
                    view.setTranslationY(stillDis);
                    view.setRotationX(-30f * position);
                }
                view.setAlpha(1 - position);
                ViewCompat.setTranslationZ(view, 0f);
            }
        }
    }

    private void transformNormal(View view, float position) {
        //offset keep x or y still
        if(isHorizontal()){
            view.setTranslationX(-view.getWidth() * position);
            view.setPivotX(view.getWidth());
            view.setPivotY(view.getHeight() / 2f);
        }else{
            view.setTranslationY(-view.getHeight() * position);
            view.setPivotX(view.getWidth() / 2f);
            view.setPivotY(view.getHeight());
        }

        if(position > 1 || position <= -1){
            float ratio = 1.0f;
            view.setScaleX(ratio);
            view.setScaleY(ratio);
            if(isHorizontal()){
                view.setRotationY(0f);
            }else{
                view.setRotationX(0f);
            }
            view.setAlpha(0);
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {
                if(isHorizontal()){
                    view.setTranslationX(0f);
                }else{
                    view.setTranslationY(0f);
                }
                view.setScaleX(1f);
                view.setScaleY(1f);
                view.setRotationX(0f);
                view.setRotationY(0f);
                view.setAlpha(1 + position);
                ViewCompat.setTranslationZ(view, 1f);
            }else if(position <= 1) {
                float ratio = 1f - 0.5f * position;
                view.setScaleX(ratio);
                view.setScaleY(ratio);
                if(isHorizontal()){
                    view.setRotationY(-30f * position);
                }else{
                    view.setRotationX(-30f * position);
                }
                view.setAlpha(1 - position);
                ViewCompat.setTranslationZ(view, 0f);
            }
        }
    }
}
