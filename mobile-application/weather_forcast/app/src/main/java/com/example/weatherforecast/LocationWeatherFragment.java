package com.example.weatherforecast;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.adapter.HourlyWeatherAdapter;
import com.example.weatherforecast.service.CityNameApi;
import com.example.weatherforecast.service.DailyForecastResponse;
import com.example.weatherforecast.service.GeocodingResponse;
import com.example.weatherforecast.service.OneCallResponse;
import com.example.weatherforecast.service.OneCallWeatherApi;
import com.example.weatherforecast.service.WeatherApi;
import com.example.weatherforecast.service.WeatherForecastResponse;
import com.example.weatherforecast.service.WeatherResponse;
import com.example.weatherforecast.service.WeatherService;
import com.example.weatherforecast.utils.Utility;
import com.example.weatherforecast.weather.DailyWeather;
import com.example.weatherforecast.weather.HourlyWeather;
import com.example.weatherforecast.weather.WeatherCondition;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.protobuf.Api;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationWeatherFragment extends Fragment {

    private ImageView backgroundImage;      // 背景图案
    private RecyclerView recyclerView;      // 用来展示预测信息的，每一行都有日期、时间、图标、温度
    private TextView locationWeatherTextView; // 用于显示天气的 TextView
    private TextView hourlyWeatherTextView; // 一个Label
//    private TextView dailyForecastTextView;
    private FusedLocationProviderClient fusedLocationClient; // 用于获取当前位置的客户端
    private static final String API_KEY = "77f99dfbeec3c406904c6e4f548ab214"; // 我自己的 OpenWeatherMap API 密钥
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private TextView cityNameTextView;      // 显示城市信息
    private TextView locationCurTempTextView;
    private  WeatherApi weatherApi;         // 用来基础获取天气
    private OneCallWeatherApi oneCallWeatherApi;    // 用来获取小时的天气数据（因为url不一样）
    private CityNameApi cityNameApi;
    private double lat;
    private double lon;
    private boolean isLocated = false;
    private AlertDialog alertDialog;
    private String cityName;
    //    LBS 的使用
    String locationProvider;
    LocationManager locationManager;
    Location location;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_weather, container, false);
        locationWeatherTextView = view.findViewById(R.id.locationWeatherTextView); // 获取 TextView
        locationCurTempTextView = view.findViewById(R.id.locationCurrentTemperature);
        hourlyWeatherTextView = view.findViewById(R.id.hourlyWeatherTextView);
