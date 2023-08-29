package com.sloth.architecture.mvp;

import com.sloth.platform.constants.Constants;

public class Paging {


    private int firstPageIndex;
    private int nowPageIndex;
    private int pageSize;

    public Paging() {
        this(Constants.PageConstants.START_INDEX, Constants.PageConstants.START_INDEX, Constants.PageConstants.PAGE_SIZE);
    }

    public Paging(int pageSize) {
        this(Constants.PageConstants.START_INDEX, Constants.PageConstants.START_INDEX, pageSize);
    }

    public Paging(int firstPageIndex, int pageSize) {
        this(firstPageIndex, firstPageIndex, pageSize);
    }

    public Paging(int firstPageIndex, int nowPageIndex, int pageSize) {
        this.firstPageIndex = firstPageIndex;
        this.nowPageIndex = nowPageIndex;
        this.pageSize = pageSize;
    }

    public int getFirstPageIndex() {
        return firstPageIndex;
    }

    public void setFirstPageIndex(int firstPageIndex) {
        this.firstPageIndex = firstPageIndex;
    }

    public int getNowPageIndex() {
        return nowPageIndex;
    }

    public void setNowPageIndex(int nowPageIndex) {
        this.nowPageIndex = nowPageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int head(){
        nowPageIndex = firstPageIndex;
        return nowPageIndex;
    }

    public int next(){
        return ++nowPageIndex;
    }

}
