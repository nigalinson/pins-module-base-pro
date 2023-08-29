package com.sloth.architecture.mvp;

import android.content.Context;

public abstract class BasePagePresenter<V extends BasePageView> extends BasePresenter<V> {

    protected final Paging paging = new Paging();

    public BasePagePresenter(V view) {
        super(view);
    }

    public BasePagePresenter(Context context, V view) {
        super(context, view);
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
