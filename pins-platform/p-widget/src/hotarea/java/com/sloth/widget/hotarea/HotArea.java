package com.sloth.widget.hotarea;

import android.graphics.Rect;
import android.graphics.RectF;

public class HotArea extends RectF {
    public HotArea() {
    }

    public HotArea(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
    }

    public HotArea(RectF r) {
        super(r);
    }

    public HotArea(Rect r) {
        super(r);
    }
}
