package com.sloth.functions.http;

import android.text.TextUtils;
import com.sloth.utils.AppUtils;
import com.sloth.utils.DeviceUtils;
import com.sloth.utils.NetworkUtils;
import com.sloth.utils.Utils;
import java.io.File;
import java.util.List;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultApiModule extends BaseApiModule {

    public DefaultApiModule(String apiHost) {
        super(apiHost);
    }

    @Override
    protected void initInterceptors(List<Interceptor> interceptors) {
        String mac = DeviceUtils.getMacAddress();
        String androidId = DeviceUtils.getAndroidID();

        Interceptor requestInterceptor = chain -> {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("ua", "Android")
                    .addHeader("appVersion", AppUtils.getAppVersionName())
                    .addHeader("osVersion", android.os.Build.VERSION.RELEASE)
                    .addHeader("deviceType", android.os.Build.MODEL)
                    .addHeader("netWork", NetworkUtils.getNetworkType().toString())
                    .addHeader("mac", TextUtils.isEmpty(mac) ? "UN_KNOWN" : mac)
                    .addHeader("androidId", TextUtils.isEmpty(androidId) ? "UN_KNOWN" : androidId)
                    .build();
            return chain.proceed(request);
        };

        //云端响应头拦截器，用来配置缓存策略
        Interceptor rewriteCacheControlInterceptor = chain -> {
            Request request = chain.request();
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isConnected()) {
                //有网的时候读接口上的@Headers里的配置，你可以在这里进行统一的设置
                String cacheControl = request.cacheControl().toString();
                return originalResponse.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
            } else {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=2419200")
                        .removeHeader("Pragma")
                        .build();
            }
        };

        interceptors.add(requestInterceptor);
        interceptors.add(rewriteCacheControlInterceptor);
    }

    @Override
    protected Cache initCacheConfig(long cacheSize) {
        File baseDir = Utils.getApp().getCacheDir();
        final File cacheDir = new File(baseDir, "HttpResponseCache");
        return new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE);
    }

}
