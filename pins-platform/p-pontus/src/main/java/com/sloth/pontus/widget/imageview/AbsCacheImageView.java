package com.sloth.pontus.widget.imageview;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.lifecycle.LifecycleObserver;

public abstract class AbsCacheImageView extends AppCompatImageView implements CacheImageProxy.Owner, LifecycleObserver {

    private final CacheImageProxy cacheImageProxy;

    public AbsCacheImageView(Context context) {
        this(context, null);
    }

    public AbsCacheImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AbsCacheImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        cacheImageProxy = createCacheAbility(this);
    }

    protected abstract CacheImageProxy createCacheAbility(AbsCacheImageView iv);

    @Override
    public void loadUrl(String url) {
        cacheImageProxy.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, int w, int h) {
        cacheImageProxy.loadUrl(url, w, h);
    }

    @Override
    public void loadUrl(String url, String local) {
        cacheImageProxy.loadUrl(url, local);
    }

    @Override
    public void loadUrl(String url, String local, int w, int h) {
        cacheImageProxy.loadUrl(url, local, w, h);
    }

    @Override
    public void setImportResource(boolean isImportant) {
        cacheImageProxy.setImportantResource(isImportant);
    }

    @Override
    public void setCancelOldWhenReset(boolean cancelOld) {
        cacheImageProxy.setCancelOldWhenReset(cancelOld);
    }

    @Override
    public void autoAdjust(boolean adjust, int anchor) {
        cacheImageProxy.autoAdjust(adjust, anchor);
    }

    @Override
    public void setSupportAlpha(boolean supportAlpha) {
        cacheImageProxy.setSupportAlpha(supportAlpha);
    }

    @Override
    public void cancelLoad() {
        cacheImageProxy.cancelLoad();
    }

    @Override
    public void detach() {
        setImageDrawable(null);
        cacheImageProxy.detach();
    }

}
