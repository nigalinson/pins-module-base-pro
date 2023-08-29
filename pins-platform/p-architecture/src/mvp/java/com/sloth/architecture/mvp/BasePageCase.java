package com.sloth.architecture.mvp;

import android.content.Context;

public abstract class BasePageCase<V extends BasePageView> extends BaseCase<V> {

    protected final Paging paging = new Paging();

    public BasePageCase() { }

    public BasePageCase(V v) {
        super(v);
    }

    public BasePageCase(Context context, V v) {
        super(context, v);
    }

    public void refresh(){
        paging.head();
        loadData(true, paging.getNowPageIndex(), paging.getPageSize());
    }

    public void loadMore(){
        paging.next();
        loadData(false, paging.getNowPageIndex(), paging.getPageSize());
    }

    protected abstract void loadData(boolean refresh, int pageIndex, int pageSize);

}
