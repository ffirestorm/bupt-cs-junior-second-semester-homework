package com.example.weatherforecast.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CityNameApi {
    @GET("direct")
    Call<GeocodingResponse> getCityChineseName(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("limit") int limit
    );
}
