package com.example.weatherforecast.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 得交钱了才能用
 */
public interface OneCallWeatherApi {
    @GET("onecall")
    Call<OneCallResponse> getOneCallWeather(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("exclude") String exclude,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );
}
