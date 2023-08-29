package com.sloth.animator.transform;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

public class FadeTransform extends AbsVerticalAndHorizTransform {

    @Override
    public void transformPage(@NonNull View view, float position) {
        //offset keep x or y still
        if(isHorizontal()){
            view.setTranslationX(-view.getWidth() * position);
        }else{
            view.setTranslationY(-view.getHeight() * position);
        }

        if(position > 1 || position <= -1){
            view.setAlpha(0.0f);
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {
                view.setAlpha(1.0f + position);
                ViewCompat.setTranslationZ(view, 1f);
            }else if(position <= 1) {
                view.setAlpha(1.0f - position);
                ViewCompat.setTranslationZ(view, 0f);
            }
        }
    }
}
