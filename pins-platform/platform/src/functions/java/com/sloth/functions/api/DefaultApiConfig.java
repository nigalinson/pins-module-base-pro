package com.sloth.functions.api;

import android.text.TextUtils;
import com.sloth.utils.AppUtils;
import com.sloth.utils.DeviceUtils;
import com.sloth.utils.NetworkUtils;
import com.sloth.utils.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultApiConfig implements ApiConfig {

    private final String apiHost;

    /**
     * 通用拦截器
     */
    private final List<Interceptor> interceptors = new ArrayList<>();

    /**
     * 缓存配置
     */
    private final Cache cache;

    public DefaultApiConfig(String apiHost) {
        this.apiHost = apiHost;
        this.cache = buildCache();
        buildInterceptors(interceptors);
    }

    @Override
    public String host() {
        return this.apiHost;
    }

    @Override
    public long timeout() {
        return DEFAULT_TIMEOUT;
    }

    @Override
    public Cache cache() {
        return cache;
    }

    @Override
    public List<Interceptor> interceptors() {
        return interceptors;
    }

    protected Cache buildCache(){
        return new Cache(buildCacheFolder(), buildCacheSize());
    }

    protected File buildCacheFolder() {
        return new File(Utils.getApp().getCacheDir(), "HttpResponseCache");
    }

    protected long buildCacheSize() {
        return DEFAULT_CACHE_SIZE;
    }

    protected void buildInterceptors(List<Interceptor> interceptors){
        interceptors.add(buildRequestInterceptor());
        interceptors.add(buildResponseInterceptor());
        interceptors.add(buildLogInterceptor());
    }

    protected Interceptor buildRequestInterceptor() {
        return chain -> {
            Request originRequest = chain.request();
            Request.Builder requestBuilder = originRequest.newBuilder();
            onInterceptRequest(originRequest, requestBuilder);
            return chain.proceed(requestBuilder.build());
        };
    }

    protected void onInterceptRequest(Request request, Request.Builder requestBuilder) {
        String mac = DeviceUtils.getMacAddress();
        String androidId = DeviceUtils.getAndroidID();
        requestBuilder.addHeader("ua", "Android");
        requestBuilder.addHeader("appVersion", AppUtils.getAppVersionName());
        requestBuilder.addHeader("osVersion", android.os.Build.VERSION.RELEASE);
        requestBuilder.addHeader("deviceType", android.os.Build.MODEL);
        requestBuilder.addHeader("netWork", NetworkUtils.getNetworkType().toString());
        requestBuilder.addHeader("mac", TextUtils.isEmpty(mac) ? "UN_KNOWN" : mac);
        requestBuilder.addHeader("androidId", TextUtils.isEmpty(androidId) ? "UN_KNOWN" : androidId);
    }

    protected Interceptor buildResponseInterceptor() {
        return chain -> {
            Request request = chain.request();
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            Response.Builder builder = originalResponse.newBuilder();
            onInterceptResponse(request, originalResponse, builder);
            return builder.build();
        };

    }

    protected void onInterceptResponse(Request request, Response response, Response.Builder responseBuilder) {
        if (NetworkUtils.isConnected()) {
            //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
            String cacheControl = request.cacheControl().toString();
            responseBuilder.header("Cache-Control", cacheControl);
            responseBuilder.removeHeader("Pragma");
        } else {
            responseBuilder.header("Cache-Control", "public, only-if-cached, max-stale=2419200");
            responseBuilder.removeHeader("Pragma");
        }
    }

    protected Interceptor buildLogInterceptor() {
        //log拦截器
        PlatformLogInterceptor loggingInterceptor = new PlatformLogInterceptor();
        loggingInterceptor.setLevel(PlatformLogInterceptor.Level.BODY);
        return loggingInterceptor;
    }

}
