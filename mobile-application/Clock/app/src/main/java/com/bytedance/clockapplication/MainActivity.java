package com.bytedance.clockapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bytedance.clockapplication.widget.Clock;
import com.bytedance.clockapplication.widget.DigitClock;

public class MainActivity extends AppCompatActivity {

    private Clock mClockView;           // 时钟界面
    private DigitClock mDigitView;      // 数字钟界面
    private ViewPager mViewPager;       // 底层ViewPager


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager=findViewById(R.id.view_pager);

        // 设置两个界面
        View view1 = getLayoutInflater().inflate(R.layout.activity_clock,null);
        mClockView = view1.findViewById(R.id.clock);
        View view2 = getLayoutInflater().inflate(R.layout.activity_digit,null);
        mDigitView = view2.findViewById(R.id.digit_clock);
        mViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position){
                // 根据页码来返回
                container.addView(position == 0 ? mClockView : mDigitView);
                return position == 0 ? mClockView : mDigitView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                // 销毁界面函数
                container.removeView(position == 0 ? mClockView : mDigitView);
            }

        });
}}