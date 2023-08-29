package com.sloth.animator.transform;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import androidx.viewpager2.widget.ViewPager2;

public class EraseTransform extends AbsVerticalAndHorizTransform {

    @Override
    public void transformPage(@NonNull View view, float position) {
        //offset keep x or y still
        if(isHorizontal()){
            view.setTranslationX(-view.getWidth() * position);
        }else{
            view.setTranslationY(-view.getHeight() * position);
        }

        Rect rect = new Rect();
        rect.set(0,0, view.getWidth(), view.getHeight());

        if(position > 1 || position <= -1){
            //others still
            ViewCompat.setTranslationZ(view, -1f);
        }else {
            if(position <= 0) {
                //erase out
                if(isHorizontal()){
                    rect.right = view.getWidth() + (int) (view.getWidth() * position);
                }else{
                    rect.bottom = view.getHeight() + (int) (view.getHeight() * position);
                }
                ViewCompat.setTranslationZ(view, 1f);
            }else if(position <= 1) {
                //in
                ViewCompat.setTranslationZ(view, 0f);
            }
        }
        ViewCompat.setClipBounds(view, rect);
    }
}
