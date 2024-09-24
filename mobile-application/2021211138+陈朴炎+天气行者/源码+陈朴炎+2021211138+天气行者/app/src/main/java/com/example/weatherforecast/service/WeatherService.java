package com.example.weatherforecast.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherService {
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    // 这条3.0的用不了，没交钱
    private static final String BASE_URL_PRO = "https://api.openweathermap.org/data/3.0/";

    private static final String CITY_NAME_BASE_URL = "http://api.openweathermap.org/geo/1.0/";
    private static WeatherApi weatherApi;
    private static OneCallWeatherApi oneCallWeatherApi;

    private static CityNameApi cityNameApi;
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
    public static OneCallWeatherApi getOneCallWeatherApi(){
        if (oneCallWeatherApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_PRO)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            oneCallWeatherApi = retrofit.create(OneCallWeatherApi.class);
        }
        return oneCallWeatherApi;
    }

    public static CityNameApi getCityNameApi(){
        if(cityNameApi == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CITY_NAME_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            cityNameApi = retrofit.create(CityNameApi.class);
        }
        return cityNameApi;
    }
}
