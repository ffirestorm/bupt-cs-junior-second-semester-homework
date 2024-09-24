package com.example.weatherforecast.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    @GET("weather")
    Call<WeatherResponse> getCurrentWeatherByLocation(
            @Query("lat") double lat,
            @Query("lon") double lon,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}