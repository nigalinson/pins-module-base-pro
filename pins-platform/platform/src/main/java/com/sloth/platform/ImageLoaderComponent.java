package com.sloth.platform;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public interface ImageLoaderComponent {

    interface Rq {
        void cancel();
    }

    interface LoadTarget<T> {

        void prepared(Drawable placeHolder);

        void loadSuccess(T drawable);

        void loadFailed(Drawable error);
    }

    interface LoadListener<T> {

        void loadSuccess(T drawable);

        void loadFailed(String msg);

    }

    ImageLoaderComponent with(Context context);

    ImageLoaderComponent load(String url);

    ImageLoaderComponent load(int res);

    ImageLoaderComponent loadLocal(String localPath);


    ImageLoaderComponent load(Uri uri);

    ImageLoaderComponent placeHolder(int resId);

    ImageLoaderComponent error(int resId);

    ImageLoaderComponent transition(Object transition);

    ImageLoaderComponent transform(Object transform);

    ImageLoaderComponent skipMemoryCache(boolean skipMemoryCache);

    ImageLoaderComponent diskStrategy(Object diskStrategy);

    ImageLoaderComponent apply(Object requestOptions);

    <T> ImageLoaderComponent listener(LoadListener<T> loadListener);

    Rq into(ImageView imageView);

    <T> Rq into(LoadTarget<T> loadTarget);

    abstract class AbsLoader implements ImageLoaderComponent {

        private static final String TAG = AbsLoader.class.getSimpleName();

        protected WeakReference<Context> wkContext;
        protected String url;
        protected String localPath;
        protected int localRes;
        protected Uri uri;
        protected int placeHolder;
        protected int error;
        protected Object transform;
        protected Object transition;
        protected LoadListener loadListener;

        protected boolean skipMemoryCache = false;
        protected Object diskStrategy;

        protected Object requestOptions;

        public AbsLoader(Context context) {
            this.wkContext = new WeakReference<>(context);
        }

        @Override
        public ImageLoaderComponent with(Context context){
            this.wkContext = new WeakReference<>(context);
            return this;
        }

        @Override
        public ImageLoaderComponent load(String url){
            this.url = url;
            return this;
        }

        @Override
        public ImageLoaderComponent load(int res){
            this.localRes = res;
            return this;
        }

        @Override
        public ImageLoaderComponent loadLocal(String localPath){
            this.localPath = localPath;
            return this;
        }

        @Override
        public ImageLoaderComponent load(Uri uri){
            this.uri = uri;
            return this;
        }

        @Override
        public ImageLoaderComponent placeHolder(int resId){
            this.placeHolder = resId;
            return this;
        }

        @Override
        public ImageLoaderComponent error(int resId){
            this.error = resId;
            return this;
        }

        @Override
        public ImageLoaderComponent transition(Object transition){
            validateTransitionType(transition);
            this.transition = transition;
            return this;
        }

        @Override
        public ImageLoaderComponent transform(Object transform){
            validateTransformType(transform);
            this.transform = transform;
            return this;
        }

        @Override
        public ImageLoaderComponent skipMemoryCache(boolean skipMemoryCache){
            this.skipMemoryCache = skipMemoryCache;
            return this;
        }

        @Override
        public ImageLoaderComponent diskStrategy(Object diskStrategy){
            validateDiskStrategyType(diskStrategy);
            this.diskStrategy = diskStrategy;
            return this;
        }

        @Override
        public ImageLoaderComponent apply(Object requestOptions){
            validateRequestOptionsType(requestOptions);
            this.requestOptions = requestOptions;
            return this;
        }

        @Override
        public ImageLoaderComponent listener(LoadListener loadListener){
            this.loadListener = loadListener;
            return this;
        }

        /**
         * 校验形变参数类型准确性（非必要）
         * @param transform
         */
        protected abstract void validateTransformType(Object transform);

        /**
         * 校验动画参数类型准确性（非必要）
         * @param transition
         */
        protected abstract void validateTransitionType(Object transition);

        /**
         * 校验缓存参数类型准确性（非必要）
         * @param diskStrategy
         */
        protected abstract void validateDiskStrategyType(Object diskStrategy);

        /**
         * 校验请求配置（非必要）
         * @param requestOptions
         */
        protected abstract void validateRequestOptionsType(Object requestOptions);
        /**
         * 校验结果
         * @param res
         */
        protected void validate(boolean res, String failed){
            if(!res){
                Platform.log().e(TAG, failed);
                throw new RuntimeException(failed);
            }else{
                //校验成功
            }
        }

        private boolean nil(Object obj){
            return null == obj;
        }

        @Override
        public abstract Rq into(ImageView imageView);

        @Override
        public abstract  <T> Rq into(LoadTarget<T> loadTarget);

    }

}
