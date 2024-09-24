package com.example.weatherforecast.weather;

import com.google.gson.annotations.SerializedName;

public class HourlyWeather {
    @SerializedName("dt")
    private long dateTime;

    @SerializedName("temp")
    private double temperature;

    @SerializedName("weather")
    private WeatherCondition[] weatherConditions;

    public long getDate() {
        return dateTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public WeatherCondition[] getWeatherConditions() {
        return weatherConditions;
    }
}
