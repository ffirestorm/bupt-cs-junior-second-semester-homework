package com.example.weatherforecast.service;

import com.example.weatherforecast.weather.CurrentWeather;
import com.example.weatherforecast.weather.DailyWeather;
import com.example.weatherforecast.weather.HourlyWeather;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OneCallResponse {
    @SerializedName("lat")
    private double latitude;

    @SerializedName("lon")
    private double longitude;

    @SerializedName("timezone")
    private String timezone;

    @SerializedName("current")
    private CurrentWeather currentWeather;

    @SerializedName("hourly")
    private List<HourlyWeather> hourlyWeatherList;

    @SerializedName("daily")
    private List<DailyWeather> dailyWeatherList;

    // 可以根据需要添加更多字段和方法

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public CurrentWeather getCurrentWeather() {
        return currentWeather;
    }

    public List<HourlyWeather> getHourlyWeatherList() {
        return hourlyWeatherList;
    }

    public List<DailyWeather> getDailyWeatherList() {
        return dailyWeatherList;
    }
}
