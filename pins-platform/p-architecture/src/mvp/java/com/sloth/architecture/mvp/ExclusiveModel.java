package com.sloth.architecture.mvp;

import android.content.Context;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import com.sloth.rx.Rx;
import com.sloth.platform.Platform;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 支持进行排他性请求
 * （同一时间一个Model实例中，只允许存在某KEY的一次请求）
 */
public class ExclusiveModel extends BaseModel {

    private int hit(int key){
        if(key > 30 || key < 0){ return 0; }

        return bloom>>key&1;
    }

    private void put(int key, boolean running){
        if(key > 30 || key < 0){ return; }
        if(running){
            bloom |= (1<<key);
        }else{
            bloom |= (1<<key);
            bloom ^= (1<<key);
        }
    }

    /**
     * 将key记录到32位的 Bloom 参数中，节约内存
     */
    private int bloom = 0;

    public ExclusiveModel() { }

    public ExclusiveModel(Context context) {
        super(context);
    }

    /**
     * 独占运行
     * @param exclusiveKey 独占key
     * @param request 请求
     * @param apiCallback 回调
     */
    protected <O, T> void executeHTTPExclusive(@IntRange(from = 0, to = 30) int exclusiveKey,
                                               Observable<O> request,
                                               Observer<T> apiCallback){
        if(hit(exclusiveKey) == 1){
            Platform.log().d(TAG, "请求：" + exclusiveKey + "已在进行中,不重复请求");
            return;
        }

        put(exclusiveKey, true);

        Rx.delegate(request).ui().retry3Time().online().execute(new ExclusiveObserver<T>(apiCallback){

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                super.onSubscribe(d);
                execute(d);
            }

            @Override
            public void onExclusiveEnd() {
                put(exclusiveKey, false);
            }
        });
    }

    /**
     * 独占运行
     * @param exclusiveKey 独占key
     * @param request 请求
     * @param dataTransform 数据转型
     * @param apiCallback 回调
     */
    protected <O, T> void executeHTTPExclusive(@IntRange(from = 0, to = 30) int exclusiveKey,
                                               Observable<O> request,
                                               ObservableTransformer<O,T> dataTransform,
                                               Observer<T> apiCallback){
        if(hit(exclusiveKey) == 1){
            Platform.log().d(TAG, "请求：" + exclusiveKey + "已在进行中,不重复请求");
            return;
        }

        put(exclusiveKey, true);

        Rx.delegate(request).ui().retry3Time().online().dataTransform(dataTransform).execute(new ExclusiveObserver<T>(apiCallback){

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                super.onSubscribe(d);
                execute(d);
            }

            @Override
            public void onExclusiveEnd() {
                put(exclusiveKey, false);
            }
        });
    }

    /**
     * 独占运行
     * @param exclusiveKey 独占key
     * @param request 请求
     * @param apiCallback 回调
     */
    protected <O, T> void executeExclusive(@IntRange(from = 0, to = 30) int exclusiveKey,
                                           Observable<O> request,
                                           Observer<T> apiCallback){
        if(hit(exclusiveKey) == 1){
            Platform.log().d(TAG, "请求：" + exclusiveKey + "已在进行中,不重复请求");
            return;
        }
        put(exclusiveKey, true);
        Rx.delegate(request).ui().execute(new ExclusiveObserver<T>(apiCallback){

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                super.onSubscribe(d);
                execute(d);
            }

            @Override
            public void onExclusiveEnd() {
                put(exclusiveKey, false);
            }
        });
    }

    /**
     * 独占运行
     * @param exclusiveKey 独占key
     * @param request 请求
     * @param dataTransform 数据转型
     * @param apiCallback 回调
     */
    protected <O, T> void executeExclusive(@IntRange(from = 0, to = 30) int exclusiveKey,
                                           Observable<O> request,
                                           ObservableTransformer<O,T> dataTransform,
                                           Observer<T> apiCallback){
        if(hit(exclusiveKey) == 1){
            Platform.log().d(TAG, "请求：" + exclusiveKey + "已在进行中,不重复请求");
            return;
        }

        put(exclusiveKey, true);

        Rx.delegate(request).ui().dataTransform(dataTransform).execute(new ExclusiveObserver<T>(apiCallback){

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                super.onSubscribe(d);
                execute(d);
            }

            @Override
            public void onExclusiveEnd() {
                put(exclusiveKey, false);
            }
        });
    }

    @Override
    public void destroy() {
        super.destroy();
        bloom = 0;
    }
}
