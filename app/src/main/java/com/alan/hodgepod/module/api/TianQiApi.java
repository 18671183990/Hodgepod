package com.alan.hodgepod.module.api;

import com.alan.hodgepod.module.bean.WeatherInfoBean;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface TianQiApi {

    String BaseUrl = "http://op.juhe.cn/onebox/weather/";

    @GET("query")
    Call<WeatherInfoBean> getWeatherInfo(@Query("cityname") String cityName, @Query("key") String appKey);
}
