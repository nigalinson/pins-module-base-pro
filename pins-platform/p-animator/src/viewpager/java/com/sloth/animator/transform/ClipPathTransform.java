package com.sloth.animator.transform;

import android.content.Context;
import androidx.core.view.ViewCompat;

import com.sloth.animator.transform.drawer.BaseDrawerTransform;
import com.sloth.animator.transform.drawer.Drawer;

public class ClipPathTransform extends BaseDrawerTransform {

    private static final String TAG = ClipPathTransform.class.getSimpleName();

    private final int canvasWidth, canvasHeight;

    private int mode;

    public ClipPathTransform(int w, int h) {
        this(w,h, ClipPathDrawer.Mode.WINDOW_OPEN_VERTICAL.code);
    }

    public ClipPathTransform(int w, int h, int mode) {
        canvasWidth = w;
        canvasHeight = h;
        this.mode = mode;
    }

    @Override
    protected Drawer createDrawer(Context context) {
        ClipPathDrawer clipPathDrawer = new ClipPathDrawer(mode, 1200);
        clipPathDrawer.setWidth(canvasWidth);
        clipPathDrawer.setHeight(canvasHeight);
        return clipPathDrawer;
    }

    @Override
    protected void transforming(DrawerDecor view, float position) {
        view.setTranslationX(-view.getWidth() * position);

        if(position > 1 || position <= -1){
            ViewCompat.setTranslationZ(view, -1f);
            view.dispatchProgress(0);
        }else if(position <= 0){
            ViewCompat.setTranslationZ(view, 1f);
            view.dispatchProgress(Math.abs(position));
        }else if(position <= 1){
            ViewCompat.setTranslationZ(view, 0f);
            view.dispatchProgress(0);
        }
    }
}
