package com.sloth.functions.http;

import com.sloth.platform.Platform;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class ResponseTransform<T> implements ObservableTransformer<T, T> {

    private final String TAG = ResponseTransform.class.getSimpleName();

    private static final int REQUEST_TIMEOUT = 408;
    private static final int GATEWAY_TIMEOUT = 504;

    @Override
    public ObservableSource<T> apply(Observable<T> httpResultObservable) {
        return httpResultObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .lift(observer -> new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        observer.onSubscribe(d);
                    }

                    @Override
                    public void onNext(T result) {
                        observer.onNext(result);
                        observer.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Throwable throwable = e;
                        //获取最根源的异常
                        while (throwable.getCause() != null) {
                            e = throwable;
                            throwable = throwable.getCause();
                        }
                        if (e instanceof HttpException) {
                            HttpException httpException = (HttpException) e;
                            Platform.log().e(TAG, "网络异常: " + httpException.code());
                            switch (httpException.code()) {
                                case REQUEST_TIMEOUT:
                                case GATEWAY_TIMEOUT: {
                                    observer.onError(new HttpError(HttpError.CODE_NET_TIMEOUT, HttpError.MSG_NET_TIMEOUT));
                                }
                                break;
                                default:
                                    observer.onError(new HttpError(HttpError.CODE_NET_DISCONNECT, HttpError.MSG_NET_DISCONNECT));
                                    break;
                            }
                        } else if (e instanceof ConnectException || e instanceof SocketTimeoutException) {
                            Platform.log().e(TAG, "网络超时: " + e.getMessage());
                            observer.onError(new HttpError(HttpError.CODE_NET_TIMEOUT, HttpError.MSG_NET_TIMEOUT));
                        } else {
                            Platform.log().e(TAG, "网络异常: " + e.getMessage());
                            observer.onError(new HttpError(HttpError.CODE_NET_DISCONNECT, e.getMessage()));
                        }
                    }

                    @Override
                    public void onComplete() { }
                });
    }

}