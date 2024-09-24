package com.example.weatherforecast;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherforecast.adapter.HourlyWeatherAdapter;
import com.example.weatherforecast.service.WeatherApi;
import com.example.weatherforecast.service.WeatherForecastResponse;
import com.example.weatherforecast.service.WeatherResponse;
import com.example.weatherforecast.service.WeatherService;
import com.example.weatherforecast.utils.Utility;
import com.example.weatherforecast.weather.WeatherCondition;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchWeatherFragment extends Fragment {

    private EditText cityEditText; // 用于输入城市名称的 EditText
    private Button searchButton; // 用于搜索的按钮
    private TextView searchWeatherTextView; // 用于显示搜索结果的 TextView
    private TextView forecastLabelTextView;
    private ImageView searchBackGround;
    private RecyclerView searchRecyclerView;
    private ImageView searchConditionImage;
    private static final String API_KEY = "77f99dfbeec3c406904c6e4f548ab214"; // OpenWeatherMap API 密钥
    private WeatherApi weatherApi;
    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_weather, container, false);
        cityEditText = view.findViewById(R.id.cityEditText); // 获取 EditText
        searchButton = view.findViewById(R.id.searchButton); // 获取 Button
        searchWeatherTextView = view.findViewById(R.id.searchWeatherTextView); // 获取 TextView
        forecastLabelTextView = view.findViewById(R.id.forecastLabelTextView);
        searchRecyclerView = view.findViewById(R.id.hourlySearchWeatherRecyclerView);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchBackGround = view.findViewById(R.id.searchBackgroundImageView);
        searchConditionImage = view.findViewById(R.id.searchCurrentConditionImage);
        searchBackGround.setImageResource(R.mipmap.bg);
        cityEditText.setText("Beijing");
        searchWeatherTextView.setText("");
        forecastLabelTextView.setText("");
        weatherApi = WeatherService.getWeatherApi();
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

    private void fetchWeatherData(String city){
        fetchTemperatureData(city);
        forecastLabelTextView.setText("");
//        fetchForecastData(city);
        fetchForecastDataLoadRecycler(city);
    }

    // 使用 Retrofit 获取天气数据
    private void fetchTemperatureData(String city) {
        weatherApi.getCurrentWeather(city, API_KEY, "metric","zh_cn").enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    WeatherCondition weatherCondition = weatherResponse.getWeatherConditions().get(0);
                    String tempMain = weatherCondition.getMain();
                    Utility.setBackGround(searchBackGround,tempMain);
                    String icon = weatherCondition.getIcon();
                    Utility.loadWeatherIcon(icon,searchConditionImage);

                    int temp = (int)weatherResponse.getMain().getTemperature();
                    String weatherInfo = temp + "°C";
                    searchWeatherTextView.setText(weatherInfo); // 显示天气信息
                } else {
                    Toast.makeText(getActivity(), "Failed to get weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "Failed to get weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchForecastData(String city){
        weatherApi.getFiveDayThreeHourForecastByCityName(city, API_KEY,"metric","zh_cn").enqueue(new Callback<WeatherForecastResponse>() {
            @Override
            public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                if(response.isSuccessful()&& response.body()!=null){
                    WeatherForecastResponse forecastResponse = response.body();
                    StringBuilder weatherInfoBuilder = new StringBuilder();
                    for (WeatherForecastResponse.WeatherForecastItem item : forecastResponse.getForecastList()) {
                        String dateText = item.getDateText();
                        int tempMin = (int) item.getMain().getTempMin();

                        String description = item.getWeather().get(0).getDescription();

                        // 解析日期和时间
                        String[] dateTimeParts = dateText.split(" ");
                        String datePart = dateTimeParts[0]; // 日期部分
                        String timePart = dateTimeParts[1]; // 时间部分

                        // 提取月份和日
                        String[] dateParts = datePart.split("-");
                        String monthDay = dateParts[1] + "." + dateParts[2];

                        // 提取时和分
                        String[] timeParts = timePart.split(":");
                        String hourMinute = timeParts[0] + "时   ";

                        String weatherInfo = monthDay + "   " + hourMinute + tempMin + "°C   " + description + "\n";
                        weatherInfoBuilder.append(weatherInfo);
                    }

//                    searchForecastTextView.setText(weatherInfoBuilder.toString());
                }else{
//                    searchForecastTextView.setText("未能获取到数据");
                }
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {

            }
        });
    }

    private void fetchForecastDataLoadRecycler(String cityName){
        weatherApi.getFiveDayThreeHourForecastByCityName(cityName, API_KEY,"metric","zh_cn").enqueue(new Callback<WeatherForecastResponse>() {
            @Override
            public void onResponse(Call<WeatherForecastResponse> call, Response<WeatherForecastResponse> response) {
                if(response.isSuccessful()&& response.body()!=null){
                    WeatherForecastResponse forecastResponse = response.body();
                    List<WeatherForecastResponse.WeatherForecastItem> forecastList = forecastResponse.getForecastList();
                    HourlyWeatherAdapter adapter = new HourlyWeatherAdapter(forecastList);
                    forecastLabelTextView.setText("天气预报");
                    searchRecyclerView.setAdapter(adapter);

                }else{

                }
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {

            }
        });
    }



}