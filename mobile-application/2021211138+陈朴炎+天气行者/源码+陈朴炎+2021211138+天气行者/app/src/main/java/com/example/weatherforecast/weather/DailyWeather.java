package com.example.weatherforecast.weather;

import com.google.gson.annotations.SerializedName;

public class DailyWeather {
    @SerializedName("dt")
    private long dateTime;

    @SerializedName("temp")
    private TemperatureRange temperatureRange;

    @SerializedName("weather")
    private WeatherCondition[] weatherConditions;

    public long getDateTime() {
        return dateTime;
    }

    public TemperatureRange getTemperature() {
        return temperatureRange;
    }

    public WeatherCondition[] getWeatherConditions() {
        return weatherConditions;
    }

    /**
     * 温度区间
     */
    public static class TemperatureRange{
        @SerializedName("min")
        private double min;

        @SerializedName("max")
        private double max;

        public double getMin() {
            return min;
        }
        public double getMax() {
            return max;
        }
    }
}
