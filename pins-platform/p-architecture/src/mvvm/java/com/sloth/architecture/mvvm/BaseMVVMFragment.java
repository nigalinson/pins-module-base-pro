package com.sloth.architecture.mvvm;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.sloth.architecture.BaseFragment;

/**
 * Author:
 * Version    V1.0
 * Date:      2021/7/30
 * Description:MVVMFragment基类
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2021/7/30                  1.0                    1.0
 * Why & What is modified:
 */
public abstract class BaseMVVMFragment<V extends BaseViewModel> extends BaseFragment {

    private ViewModelProvider parentViewModelProvider;
    protected V viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        parentViewModelProvider = new ViewModelProvider(requireActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        parentViewModelProvider = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        initViewModel();
        return rootView;
    }

    private void initViewModel(){
        viewModel = new ViewModelProvider(this).get(viewModelClass());
        onViewModelInit();
    }

    protected abstract Class<V> viewModelClass();

    protected void onViewModelInit(){ }

    protected <FB extends ViewModel> FB getParentViewModel(Class<FB> clz){
        if(parentViewModelProvider == null)
            throw new RuntimeException("请在 onAttach() 后调用！");
        return parentViewModelProvider.get(clz);
    }

}
