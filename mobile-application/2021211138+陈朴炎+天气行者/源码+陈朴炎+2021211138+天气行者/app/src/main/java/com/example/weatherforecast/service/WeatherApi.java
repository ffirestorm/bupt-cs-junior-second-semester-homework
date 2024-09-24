package com.example.weatherforecast.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

    @GET("weather")
    Call<WeatherResponse> getCurrentWeatherByLocation(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );
    // 获取16天的每日天气预报数据
    @GET("forecast")
    Call<WeatherForecastResponse> getFiveDayThreeHourForecast(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String language
    );
    @GET("forecast")
    Call<WeatherForecastResponse> getFiveDayThreeHourForecastByCityName(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units,
            @Query("lang") String lang
    );

}