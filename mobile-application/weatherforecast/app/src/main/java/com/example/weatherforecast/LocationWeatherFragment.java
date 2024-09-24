package com.example.weatherforecast;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherforcast.R;
import com.example.weatherforecast.service.WeatherApi;
import com.example.weatherforecast.service.WeatherResponse;
import com.example.weatherforecast.service.WeatherService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationWeatherFragment extends Fragment {

    private TextView locationWeatherTextView; // 用于显示天气的 TextView
    private FusedLocationProviderClient fusedLocationClient; // 用于获取当前位置的客户端
    private static final String API_KEY = "YOUR_OPENWEATHERMAP_API_KEY"; // OpenWeatherMap API 密钥

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.example.weatherforcast.R.layout.fragment_location_weather, container, false);
        locationWeatherTextView = view.findViewById(R.id.locationWeatherTextView); // 获取 TextView

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity()); // 初始化位置客户端

        getCurrentLocationWeather(); // 获取当前位置的天气

        return view;
    }

    // 获取当前位置的天气
    private void getCurrentLocationWeather() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            fetchWeatherData(lat, lon); // 获取天气数据
                        }
                    }
                });
    }

    // 使用 Retrofit 获取天气数据
    private void fetchWeatherData(double lat, double lon) {
        WeatherApi weatherApi = WeatherService.getWeatherApi();
        weatherApi.getCurrentWeatherByLocation(lat, lon, API_KEY, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String weatherInfo = "Location: " + weatherResponse.getCityName() + "\n" +
                            "Temperature: " + weatherResponse.getMain().getTemperature() + "°C";
                    locationWeatherTextView.setText(weatherInfo); // 显示天气信息
                } else {
                    Toast.makeText(getActivity(), "Failed to get weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed to get weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
