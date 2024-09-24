package com.example.weatherforecast.weather;

import com.google.gson.annotations.SerializedName;

public class CurrentWeather {
    @SerializedName("dt")
    private long dateTime;

    @SerializedName("temp")
    private double temperature;

    @SerializedName("weather")
    private WeatherCondition[] weatherConditions;

    // 可以根据需要添加更多字段和方法

    public long getDateTime() {
        return dateTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public WeatherCondition[] getWeatherConditions() {
        return weatherConditions;
    }
}
