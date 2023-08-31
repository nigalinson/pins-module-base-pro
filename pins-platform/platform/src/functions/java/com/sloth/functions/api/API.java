package com.sloth.functions.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class API {
    private static final API INSTANCE = new API();

    public static API getInstance(){
        return INSTANCE;
    }

    /**
     * 接口提供器实例
     * key - 类名
     */
    private final Map<String, Object> stores = new ConcurrentHashMap<>();

    private API(){}

    public <ApiStore> ApiStore create(Class<ApiStore> clz){
        return create(null, clz);
    }

    public <ApiStore> ApiStore create(ApiConfig module, Class<ApiStore> clz){
        Object cache = stores.get(clz.getSimpleName());
        if(cache != null){
            return (ApiStore) cache;
        }
        return reCreate(module, clz);
    }

    public <ApiStore> ApiStore reCreate(ApiConfig module, Class<ApiStore> clz){
        ApiStore apiStore = make(module, clz);
        stores.put(clz.getSimpleName(), apiStore);
        return apiStore;
    }

    private <ApiStore> ApiStore make(ApiConfig module, Class<ApiStore> clz){
        if(module == null){
            throw new RuntimeException("应用内请至少设置一次数据接口模板！！调用 API.create(ApiConfig, Class)");
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        //添加通用拦截器
        for(Interceptor interceptor: module.interceptors()){
            builder.addInterceptor(interceptor);
        }

        //设置超时时间等
        OkHttpClient okHttpClient = builder
                .connectTimeout(module.timeout(), TimeUnit.SECONDS)
                .readTimeout(module.timeout(), TimeUnit.SECONDS)
                .writeTimeout(module.timeout(), TimeUnit.SECONDS)
                .cache(module.cache())
                .retryOnConnectionFailure(false)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(LenientGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(module.host())
                .build();

        return retrofit.create(clz);
    }

}
