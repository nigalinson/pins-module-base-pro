package com.sloth.widget.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.PagerAdapter;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePagerAdapter<T> extends PagerAdapter {

    private final List<View> mViews = new ArrayList<>();
    private final List<T> mList = new ArrayList<>();

    public BasePagerAdapter() { }

    public BasePagerAdapter(List<T> list) {
        setList(list);
    }

    public void clean() {
        mList.clear();
    }

    public void setList(List<T> list) {
        this.mList.clear();
        this.mList.addAll(list);
    }

    public T getData(int position){
        return position >= 0 && position < mList.size() ? mList.get(position) : null;
    }

    public abstract View createItemView(int position, T data);

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(createItemView(position, getData(position)));
        return mList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
    }
}
