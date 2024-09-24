package com.example.weatherforecast.service;
import com.example.weatherforecast.weather.DailyWeather;
import com.example.weatherforecast.weather.HourlyWeather;
import com.example.weatherforecast.weather.WeatherCondition;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    @SerializedName("main")
    private Main main; // 主要天气信息

    @SerializedName("name")
    private String cityName; // 城市名称
    @SerializedName("weather")
    private List<WeatherCondition> weatherConditions; // 天气状况列表

    @SerializedName("hourlyWeather") // 新添加的字段，表示每个小时的天气数据
    private List<HourlyWeather> hourlyWeatherList;

    @SerializedName("dailyWeather")
    private List<DailyWeather> dailyWeatherList;

    public List<HourlyWeather> getHourlyWeatherList() {
        return hourlyWeatherList;
    }
    public List<DailyWeather> getDailyWeatherList(){return  dailyWeatherList;}
    public Main getMain() {
        return main;
    }

    public String getCityName() {
        return cityName;
    }
    public List<WeatherCondition> getWeatherConditions() {
        return weatherConditions;
    }


    public class Main {
        @SerializedName("temp")
        private float temperature; // 温度

        public float getTemperature() {
            return temperature;
        }
    }
}