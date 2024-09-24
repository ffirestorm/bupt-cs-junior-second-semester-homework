package com.example.weatherforecast.service;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GeocodingResponse {

    public String name;
    @SerializedName("local_names")
    public Map<String, String> localNames;
    public double lat;
    public double lon;
    public String country;
    public String state;
}
