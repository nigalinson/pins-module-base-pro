package com.sloth.widget.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {
    private final ArrayList<Fragment> mFragments;
    private String[] mTitles;

    public BaseFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        //懒加载
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments = fragments;
    }

    public BaseFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments, String[] titles) {
        //懒加载
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments = fragments;
        mTitles = titles;
    }

    public void setTitles(String[] titles) {
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles == null ? "" : mTitles[position];
    }
}
