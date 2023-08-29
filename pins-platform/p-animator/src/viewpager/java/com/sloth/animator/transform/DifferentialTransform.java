package com.sloth.animator.transform;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

public class DifferentialTransform extends AbsVerticalAndHorizTransform {

    @Override
    public void transformPage(@NonNull View view, float position) {
        if(isReverse()){
            transformReverse(view, position);
        }else{
            transformNormal(view, position);
        }
    }

    private void transformReverse(View view, float position) {
        //offset keep x or y still
        float stillDistance = 0;
        if(isHorizontal()){
            stillDistance = -view.getWidth() * position;
        }else{
            stillDistance = -view.getHeight() * position;
        }

        if(position > 1 || position <= -1){
            view.setAlpha(0f);
            if(isHorizontal()){
                stillDistance += (view.getWidth() * 0.3f);
                view.setTranslationX(stillDistance);
            }else{
                stillDistance += (view.getHeight() * 0.3f);
                view.setTranslationY(stillDistance);
            }
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {
                view.setAlpha(1.0f + position);
                if(isHorizontal()){
                    stillDistance -= (view.getWidth() * 0.3f * position);
                    view.setTranslationX(stillDistance);
                }else{
                    stillDistance -= (view.getHeight() * 0.3f * position);
                    view.setTranslationY(stillDistance);
                }
                ViewCompat.setTranslationZ(view, 0f);
            }else if(position <= 1) {
                view.setAlpha(1.0f - position);
                if(isHorizontal()){
                    stillDistance -= (view.getWidth() * position);
                    view.setTranslationX(stillDistance);
                }else{
                    stillDistance -= (view.getHeight() * position);
                    view.setTranslationY(stillDistance);
                }
                ViewCompat.setTranslationZ(view, 1f);
            }
        }
    }

    private void transformNormal(View view, float position) {
        //offset keep x or y still
        float stillDistance = 0;
        if(isHorizontal()){
            stillDistance = -view.getWidth() * position;
        }else{
            stillDistance = -view.getHeight() * position;
        }

        if(position > 1 || position <= -1){
            view.setAlpha(0f);
            if(isHorizontal()){
                stillDistance += (view.getWidth() * 0.3f);
                view.setTranslationX(stillDistance);
            }else{
                stillDistance += (view.getHeight() * 0.3f);
                view.setTranslationY(stillDistance);
            }
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {
                view.setAlpha(1.0f + position);
                if(isHorizontal()){
                    stillDistance += (view.getWidth() * position);
                    view.setTranslationX(stillDistance);
                }else{
                    stillDistance += (view.getHeight() * position);
                    view.setTranslationY(stillDistance);
                }
                ViewCompat.setTranslationZ(view, 1f);
            }else if(position <= 1) {
                view.setAlpha(1.0f - position);
                if(isHorizontal()){
                    stillDistance += (view.getWidth() * 0.3f * position);
                    view.setTranslationX(stillDistance);
                }else{
                    stillDistance += (view.getHeight() * 0.3f * position);
                    view.setTranslationY(stillDistance);
                }
                ViewCompat.setTranslationZ(view, 0f);
            }
        }
    }
}
