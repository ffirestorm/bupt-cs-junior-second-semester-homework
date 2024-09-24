package com.example.weatherforecast.service;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("main")
    private Main main; // 主要天气信息

    @SerializedName("name")
    private String cityName; // 城市名称

    public Main getMain() {
        return main;
    }

    public String getCityName() {
        return cityName;
    }

    public class Main {
        @SerializedName("temp")
        private float temperature; // 温度

        public float getTemperature() {
            return temperature;
        }
    }
}