//        dailyForecastTextView = view.findViewById(R.id.dailyWeatherTextView);
        location = null;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity()); // 初始化位置客户端
        cityNameTextView = view.findViewById(R.id.cityNameTextView);
        backgroundImage = view.findViewById(R.id.backgroundImageView);
        recyclerView = view.findViewById(R.id.hourlyWeatherRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cityNameTextView.setText("正在定位中...");
        locationCurTempTextView.setText("");
        locationWeatherTextView.setText("");
        hourlyWeatherTextView.setText("");
//        dailyForecastTextView.setText("");
        backgroundImage.setImageResource(R.mipmap.bg);
        weatherApi = WeatherService.getWeatherApi();
        oneCallWeatherApi = WeatherService.getOneCallWeatherApi();
        cityNameApi = WeatherService.getCityNameApi();
        lat = 40;
        lon = 120;
        // 检查并请求位置权限
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            cityNameTextView.setText("未授予权限");
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 如果权限已经被授予，获取当前位置的天气
            cityNameTextView.setText("权限被授予,开始定位");
            getCurrentLocationWeather();
        }
        if(!isLocated){
            initLbs();
        }

        return view;
    }

    private void initLbs() {
        locationProvider = LocationManager.NETWORK_PROVIDER;
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            cityNameTextView.setText("没有位置权限");
            showPermissionAlertDialog();
            return;
        }
        location = locationManager.getLastKnownLocation(locationProvider);
        // 创建位置监听器对象
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(@NonNull Location loc) {
                // 位置信息变更后会回调这里，在这里根据最新的位置信息做相应的处理即可
                lat = location.getLatitude();
                lon = location.getLongitude();
                fetchWeatherData(lat, lon);
                // 位置更新后可以取消监听，以防止频繁调用
                locationManager.removeUpdates(this);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(@NonNull String provider) {}
            public void onProviderDisabled(@NonNull String provider) {}
        };
        // 注册监听器监听位置变化信息
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        if(location==null){
            double defaultLat = 40.0;
            double defaultLon = 116.0;
            cityNameTextView.setText("默认：北京");
            fetchWeatherData(defaultLat, defaultLon);
        }
    }

    private void showPermissionAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        // 创建一个TextView并设置文本内容和样式
        TextView messageTextView = new TextView(requireContext());
        messageTextView.setText("\n使用须知\n\n请开启app定位权限或重启软件\n\n搜索时请输入地区的拼音或英文名\n");
        messageTextView.setGravity(Gravity.CENTER); // 设置文字居中
        messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20); // 设置文字大小
        builder.setView(messageTextView); // 将TextView设置到对话框中
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();

        // 点击屏幕时关闭对话框
        alertDialog.getWindow().getDecorView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，获取当前位置的天气
                getCurrentLocationWeather();
            } else {
                // 权限被拒绝，显示消息
                Toast.makeText(getActivity(), "Location permission not granted", Toast.LENGTH_SHORT).show();
                cityNameTextView.setText("没有授予定位权限");
            }
        }
    }

    /**
     * 获取当前位置的天气
     */
    @SuppressLint({"MissingPermission", "SetTextI18n"})
    private void getCurrentLocationWeather() {
        cityNameTextView.setText("正在获取位置...");
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(Location loc) {
                        if (loc != null) {
                            lat = loc.getLatitude();
                            lon = loc.getLongitude();
                            isLocated = true;
                            fetchWeatherData(lat, lon);
                            location = loc;
                        } else {
                            cityNameTextView.setText("Beijing");
                            fetchWeatherData(lat, lon);
                            Toast.makeText(getActivity(), "Unable to get location", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        locationWeatherTextView.setText("Failed to get location: " + e.getMessage());
                        Toast.makeText(getActivity(), "Failed to get location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchWeatherData(double lat, double lon){
        fetchTemperatureData(lat,lon);
//        fetchForecastData(lat,lon);
//        fetchHourlyWeatherData(lat, lon);
//        fetchDailyForecast(lat, lon);
        fetchForecastDataLoadRecyclerView(lat, lon);
    }

    /**
     * 使用 Retrofit 获取天气数据，主要获取的是温度和天气情况
     * @param lat   传入经度
     * @param lon   传入纬度
     */
    private void fetchTemperatureData(double lat, double lon) {
        // 然后把参数传递进来
        weatherApi.getCurrentWeatherByLocation(lat, lon, API_KEY, "metric","zh_cn").enqueue(new Callback<WeatherResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    cityName = weatherResponse.getCityName();
                    // 获取温度
                    int temperature = (int)weatherResponse.getMain().getTemperature();
                    locationCurTempTextView.setText(temperature+"°C");
                    StringBuilder weatherInfoBuilder = new StringBuilder();


                    // 获取天气状况
                    WeatherCondition weatherCondition = weatherResponse.getWeatherConditions().get(0); // 假设取第一个天气状况
                    String tempMain = weatherCondition.getMain();
                    weatherInfoBuilder.append("天气情况\n").append(tempMain).append("\n").append(weatherCondition.getDescription());
                    //设置背景图片
                    Utility.setBackGround(backgroundImage, tempMain);

                    locationWeatherTextView.setText(weatherInfoBuilder.toString()); // 显示天气信息
                    locationWeatherTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    cityNameTextView.setText(cityName);
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



    /**
     * 获取预测数据并放在TextView里展示出来
     * @param lat
     * @param lon
     */
    private void fetchForecastData(double lat, double lon){
        weatherApi.getFiveDayThreeHourForecast(lat,lon,API_KEY,"metric","zh_cn").enqueue(new Callback<WeatherForecastResponse>(){
            @Override
            public void onResponse(@NonNull Call<WeatherForecastResponse> call, @NonNull Response<WeatherForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
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
                        String hourMinute = timeParts[0] + "时   " ;

                        String weatherInfo = monthDay + "   " + hourMinute +  tempMin + "°C   " + description + "\n";
                        weatherInfoBuilder.append(weatherInfo);
                    }

//                    dailyForecastTextView.setText(weatherInfoBuilder.toString());
                } else {
//                    dailyForecastTextView.setText("服务器未能响应");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<WeatherForecastResponse> call, @NonNull Throwable t) {
//                dailyForecastTextView.setText("请求失败: " + t.getMessage());
            }
        });
    }


    /**
     * 获取预测数据以及图标，并放在RecyclerView里展示出来
     * @param lat   经度
     * @param lon   纬度
     */
    private void fetchForecastDataLoadRecyclerView(double lat, double lon) {
        weatherApi.getFiveDayThreeHourForecast(lat, lon, API_KEY, "metric", "zh_cn").enqueue(new Callback<WeatherForecastResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherForecastResponse> call, @NonNull Response<WeatherForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherForecastResponse forecastResponse = response.body();
                    List<WeatherForecastResponse.WeatherForecastItem> forecastList = forecastResponse.getForecastList();
                    HourlyWeatherAdapter adapter = new HourlyWeatherAdapter(forecastList);
                    hourlyWeatherTextView.setText("天气预报");
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<WeatherForecastResponse> call, Throwable t) {

            }
        });
    }
    /**
     * 用来获取每个小时的天气信息
     */
    private void fetchHourlyWeatherData(double lat, double lon){
        hourlyWeatherTextView.setText("正在获取小时温度信息...");
        oneCallWeatherApi.getOneCallWeather(lat, lon, "minutely,daily,alerts",API_KEY,"metrics","zh_cn").enqueue(new Callback<OneCallResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<OneCallResponse> call, @NonNull Response<OneCallResponse> response) {
                if(!response.isSuccessful()){
                    hourlyWeatherTextView.setText("服务器未能响应");
                    String localHourly = Utility.getHourlyForecast(requireContext(), "local");
                    hourlyWeatherTextView.setText(localHourly);
                    return;
                }
                if (response.body() != null) {
                    OneCallResponse oneCallResponse = response.body();

                    StringBuilder hourlyWeatherInfoBuilder = new StringBuilder();

                    for (HourlyWeather hourlyWeather : oneCallResponse.getHourlyWeatherList()) {
                        // 获取每个小时的天气温度情况
                        String temper = (int) hourlyWeather.getTemperature() + "°C";
                        String weatherDescription = hourlyWeather.getWeatherConditions()[0].getDescription();
                        String hourlyWeatherInfo = temper + " - " + weatherDescription + "\n";
                        hourlyWeatherInfoBuilder.append(hourlyWeatherInfo);
                    }

                    hourlyWeatherTextView.setText(hourlyWeatherInfoBuilder.toString()); // Display hourly weather info

                } else {
                    // 输出响应错误信息
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("WeatherDataError", "Error response body: " + errorBody);
                        } catch (IOException e) {
                            Log.e("WeatherDataError", "Error reading error response body", e);
                        }
                    }
                    hourlyWeatherTextView.setText("");
                    Toast.makeText(getActivity(), "fetchHourlyWeatherData：Failed to get weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OneCallResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "fetchHourlyWeatherData on fail：Failed to get weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    /**
//     * 获取之后16日的天气预测数据
//     * @param lat
//     * @param lon
//     */
//    private void fetchDailyForecast(double lat, double lon) {
//        WeatherApi weatherApi = WeatherService.getWeatherApi();
//        weatherApi.getDailyForecast(lat, lon, 3, API_KEY, "metric", "zh_cn").enqueue(new Callback<DailyForecastResponse>() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onResponse(@NonNull Call<DailyForecastResponse> call, @NonNull Response<DailyForecastResponse> response) {
//                if(!response.isSuccessful()){
//                    dailyForecastTextView.setText("服务器未能响应");
//                    String localDailyForecast = Utility.getDailyForecast(requireContext(),"local");
//                    dailyForecastTextView.setText(localDailyForecast);
//                    return;
//                }
//                if (response.body() != null) {
//                    DailyForecastResponse dailyForecastResponse = response.body();
//                    StringBuilder dailyForecastInfoBuilder = new StringBuilder();
//
//                    for (DailyWeather dailyWeather : dailyForecastResponse.getDailyWeatherList()) {
//                        double minTemp = dailyWeather.getTemperature().getMin();
//                        double maxTemp = dailyWeather.getTemperature().getMax();
//                        String weatherDescription = dailyWeather.getWeatherConditions()[0].getDescription();
//
//                        String dailyWeatherInfo = (int) minTemp + "°C -- " + (int) maxTemp + "°C\t" + weatherDescription + "\n";
//                        dailyForecastInfoBuilder.append(dailyWeatherInfo);
//                    }
//
//                    dailyForecastTextView.setText(dailyForecastInfoBuilder.toString()); // Display daily forecast info
//
//                } else {
//                    Toast.makeText(getActivity(), "Failed to get daily forecast data", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<DailyForecastResponse> call, @NonNull Throwable t) {
//                Toast.makeText(getActivity(), "Failed to get daily forecast data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    /**
     * 这个函数我用来翻译地名，但是好像用不了
     * @param cityName
     */
    private void translateCityName(String cityName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Translate translate = TranslateOptions.getDefaultInstance().getService();
                Translation translation = translate.translate(cityName, Translate.TranslateOption.targetLanguage("zh"));
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cityNameTextView.setText(translation.getTranslatedText());
                    }
                });
            }
        }).start();
    }

    /**
     * 用来检查地区名称是不是全是英文
     * @param str
     * @return
     */
    private boolean isAllEnglishAndSpaces(String str) {
        return str.matches("^[a-zA-Z\\s]+$");
    }
}