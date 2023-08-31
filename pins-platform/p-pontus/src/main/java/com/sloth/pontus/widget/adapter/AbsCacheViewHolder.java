package com.sloth.pontus.widget.adapter;

import android.view.View;
import com.sloth.widget.adapter.BaseViewHolder;

public abstract class AbsCacheViewHolder<T> extends BaseViewHolder<T> {

    public AbsCacheViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void detach();

}
