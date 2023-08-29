package com.sloth.widget.adapter;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindViewData(T data);

    public void detach() { }
}
