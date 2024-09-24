package com.example.weatherforecast;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewPager);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TabLayout tabLayout = findViewById(R.id.tabLayout);

        viewPager.setAdapter(new WeatherPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    private class WeatherPagerAdapter extends FragmentPagerAdapter {

        public WeatherPagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new LocationWeatherFragment();
            } else {
                return new SearchWeatherFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "本地天气";
            } else {
                return "搜索其他城市天气";
            }
        }
    }

    private List<TextView> getAllTextViews(ViewGroup root) {
        List<TextView> textViews = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);
            if (child instanceof TextView) {
                textViews.add((TextView) child);
            } else if (child instanceof ViewGroup) {
                textViews.addAll(getAllTextViews((ViewGroup) child));
            }
        }
        return textViews;
    }
}
