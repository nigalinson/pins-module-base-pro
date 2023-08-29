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
import com.sloth.architecture.BaseFragmentDialog;

public abstract class BaseMVVMFragmentDialog<V extends BaseViewModel>
        extends BaseFragmentDialog {

    private static final String TAG = BaseMVVMFragmentDialog.class.getSimpleName();

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        initViewModel();
        return rootView;
    }

    private void initViewModel(){
        viewModel = new ViewModelProvider(this).get(getModelClass());
        onViewModelInit();
    }

    protected abstract Class<V> getModelClass();

    protected void onViewModelInit(){ }

    protected <FB extends ViewModel> FB getParentViewModel(Class<FB> clz){
        if(parentViewModelProvider == null)
            throw new RuntimeException("请在 onAttach() 后调用！");
        return parentViewModelProvider.get(clz);
    }

}
