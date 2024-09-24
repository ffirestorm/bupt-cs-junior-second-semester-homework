package com.example.weatherforecast.service;

import com.example.weatherforecast.weather.DailyWeather;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DailyForecastResponse {
    @SerializedName("city")
    private City city;

    @SerializedName("list")
    private List<DailyWeather> dailyWeatherList;

    public City getCity() {
        return city;
    }

    public List<DailyWeather> getDailyWeatherList() {
        return dailyWeatherList;
    }

    public static class City {
        @SerializedName("name")
        private String name;

        public String getName() {
            return name;
        }
    }
}
