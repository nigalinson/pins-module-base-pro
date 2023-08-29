package com.sloth.architecture;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    protected abstract int getFragmentLayoutRes();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getArgumentsForFragmentIntent(getArguments());
        }
    }

    /**
     * 获取 Fragment 传递的数据
     *
     * @param bundle Fragment Bundle
     */
    protected void getArgumentsForFragmentIntent(Bundle bundle) { }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        beforeSetContentView(savedInstanceState);
        return onSetContentView(inflater, container, getFragmentLayoutRes());
    }

    protected void beforeSetContentView(Bundle savedInstanceState) { }

    protected View onSetContentView(LayoutInflater inflater, ViewGroup container, int layoutId) {
        return inflater.inflate(layoutId, container, false);
    }

}
