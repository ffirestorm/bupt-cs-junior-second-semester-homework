package com.example.weatherforecast.weather;

import com.google.gson.annotations.SerializedName;

public class Temperature {
    @SerializedName("dt")
    private long dateTime;

    @SerializedName("temp")
    private Temperature temperature;

    @SerializedName("weather")
    private WeatherCondition[] weatherConditions;

    // 可以根据需要添加更多字段和方法

    public long getDateTime() {
        return dateTime;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public WeatherCondition[] getWeatherConditions() {
        return weatherConditions;
    }
}
