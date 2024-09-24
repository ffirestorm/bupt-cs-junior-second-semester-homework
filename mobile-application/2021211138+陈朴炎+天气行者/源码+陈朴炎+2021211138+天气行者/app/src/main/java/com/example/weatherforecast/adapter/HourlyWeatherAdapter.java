package com.example.weatherforecast.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.weatherforecast.R;
import com.example.weatherforecast.service.WeatherForecastResponse;
import java.util.List;
import com.bumptech.glide.Glide;
import com.example.weatherforecast.utils.Utility;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.ViewHolder> {
    private List<WeatherForecastResponse.WeatherForecastItem> forecastList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView dateTextView;
        public TextView timeTextView;
        public ImageView weatherIconImageView;
        public TextView temperatureTextView;

        public ViewHolder(View view) {
            super(view);
            dateTextView = view.findViewById(R.id.dateTextView);
            timeTextView = view.findViewById(R.id.timeTextView);
            weatherIconImageView = view.findViewById(R.id.weatherIconImageView);
            temperatureTextView = view.findViewById(R.id.temperatureTextView);
        }
    }

    public HourlyWeatherAdapter(List<WeatherForecastResponse.WeatherForecastItem> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hourly_weather, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherForecastResponse.WeatherForecastItem item = forecastList.get(position);
        holder.dateTextView.setText(getDateFromDateTime(item.getDateText()));
        holder.timeTextView.setText(getTimeFromDateTimeText(item.getDateText()));
        String icon = item.getWeather().get(0).getIcon();
        Utility.loadWeatherIcon(icon,holder.weatherIconImageView);
        int temp = (int)item.getMain().getTempMin();
        holder.temperatureTextView.setText(temp+"°C");
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    private String getTimeFromDateTimeText(String dateTimeText) {
        // 这里根据你的日期时间格式提取时间部分
        return dateTimeText.substring(11, 16);
    }

    private String getDateFromDateTime(String dateTime){
        return dateTime.substring(5,10);
    }



}
