package com.sloth.widget.hotarea;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

public class HotAreaImageView extends AppCompatImageView implements HotAreaTarget {

    private final HotAreaProxy hotAreaProxy;

    public HotAreaImageView(Context context) {
        this(context, null);
    }

    public HotAreaImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public HotAreaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        hotAreaProxy = new HotAreaProxy(this);
    }

    @Override
    public HotAreaProxy getHotAreaProxy() {
        return hotAreaProxy;
    }

    @Override
    public Resources getResource() {
        return getContext().getResources();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return hotAreaProxy.onTouchEvent(event)|super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        hotAreaProxy.onDraw(canvas);
    }
}
