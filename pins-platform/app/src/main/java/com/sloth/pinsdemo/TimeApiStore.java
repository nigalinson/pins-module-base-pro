package com.sloth.pinsdemo;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TimeApiStore {
    @GET("easy-smart/version/getCurrentTime")
    Observable<Object> getServerTime(@Query("now") String now);
}
