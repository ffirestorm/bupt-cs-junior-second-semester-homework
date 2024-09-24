package com.example.weatherforecast.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherService {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private static WeatherApi weatherApi;

    public static WeatherApi getWeatherApi() {
        if (weatherApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            weatherApi = retrofit.create(WeatherApi.class);
        }
        return weatherApi;
    }
}
