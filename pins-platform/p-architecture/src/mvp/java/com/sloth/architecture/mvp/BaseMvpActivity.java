package com.sloth.architecture.mvp;

import android.os.Bundle;
import androidx.annotation.Nullable;

import com.sloth.architecture.BaseActivity;

public abstract class BaseMvpActivity<P extends BasePresenter> extends BaseActivity {

    protected P mPresenter;

    protected abstract P createPresenter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if(savedInstanceState != null){
            if(mPresenter != null){
                mPresenter.onSaveInstanceRestored(savedInstanceState);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(mPresenter != null){
            mPresenter.onRestoreInstance(savedInstanceState);
        }
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null) {
            mPresenter.destroy();
        }
        super.onDestroy();
    }
}
