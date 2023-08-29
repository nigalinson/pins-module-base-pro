package com.sloth.widget.hotarea;

import android.content.res.Resources;

public interface HotAreaTarget {
    HotAreaProxy getHotAreaProxy();
    Resources getResource();
    void setWillNotDraw(boolean willNotDraw);
    void invalidate();
}
