package com.sloth.architecture.mvvm;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sloth.utils.livedata.UnPeekLiveData;
import com.sloth.rx.ProxyObserver;
import com.sloth.rx.Rx;
import com.sloth.platform.Platform;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseViewModel extends ViewModel {

    protected final String TAG = BaseViewModel.class.getSimpleName();

    public BaseViewModel(){ }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> | 标准事件流 | <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private final UnPeekLiveData<VmAction> mAction = new UnPeekLiveData<>();

    public LiveData<VmAction> getAction(){
        return mAction;
    }

    public void postAction(VmAction act){
        mAction.postValue(act);
    }

    public void setAction(VmAction act){
        mAction.setValue(act);
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> | disposable | <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

    private CompositeDisposable mCompositeDisposable;
    private final Map<Integer, Disposable> disposableMap = new ConcurrentHashMap<>();

    /**
     * 直接注册disposable，需要外部保存以便手动注销
     * @param disposable
     */
    protected void execute(Disposable disposable){
        if(mCompositeDisposable == null){
            mCompositeDisposable = new CompositeDisposable();
        }
        if(disposable != null){
            mCompositeDisposable.add(disposable);
        }
    }

    protected void cancel(Disposable disposable){
        if(disposable != null && mCompositeDisposable != null){
            mCompositeDisposable.remove(disposable);
        }
    }

    /**
     * 使用code schedule的任务，手动注销时也需要使用code注销，否则会在map中留存
     * cancelAll的时候会全部注销
     * @param code
     * @param disposable
     */
    protected void execute(int code, Disposable disposable){
        if(disposable != null){
            if(code != -1){
                Disposable old = disposableMap.get(code);
                if(old != null && mCompositeDisposable != null){
                    mCompositeDisposable.remove(old);
                }
                disposableMap.put(code, disposable);
            }
            execute(disposable);
        }
    }

    protected void cancel(int code){
        if(code != -1){
            Disposable old = disposableMap.get(code);
            if(old != null){
                if(mCompositeDisposable != null){
                    mCompositeDisposable.remove(old);
                }
                disposableMap.remove(code);
            }
        }
    }

    protected <O, T> void execute(Observable<O> request, Observer<T> apiCallback){
        Rx.delegate(request).ui().execute(new ProxyObserver<T>(apiCallback) {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
                execute(d);
            }

            @Override
            protected void onUnSubscribe(Disposable disposable) {
                super.onUnSubscribe(disposable);
                cancel(disposable);
            }
        });
    }

    protected void delay(long delayTime, Observer<Long> observer){
        delay(-1, delayTime, observer);
    }

    protected void delay(int code, long delayTime, Observer<Long> observer){
        Observable.timer(delayTime, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProxyObserver<Long>(observer) {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        super.onSubscribe(disposable);
                        execute(code, disposable);
                    }

                    @Override
                    protected void onUnSubscribe(Disposable disposable) {
                        super.onUnSubscribe(disposable);
                        if(code != -1){
                            cancel(code);
                        }else{
                            cancel(disposable);
                        }
                    }
                });
    }

    protected void interval(long delayTime, long interval, Observer<Long> observer){
        interval(-1, delayTime, interval, observer);
    }

    protected void interval(int code, long delayTime, long interval, Observer<Long> observer){
        Observable.interval(delayTime, interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ProxyObserver<Long>(observer) {
                    @Override
                    public void onSubscribe(@NonNull Disposable disposable) {
                        super.onSubscribe(disposable);
                        execute(code, disposable);
                    }

                    @Override
                    protected void onUnSubscribe(Disposable disposable) {
                        super.onUnSubscribe(disposable);
                        if(code != -1){
                            cancel(code);
                        }else{
                            cancel(disposable);
                        }
                    }
                });
    }

    protected <T> void executeHTTP(Observable<T> request, Observer<T> apiCallback){
       Rx.delegate(request).ui().retry3Time().online().execute(new ProxyObserver<T>(apiCallback) {
            @Override
            public void onSubscribe(Disposable disposable) {
                super.onSubscribe(disposable);
                execute(disposable);
            }

            @Override
            protected void onUnSubscribe(Disposable disposable) {
                super.onUnSubscribe(disposable);
                cancel(disposable);
            }
        });
    }

    protected <O, T> void executeHTTP(Observable<O> request, ObservableTransformer<O,T> dataTransform, Observer<T> apiCallback){
        Rx.delegate(request).ui().retry3Time().online().dataTransform(dataTransform).execute(new ProxyObserver<T>(apiCallback) {
            @Override
            public void onSubscribe(Disposable disposable) {
                super.onSubscribe(disposable);
                execute(disposable);
            }

            @Override
            protected void onUnSubscribe(Disposable disposable) {
                super.onUnSubscribe(disposable);
                cancel(disposable);
            }
        });
    }

    public void cancelAll(){
        if(mCompositeDisposable != null && !mCompositeDisposable.isDisposed()){
            mCompositeDisposable.dispose();
            mCompositeDisposable = null;
        }
        disposableMap.clear();
    }

    protected boolean isSubscribed(int code){
        if(code != -1){
            return disposableMap.containsKey(code);
        }
        return false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Platform.log().i(TAG,"onCleared：ViewModel 即将销毁");
        cancelAll();
    }

}