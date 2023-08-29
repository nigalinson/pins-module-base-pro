package com.sloth.architecture.mvp;

import android.content.Context;

public class DefaultCase extends BaseCase {

    public DefaultCase() { }

    public DefaultCase(BaseView baseView) {
        super(baseView);
    }

    public DefaultCase(Context context, BaseView baseView) {
        super(context, baseView);
    }

}
