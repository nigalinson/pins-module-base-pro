package com.sloth.functions.api;

import java.util.List;
import okhttp3.Cache;
import okhttp3.Interceptor;

public interface ApiConfig {

    //默认超时时间
    long DEFAULT_TIMEOUT = 15;

    //默认缓存区大小 10 MB
    long DEFAULT_CACHE_SIZE = 10 * 1024 * 1024;

    String host();

    long timeout();

    Cache cache();

    List<Interceptor> interceptors();


}
