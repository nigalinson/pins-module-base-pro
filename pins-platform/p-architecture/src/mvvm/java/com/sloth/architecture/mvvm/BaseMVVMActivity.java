package com.sloth.architecture.mvvm;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.sloth.architecture.BaseActivity;

public abstract class BaseMVVMActivity<V extends BaseViewModel> extends BaseActivity {

    protected V viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
    }

    private void initViewModel(){
        viewModel = new ViewModelProvider(this).get(viewModelClass());
        onViewModelInit();
    }

    protected abstract Class<V> viewModelClass();

    protected void onViewModelInit(){ }

}
