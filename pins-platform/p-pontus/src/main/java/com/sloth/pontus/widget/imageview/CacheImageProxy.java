package com.sloth.pontus.widget.imageview;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.BitmapFactory;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.sloth.platform.Platform;
import com.sloth.platform.ResourceManagerComponent;
import com.sloth.pontus.listener.ResourceListenerAdapter;
import com.sloth.utils.StringUtils;
import com.sloth.utils.Utils;

import java.io.File;

public abstract class CacheImageProxy {

    private static final String TAG = CacheImageProxy.class.getSimpleName();

    //根据宽缩放
    public static final int ANCHOR_WIDTH = 1;
    //根据高缩放
    public static final int ANCHOR_HEIGHT = 2;
    //根据宽高选一个 更low的缩放
    public static final int ANCHOR_BOTH = 3;

    private final AppCompatImageView iv;
    private int loadingSizeX = -1;
    private int loadingSizeY = -1;

    private boolean isImportantResource = false;

    //是否渲染Alpha通道
    private boolean supportAlpha = false;

    /**
     * 当前操作的URL
     */
    private String operatingUrl;

    private boolean cancelOldWhenReset = true;

    /**
     * 下载完成后是否按照容器大小等比缩放图片大小
     */
    private boolean autoAdjust = false;

    private int adjustAnchor = ANCHOR_BOTH;

    public CacheImageProxy(AppCompatImageView iv) {
        this.iv = iv;
    }

