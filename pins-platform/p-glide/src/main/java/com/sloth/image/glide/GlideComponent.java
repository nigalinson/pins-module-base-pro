package com.sloth.image.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.sankuai.waimai.router.annotation.RouterService;
import com.sloth.platform.ComponentTypes;
import com.sloth.platform.ImageLoaderComponent;
import com.sloth.utils.FileUtils;
import com.sloth.platform.Platform;

import java.io.File;

@RouterService(interfaces = ImageLoaderComponent.class, key = ComponentTypes.ImageLoader.GLIDE)
public class GlideComponent extends ImageLoaderComponent.AbsLoader {

    private static final String TAG = GlideComponent.class.getSimpleName();

    public GlideComponent(Context context) {
        super(context);
    }

    @Override
    protected void validateTransformType(Object transform) {
        validate(transform instanceof Transformation, "图片加载器的形变类型校验失败");
    }

    @Override
    protected void validateTransitionType(Object transition) {
        validate(transition instanceof TransitionOptions, "图片加载器的动画类型校验失败");
    }

    @Override
    protected void validateDiskStrategyType(Object diskStrategy) {
        validate(diskStrategy instanceof DiskCacheStrategy, "图片缓存类型校验失败");
    }

    @Override
    protected void validateRequestOptionsType(Object requestOptions) {
        validate(requestOptions instanceof RequestOptions, "请求配置类型校验失败");
    }

    @Override
    public Rq into(ImageView imageView) {
        GlideRq rq = new GlideRq();

        if(imageView == null){
            Platform.log().i(TAG, "图片控件已销毁，不继续加载图片");
            return rq;
        }

        RequestBuilder requestBuilder = buildRequest();

        if(requestBuilder == null){ return rq;}

        rq.setProxy(requestBuilder.into(imageView).clearOnDetach());

        return rq;
    }

    @Override
    public Rq into(LoadTarget loadTarget) {
        GlideRq rq = new GlideRq();
        if(loadTarget == null){
            Platform.log().i(TAG, "容器已销毁，不继续加载图片");
            return rq;
        }

        RequestBuilder requestBuilder = buildRequest();

        if(requestBuilder == null){ return rq;}

        rq.setProxy(
                requestBuilder.into(new Target<Drawable>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        loadTarget.prepared(placeholder);
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        loadTarget.loadFailed(errorDrawable);
                    }

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                        loadTarget.loadSuccess(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }

                    @Override
                    public void getSize(@NonNull SizeReadyCallback cb) { }

                    @Override
                    public void removeCallback(@NonNull SizeReadyCallback cb) { }

                    @Override
                    public void setRequest(@Nullable Request request) { }

                    @Nullable
                    @Override
                    public Request getRequest() { return null; }

                    @Override
                    public void onStart() { }

                    @Override
                    public void onStop() { }

                    @Override
                    public void onDestroy() { }
                })
        );


        return rq;
    }

    private RequestBuilder buildRequest(){
        if(wkContext.get() == null){
            Platform.log().i(TAG, "挂载父容器已销毁，不继续加载图片");
            return null;
        }

        RequestManager requestManager = Glide.with(wkContext.get());

        RequestBuilder<Drawable> requestBuilder;
        if(exist(localPath)){
            //本地图片优先级最高
            requestBuilder = requestManager.load(new File(localPath));
        }else if(!nil(localRes)){
            //resId
            requestBuilder = requestManager.load(localRes);
        }else if(uri != null){
            //uri
            requestBuilder = requestManager.load(uri);
        }else if(!nil(url)){
            //网络图片
            requestBuilder = requestManager.load(url);
        }else if(!nil(error)){
            //加载错误图片
            requestBuilder = requestManager.load(error);
        }else if(!nil(placeHolder)){
            //加载中图片
            requestBuilder = requestManager.load(placeHolder);
        }else{
            Platform.log().e(TAG, "未设置任何资源，可能导致图片空白！！！");
            requestBuilder = requestManager.load("");
        }

        if(!nil(placeHolder)){
            requestBuilder = requestBuilder.placeholder(placeHolder);
        }

        if(!nil(placeHolder)){
            requestBuilder = requestBuilder.error(error);
        }

        if(transform != null){
            //前面代码已经校验过参数合法性，可放心强转
            requestBuilder = requestBuilder.transform((Transformation)transform);
        }

        if(transition != null){
            //前面代码已经校验过参数合法性，可放心强转
            requestBuilder = requestBuilder.transition((TransitionOptions)transition);
        }

        if( diskStrategy != null){
            //前面代码已经校验过参数合法性，可放心强转
            requestBuilder = requestBuilder.diskCacheStrategy((DiskCacheStrategy) diskStrategy);
        }else{
            //todo 默认缓存策略
        }

        if(skipMemoryCache){
            requestBuilder.skipMemoryCache(true);
        }

        if(requestOptions != null){
            //前面代码已经校验过参数合法性，可放心强转
            requestBuilder = requestBuilder.apply((BaseRequestOptions<?>) requestOptions);
        }

        if(loadListener != null){
            requestBuilder = requestBuilder.listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    loadListener.loadFailed(e != null ? e.getMessage() : "加载失败");
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    loadListener.loadSuccess(resource);
                    return false;
                }
            });
        }

        return requestBuilder;
    }

    private boolean nil(String txt){
        return (null == txt || "".equals(txt));
    }

    private boolean nil(int res){
        return (-1 == res || 0 == res);
    }

    private boolean exist(String path){
        return FileUtils.isFileExists(path);
    }

    private static class GlideRq implements Rq {

        Target proxy;

        public void setProxy(Target proxy) {
            this.proxy = proxy;
        }

        @Override
        public void cancel() {
            if(proxy != null && proxy.getRequest() != null){
                proxy.getRequest().clear();
            }
        }
    }
}
