package com.sloth.rx;

import com.sloth.functions.api.API;
import com.sloth.platform.constants.Constants;
import com.sloth.platform.Platform;
import com.sloth.utils.NetworkUtils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Rx {

    public static <T> Observable<T> run(Creator<T> creator){
        return Observable.create((ObservableOnSubscribe<T>) emitter -> {
            T data = creator.create();
            if(data != null){
                emitter.onNext(data);
                emitter.onComplete();
            }else{
                emitter.onError(new IllegalArgumentException("creator make null!"));
            }
        });
    }

    public static <T> Observable<T> io(Creator<T> creator){
        return run(creator).subscribeOn(Schedulers.io());
    }

    public static <T> Observable<T> ui(Creator<T> creator){
        return run(creator).compose(uiTransform());
    }

    public static <T> Observable<T> main(Creator<T> creator){
        return run(creator).subscribeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Boolean> run(Runner runner){
        return Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            runner.run();
            emitter.onNext(true);
            emitter.onComplete();
        });
    }

    public static Observable<Boolean> io(Runner runner){
        return run(runner).subscribeOn(Schedulers.io());
    }

    public static Observable<Boolean> ui(Runner runner){
        return run(runner).compose(uiTransform());
    }

    public static Observable<Boolean> main(Runner runner){
        return run(runner).subscribeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Long> interval(int interval){
        return interval(0, interval);
    }

    public static Observable<Long> interval(int delay, int interval){
        return Observable.interval(delay, interval, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Long> delay(int delay){
        return Observable.timer(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Long> repeat(int times, int interval){
        return Observable.just(0L)
                .repeatWhen(repeatFunc(times, interval))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> repeat(Creator<T> creator, int times, int interval){
        return Observable.create((ObservableOnSubscribe<T>) emitter -> {
            T data = creator.create();
            if(data != null){
                emitter.onNext(data);
                emitter.onComplete();
            }else{
                emitter.onError(new IllegalArgumentException("creator make null!"));
            }
        }).repeatWhen(repeatFunc(times, interval)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> retryWhenError(Creator<T> creator, int times, int interval){
        return Observable.create((ObservableOnSubscribe<T>) emitter -> {
            T data = creator.create();
            if(data != null){
                emitter.onNext(data);
                emitter.onComplete();
            }else{
                emitter.onError(new IllegalArgumentException("creator make null!"));
            }
        }).retryWhen(retryFunc(times, interval)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Function<? super Observable<Object>, ? extends ObservableSource<?>> repeatFunc(int times, int interval){
        return new RepeatFunc<>(times, interval);
    }

    public static Function<? super Observable<Throwable>, ? extends ObservableSource<?>> retry3TimesFunc(){
        return new RetryFunc(3/*重试3次*/, 3000/*重试间隔*/);
    }

    public static Function<? super Observable<Throwable>, ? extends ObservableSource<?>> retryFunc(int times, int interval){
        return new RetryFunc(times, interval);
    }

    public static <T> ObservableTransformer<T, T> ioTransform(){
        return (ObservableTransformer<T, T>) upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(Schedulers.io());
    }

    public static <T> ObservableTransformer<T, T> uiTransform(){
        return (ObservableTransformer<T, T>) upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> mainTransform(){
        return (ObservableTransformer<T, T>) upstream -> upstream.subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread());
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

    /**
     * 直接执行observable 并同步返回结果
     * @param observable
     * @param <T>
     * @return
     * @throws InterruptedException
     */
    public static <T> T waitUntil(Observable<T> observable) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<T> result = new AtomicReference<>();
        observable.subscribe(new Obx<T>() {
            @Override
            protected void onExe(T data) {
                super.onExe(data);
                result.set(data);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                countDownLatch.countDown();
            }
        });

        if(countDownLatch.getCount() != 0) countDownLatch.await();

        return result.get();
    }

    /**
     * 链式快速调用
     * @param observable
     * @return
     */
    public static Chain delegate(Observable observable){
        return new Chain(observable);
    }

    public static final class StoreProxy<A> {
        private final A store;

        public StoreProxy(Class<A> clz) {
            this.store = API.getInstance().create(clz);
        }

        public <T> Chain run(ApiObservableCreator<A, T> apiObservableCreator){
            return new Chain(apiObservableCreator.create(store));
        }
    }

    public interface ApiObservableCreator<A, T> {
        Observable<T> create(A store);
    }

    public static <A> StoreProxy<A> delegate(Class<A> store){
        return new StoreProxy<A>(store);
    }

    public static class Chain {

        private static final String TAG = Chain.class.getSimpleName();

        private Observable request;

        //priority 1
        private Observer observer;

        //priority 2
        private Consumer onNext = o->{};
        private Consumer<? extends Throwable> onError = throwable -> {};
        private Action onComplete = ()->{};

        private Chain(Observable request) {
            this.request = request;
        }

        /**
         * 线程切换 设置
         */
        private ObservableTransformer threadTransform = Rx.uiTransform();
        /**
         * 数据结构适配器 设置
         */
        private ObservableTransformer dataTransform;

        /**
         * 重试操作 设置
         */
        private Function retryFunction;

        /**
         * 延迟obr
         */
        private Long delay;

        /**
         * 是否需要网络
         */
        private boolean needOnline = false;

        public Chain io(){
            this.threadTransform = Rx.ioTransform();
            return this;
        }

        public Chain ui(){
            this.threadTransform = Rx.uiTransform();
            return this;
        }

        public Chain main(){
            this.threadTransform = Rx.mainTransform();
            return this;
        }

        public Chain retry(Function function){
            this.retryFunction = function;
            return this;
        }

        public Chain retry(int times, int interval){
            this.retryFunction = Rx.retryFunc(times, interval);
            return this;
        }

        public Chain delay(long delay){
            this.delay = delay;
            return this;
        }

        public Chain retry3Time(){
            this.retryFunction = Rx.retry3TimesFunc();
            return this;
        }

        public Chain online(){
            this.needOnline = true;
            return this;
        }

        public Chain dataTransform(ObservableTransformer dataTransform){
            this.dataTransform = dataTransform;
            return this;
        }

        public <T> Observable<T> buildRequest(){
            if(request == null){
                request = Observable.never();
            }

            if(delay != null && delay > 0){
                request = request.delay(delay, TimeUnit.MILLISECONDS);
            }

            if(threadTransform != null){
                request = request.compose(threadTransform);
            }

            if(retryFunction != null){
                request = request.retryWhen(retryFunction);
            }

            if(dataTransform != null){
                request = request.compose(dataTransform);
            }

            return request;
        }

        /**
         * 真实执行
         */
        private Disposable realExecute(){
            buildRequest();

            if(onNext == null && onError == null && onComplete == null){
                return Rx.NOTHING();
            }

            Disposable disposable = null;

            if(observer != null){
                disposable = request.subscribe(
                        o -> observer.onNext(o),
                        throwable -> observer.onError((Throwable) throwable),
                        () -> observer.onComplete()
                );
            }else{
                disposable = request.subscribe(onNext, onError, onComplete);
            }

            return disposable;
        }

        public Disposable execute(Observer observer){
            this.observer = observer;

            if(needOnline && !checkNetwork(observer)){
                return Rx.NOTHING();
            }

            return realExecute();
        }

        public Disposable execute(Consumer consumer){
            this.onNext = consumer;

            if(needOnline && !checkNetwork(null, null)){
                return Rx.NOTHING();
            }

            return realExecute();
        }

        public Disposable execute(Consumer next, Consumer error){
            this.onNext = next;
            this.onError = error;

            if(needOnline && !checkNetwork(error, null)){
                return Rx.NOTHING();
            }

            return realExecute();
        }

        public Disposable execute(Consumer next, Consumer error, Action complete){
            this.onNext = next;
            this.onError = error;
            this.onComplete = complete;

            if(needOnline && !checkNetwork(error, complete)){
                return Rx.NOTHING();
            }

            return realExecute();
        }


        private static boolean checkNetwork(Observer subscriber) {
            if (!NetworkUtils.isConnected()) {
                if(subscriber != null){
                    subscriber.onError(new RuntimeException(Constants.HintConstants.NET_ERROR));
                }
                return false;
            }
            return true;
        }

        private static boolean checkNetwork(Consumer consumer, Action action) {
            if (!NetworkUtils.isConnected()) {
                if(consumer != null){
                    try {
                        consumer.accept(new RuntimeException(Constants.HintConstants.NET_ERROR));
                    } catch (Exception e) {
                        Platform.log().e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }

                if(action != null){
                    try {
                        action.run();
                    } catch (Exception e) {
                        Platform.log().e(TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
                return false;
            }
            return true;
        }
    }

}
