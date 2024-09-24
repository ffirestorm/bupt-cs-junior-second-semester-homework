package com.example.weatherforecast.utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.ImageView;

import androidx.collection.LongSparseArray;

import com.bumptech.glide.Glide;
import com.example.weatherforecast.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utility {

    public static String getDailyForecast(Context context, String cityName){
        cityName = cityName.toLowerCase();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 将城市名称转换为小写
            String fileName = cityName.toLowerCase()+"_daily";

            String packageName = context.getPackageName();
            @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier(fileName, "raw", packageName);
            // 获取资源文件的输入流
            if (resId != 0) {
                InputStream inputStream = context.getResources().openRawResource(resId);
                // 使用BufferedReader读取文件内容
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                // 逐行读取文件内容并添加到StringBuilder中
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                // 关闭输入流
                inputStream.close();
            }else{
                System.out.println("resId = 0， 读取失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回文件内容
        return stringBuilder.toString();
    }


    public static String getHourlyForecast(Context context, String cityName){
        cityName = cityName.toLowerCase();
        StringBuilder stringBuilder = new StringBuilder();
        try {
            // 将城市名称转换为小写
            String fileName = cityName.toLowerCase()+"_hourly";
            String packageName = context.getPackageName();
            @SuppressLint("DiscouragedApi") int resId = context.getResources().getIdentifier(fileName, "raw", packageName);
            // 获取资源文件的输入流
            if (resId != 0) {
                InputStream inputStream = context.getResources().openRawResource(resId);
                // 使用BufferedReader读取文件内容
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                // 逐行读取文件内容并添加到StringBuilder中
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                // 关闭输入流
                inputStream.close();
            }else{
                System.out.println("resId = 0， 读取失败");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 返回文件内容
        return stringBuilder.toString();
    }


    public static void setBackGround(ImageView imageView, String main){
        main = main.toLowerCase();
        switch (main){
            case "clear":
                imageView.setImageResource(R.drawable.clear);
                break;
            case "clouds":
            case "cloud":
                imageView.setImageResource(R.drawable.clouds);
                break;
            case "rain":
                imageView.setImageResource(R.drawable.rain);
                break;
            case"mist":
                imageView.setImageResource(R.drawable.mist);
                break;
            case"snow":
                imageView.setImageResource(R.drawable.snow);
                break;
            default:
                imageView.setImageResource(R.mipmap.bg);
        }
    }
    /**
     * 导入天气图标
     * @param iconCode  天气图标的icon字符串值，通过OpenWeatherApi获得的
     * @param imageView 展示图标的组件
     */
    public static void loadWeatherIcon(String iconCode, ImageView imageView) {
        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
        Glide.with(imageView.getContext()).load(iconUrl).into(imageView);
    }
}