    private final ResourceManagerComponent.ResourceListener listener = new ResourceListenerAdapter(){
        @Override
        public void onResourceReady(Long resourceId, String url, String localPath) {
            operatingUrl = null;
            if(localPath != null){
                Platform.log().d(TAG, "resourceId: " + resourceId + ",localPath: " + localPath);
                if(isSvg(localPath)){
                    Platform.log().d(TAG, "加载类型：svg");
                    try{
                        if(live()){
                            onLoadSvg(iv, localPath);
                        }
                    }catch (Exception e){
                        if(live()){
                            iv.setImageDrawable(ContextCompat.getDrawable(Utils.getApp(), errorDrawable()));
                        }
                    }
                }else if(isGif(localPath)){
                    Platform.log().d(TAG, "加载类型：gif");
                    try{
                        if(live()){
                            boolean is4K = is4KDevice(iv.getContext());
                            if(loadingSizeX != -1 && loadingSizeY != -1){
                                Platform.log().d(TAG, "加载类型：gif，按大小加载");
                                Glide.with(getContext()).asGif().load(new File(localPath))
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .format(supportAlpha ? DecodeFormat.PREFER_ARGB_8888 : DecodeFormat.PREFER_RGB_565)
                                        .placeholder(is4K ? -1 : placeHolderDrawable())
                                        .error(is4K ? -1 : errorDrawable())
                                        .override(loadingSizeX, loadingSizeY)
                                        .into(iv);
                            }else{
                                Platform.log().d(TAG, "加载类型：gif，原大小加载");
                                Glide.with(getContext()).asGif().skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .format(supportAlpha ? DecodeFormat.PREFER_ARGB_8888 : DecodeFormat.PREFER_RGB_565)
                                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .placeholder(is4K ? -1 : placeHolderDrawable())
                                        .error(is4K ? -1 : errorDrawable())
                                        .load(new File(localPath))
                                        .into(iv);
                            }
                        }
                    }catch (Exception e){
                        if(live()){
                            iv.setImageDrawable(ContextCompat.getDrawable(Utils.getApp(), errorDrawable()));
                        }
                    }
                }else{
                    if(live()){
                        if(loadingSizeX != -1 && loadingSizeY != -1){
                            Platform.log().d(TAG, "加载类型：普通图像，按大小加载");
                            loadBySize(localPath, DiskCacheStrategy.NONE, loadingSizeX, loadingSizeY);
                        }else{
                            if(autoAdjust){
                                Platform.log().d(TAG, "加载类型：普通图像，根据图片大小动态自适应");
                                //如果设置为空，则获取的bitmap为空,
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;//这个参数设置为true才有效，
                                BitmapFactory.decodeFile(localPath, options);
                                int oriWidth = options.outWidth;
                                int oriHeight = options.outHeight;
                                Platform.log().d(TAG, "通过Options获取到的原始图片大小" + "width:" + oriWidth + " height: " + oriHeight);
                                int ivWidth = iv.getWidth() != 0 ? iv.getWidth() : 1080;
                                int ivHeight = iv.getHeight() != 0 ? iv.getHeight() : 1920;
                                Platform.log().d(TAG, "图片容器大小" + "width:" + ivWidth + " height: " + ivHeight);
                                float ratio = 1f;
                                float wR = 1f * oriWidth / ivWidth;
                                float hR = 1f * oriHeight / ivHeight;
                                if(adjustAnchor == ANCHOR_BOTH){
                                    ratio = Math.max(ratio, wR);
                                    ratio = Math.max(ratio, hR);
                                }else if(adjustAnchor == ANCHOR_WIDTH){
                                    ratio = Math.max(ratio, wR);
                                }else if(adjustAnchor == ANCHOR_HEIGHT){
                                    ratio = Math.max(ratio, hR);
                                }

                                int tgW = (int) (oriWidth / ratio);
                                int tgH = (int) (oriHeight / ratio);

                                Platform.log().d(TAG, "加载类型：普通图像，动态缩放：" + ratio + ", w:" + tgW + ", h:" + tgH);
                                loadBySize(localPath, DiskCacheStrategy.NONE, tgW, tgH);

                            }else{
                                Platform.log().d(TAG, "加载类型：普通图像，原大小加载");
                                loadBySize(localPath, DiskCacheStrategy.NONE, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                            }
                        }
                    }
                }

            }else{
                if(live()){
                    iv.setImageDrawable(ContextCompat.getDrawable(Utils.getApp(), errorDrawable()));
                }
            }
        }

        private void loadBySize(String localPath, DiskCacheStrategy strategy, int sizeOriginal, int sizeOriginal2) {
            boolean is4K = is4KDevice(iv.getContext());
            Glide.with(getContext()).load(new File(localPath))
                    .diskCacheStrategy(strategy)
                    .format(supportAlpha ? DecodeFormat.PREFER_ARGB_8888 : DecodeFormat.PREFER_RGB_565)
                    .placeholder(is4K ? -1 : placeHolderDrawable())
                    .error(is4K ? -1 : errorDrawable())
                    .override(sizeOriginal, sizeOriginal2)
                    .into(iv);
        }

        @Override
        public void onResourceFailed(Long resourceId, String url, String localPath, String err) {
            if(live()){
                iv.setImageDrawable(ContextCompat.getDrawable(Utils.getApp(), errorDrawable()));
            }
        }
    };


    protected abstract boolean is4KDevice(Context context);

    protected abstract int placeHolderDrawable();

    protected abstract int errorDrawable();

    protected void onLoadSvg(AppCompatImageView iv, String localPath){
        //默认不实现SVG加载
    }

    private Context getContext() {
        return iv != null ? iv.getContext() : null;
    }

    public void setImportantResource(boolean importantResource) {
        isImportantResource = importantResource;
    }

    public void setCancelOldWhenReset(boolean cancelOldWhenReset) {
        this.cancelOldWhenReset = cancelOldWhenReset;
    }

    public void autoAdjust(boolean adjust, int anchor){
        this.autoAdjust = adjust;
        this.adjustAnchor = anchor;
    }

    public void setSupportAlpha(boolean supportAlpha) {
        this.supportAlpha = supportAlpha;
    }

    private boolean live() {
        if(iv == null){
            Platform.log().d(TAG, "imageView已销毁，取消加载");
            return false;
        }

        Context context = iv.getContext();

        if(context == null){
            Platform.log().d(TAG, "context已销毁，取消加载");
            return false;
        }

        Activity activity = getActivity(context);
        assert activity != null;
        if(activity.isDestroyed()){
            Platform.log().d(TAG, "activity已销毁，取消加载");
            return false;
        }

        return true;
    }

    private Activity getActivity(Context context){
        if(context instanceof Activity){
            return (Activity)context;
        }else if(context instanceof ContextWrapper){
            return getActivity(((ContextWrapper)context).getBaseContext());
        }
        return null;
    }

    private boolean isSvg(String localPath) {
        int index = localPath.lastIndexOf(".");
        if(index != -1){
            String suffix = localPath.substring(index);
            return ".svg".equalsIgnoreCase(suffix);
        }
        return false;
    }

    private boolean isGif(String localPath) {
        int index = localPath.lastIndexOf(".");
        if(index != -1){
            String suffix = localPath.substring(index);
            return ".gif".equalsIgnoreCase(suffix);
        }
        return false;
    }

    public void loadUrl(String url){
        loadUrl(url ,-1, -1);
    }

    public void loadUrl(String url, int w, int h){
        if(live()){
            if(StringUtils.notEmpty(operatingUrl) && !operatingUrl.equals(url) && cancelOldWhenReset){
                //当前有正在下载的内容，替换需要先取消上一次请求
                Platform.resourceManager().cancel(operatingUrl);
                Platform.log().d(TAG, "cancel: " + operatingUrl);
            }
            operatingUrl = url;
            loadingSizeX = w;
            loadingSizeY = h;
            iv.setImageDrawable(ContextCompat.getDrawable(Utils.getApp(), placeHolderDrawable()));

            if(StringUtils.notEmpty(url)){
                Platform.resourceManager().get(url)
                        .setMaxHotness(isImportantResource)
                        .submit(listener);
            }
        } else{
            Platform.log().d(TAG, "Context已销毁");
        }
    }

    public void loadUrl(String url, String local){
        loadUrl(url, local, -1, -1);
    }

    public void loadUrl(String url, String local, int w, int h){
        if(live()){
            if(StringUtils.notEmpty(operatingUrl) && !operatingUrl.equals(url) && cancelOldWhenReset){
                //当前有正在下载的内容，替换需要先取消上一次请求
                Platform.resourceManager().cancel(operatingUrl);
                Platform.log().d(TAG, "cancel: " + operatingUrl);
            }

            loadingSizeX = w;
            loadingSizeY = h;

            iv.setImageDrawable(ContextCompat.getDrawable(Utils.getApp(), placeHolderDrawable()));
            if(StringUtils.notEmpty(local)){
                Platform.resourceManager().get(url)
                        .setPath(local)
                        .setMaxHotness(isImportantResource)
                        .submit(listener);
            }else{
                Platform.resourceManager().get(url)
                        .setMaxHotness(isImportantResource)
                        .submit(listener);
            }
        }
    }

    public void cancelLoad(){
        if(StringUtils.notEmpty(operatingUrl)){
            Platform.log().d(TAG, "取消下载：" + operatingUrl);
            Platform.resourceManager().cancel(operatingUrl);
            operatingUrl = null;
        }
        Platform.log().d(TAG, "移除Listener");
        Platform.resourceManager().removeListener(listener);
    }

    public void detach(){
        Platform.log().d(TAG, "移除Listener");
        Platform.resourceManager().removeListener(listener);
    }

    public interface Owner {
        void loadUrl(String url);
        void loadUrl(String url, int w, int h);
        void loadUrl(String url, String local);
        void loadUrl(String url, String local, int w, int h);
        void setImportResource(boolean isImportant);
        void setCancelOldWhenReset(boolean cancelOld);
        void autoAdjust(boolean adjust, int anchor);
        void setSupportAlpha(boolean supportAlpha);
        void cancelLoad();
        void detach();
    }

}
