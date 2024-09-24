package com.example.weatherforecast.service;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherForecastResponse {
    @SerializedName("list")
    private List<WeatherForecastItem> forecastList;

    public List<WeatherForecastItem> getForecastList() {
        return forecastList;
    }

    public static class WeatherForecastItem {
        @SerializedName("dt_txt")
        private String dateText;

        @SerializedName("main")
        private Main main;

        @SerializedName("weather")
        private List<Weather> weather;

        public String getDateText() {
            return dateText;
        }

        public Main getMain() {
            return main;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public static class Main {
            @SerializedName("temp")
            private double temp;

            @SerializedName("temp_min")
            private double tempMin;

            @SerializedName("temp_max")
            private double tempMax;

            @SerializedName("pressure")
            private int pressure;

            @SerializedName("humidity")
            private int humidity;

            public double getTemp() {
                return temp;
            }

            public double getTempMin() {
                return tempMin;
            }

            public double getTempMax() {
                return tempMax;
            }

            public int getPressure() {
                return pressure;
            }

            public int getHumidity() {
                return humidity;
            }
        }

        public static class Weather {
            @SerializedName("description")
            private String description;
            @SerializedName("icon")
            private String icon;

            public String getIcon(){ return icon;}

            public String getDescription() {
                return description;
            }
        }
    }
}
