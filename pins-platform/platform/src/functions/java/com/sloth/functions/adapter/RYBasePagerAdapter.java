package com.sloth.functions.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:      hs
 * Version      V1.0
 * Date:        2017/8/7
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2017/5/25         hs            1.0                    1.0
 * Why & What is modified:
 */
public abstract class RYBasePagerAdapter<T> extends PagerAdapter {
    private final List<T> mList = new ArrayList<>();

    public RYBasePagerAdapter() { }

    public RYBasePagerAdapter(List<T> list) {
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

    public abstract View getView(int position);

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
        container.addView(getView(position));
        return mList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
