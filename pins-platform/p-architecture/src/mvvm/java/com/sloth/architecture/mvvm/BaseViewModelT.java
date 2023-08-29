package com.sloth.architecture.mvvm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public abstract class BaseViewModelT<T> extends BaseViewModel {

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> | 主数据源 | <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private final MutableLiveData<T> mData = new MutableLiveData<>();

    public LiveData<T> getData(){
        return mData;
    }

    public void postData(T obj){
        mData.postValue(obj);
    }

    public void setData(T obj){
        mData.setValue(obj);
    }

}
