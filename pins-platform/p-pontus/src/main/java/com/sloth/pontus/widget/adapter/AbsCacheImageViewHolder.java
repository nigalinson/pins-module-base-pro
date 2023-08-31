package com.sloth.pontus.widget.adapter;

import android.view.View;
import com.sloth.functions.listeners.OnlineResource;
import com.sloth.pontus.widget.imageview.AbsCacheImageView;

public abstract class AbsCacheImageViewHolder<T extends OnlineResource> extends AbsCacheViewHolder<T> {

    protected AbsCacheImageView cacheImageView;

    public AbsCacheImageViewHolder(View itemView) {
        super(itemView);
        cacheImageView = inflateImageView(itemView);
    }

    protected abstract AbsCacheImageView inflateImageView(View itemView);

    @Override
    public void bindViewData(T data) {
        if(cacheImageView != null){
            cacheImageView.loadUrl(data.url(), cacheImageView.getWidth(), cacheImageView.getHeight());
        }
    }

    @Override
    public void detach() {
        if(cacheImageView != null){
            cacheImageView.detach();
            cacheImageView = null;
        }
    }
}
