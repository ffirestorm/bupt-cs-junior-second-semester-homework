package com.example.weatherforecast;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherforcast.R;
import com.example.weatherforecast.service.WeatherApi;
import com.example.weatherforecast.service.WeatherResponse;
import com.example.weatherforecast.service.WeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchWeatherFragment extends Fragment {

    private EditText cityEditText; // 用于输入城市名称的 EditText
    private Button searchButton; // 用于搜索的按钮
    private TextView searchWeatherTextView; // 用于显示搜索结果的 TextView
    private static final String API_KEY = "YOUR_OPENWEATHERMAP_API_KEY"; // OpenWeatherMap API 密钥

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_weather, container, false);
        cityEditText = view.findViewById(R.id.cityEditText); // 获取 EditText
        searchButton = view.findViewById(R.id.searchButton); // 获取 Button
        searchWeatherTextView = view.findViewById(R.id.searchWeatherTextView); // 获取 TextView

        // 设置按钮点击事件
        searchButton.setOnClickListener(v -> {
            String city = cityEditText.getText().toString();
            if (!city.isEmpty()) {
                fetchWeatherData(city); // 获取天气数据
            } else {
                Toast.makeText(getActivity(), "Please enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // 使用 Retrofit 获取天气数据
    private void fetchWeatherData(String city) {
        WeatherApi weatherApi = WeatherService.getWeatherApi();
        weatherApi.getCurrentWeather(city, API_KEY, "metric").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String weatherInfo = "City: " + weatherResponse.getCityName() + "\n" +
                            "Temperature: " + weatherResponse.getMain().getTemperature() + "°C";
                    searchWeatherTextView.setText(weatherInfo); // 显示天气信息
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