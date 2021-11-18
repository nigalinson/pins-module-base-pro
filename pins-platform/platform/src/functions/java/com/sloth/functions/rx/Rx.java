package com.sloth.functions.rx;

import com.rongyi.common.functions.http.options.RetryWithDelay;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:    Carl
 * Version    V1.0
 * Date:      2020/9/29 17:45
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 2020/9/29         Carl            1.0                    1.0
 * Why & What is modified:
 */
public class Rx {

    //IO transform
    public static final ObservableTransformer IO_TRANSFORM = upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    //main transform
    public static final ObservableTransformer MAIN_TRANSFORM = upstream -> upstream.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());

    //default retry setting
    public static final Function RETRY_THREE_TIMES = new RetryWithDelay(3/*重试3次*/, 3000/*重试间隔*/);

    /**
     * 生成retry
     * @param times
     * @param interval
     * @return
     */
    public static Function retry(int times, int interval){
        return new RetryWithDelay(times, interval);
    }

    /**
     * 无意义dispose对象
     */
    public static Disposable NOTHING(){
        return new Disposable() {
            @Override
            public void dispose() { }

            @Override
            public boolean isDisposed() { return true; }
        };
    }

}
