package com.sloth.architecture.mvp;

import android.os.Bundle;
import androidx.annotation.Nullable;
import com.sloth.architecture.BaseFragment;

public abstract class BaseMvpFragment<P extends BasePresenter> extends BaseFragment {
    protected P mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if(savedInstanceState != null){
            if(mPresenter != null){
                mPresenter.onSaveInstanceRestored(savedInstanceState);
            }
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(mPresenter != null){
            mPresenter.onRestoreInstance(savedInstanceState);
        }
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        super.onDestroy();
    }

    protected abstract P createPresenter();
}
