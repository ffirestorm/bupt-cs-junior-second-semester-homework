package com.bytedance.clockapplication.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class DigitClock extends View {

    private int vCenterX;   // 试图中心X坐标
    private int vCenterY;   // 视图中心Y坐标
    private final int digitSize;
    private final int dateSize;

    public DigitClock(Context context) {
        super(context);
        digitSize = 160;
        dateSize = 100;
    }

    public DigitClock(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        digitSize = 160;
        dateSize = 100;
    }

    public DigitClock(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        digitSize = 160;
        dateSize = 100;
    }

    @Override
    protected void onMeasure(int w, int h){
        super.onMeasure(w, h);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }
    private  void drawDigitClock(Canvas canvas){
        // 先设置画笔
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setTextSize(digitSize);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        // 在设置字体
        Typeface font = Typeface.create(Typeface.DEFAULT_BOLD , Typeface.BOLD);
        paint.setTypeface(font);

        // 之后设置时间
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        now = new Date(now.getTime());
        int nowHours = now.getHours();
        int nowMinutes = now.getMinutes();
        int nowSeconds = now.getSeconds();

        //绘制时间
        @SuppressLint("DefaultLocale")
        String timeString =
                String.format("%02d",nowHours) + " : " +
                String.format("%02d",nowMinutes) + " : " +
                String.format("%02d",nowSeconds);
        // 计算字符的长度并绘制
        float [] charSize = new float[timeString.length()];
        paint.getTextWidths(timeString, charSize);
        float stringWidth = 0;
        for(float width : charSize)
                stringWidth += width;

        canvas.drawText(timeString, vCenterX-stringWidth/2, vCenterY-(float)digitSize/3,paint);

        // 绘制日期
        paint.setTextSize(dateSize);
        @SuppressLint("DefaultLocale")
        String dateString =
                String.format("%d",now.getYear() + 1900) + " / " +
                String.format("%02d",now.getMonth() + 1) + " / " +
                String.format("%02d",now.getDate());

        charSize = new float[dateString.length()];
        paint.getTextWidths(dateString, charSize);
        stringWidth = 0;
        for(float width : charSize) stringWidth += width;
        canvas.drawText(dateString, vCenterX-stringWidth/2, vCenterY+dateSize, paint);
    }
    @Override
    protected void onDraw(final Canvas canvas){
        super.onDraw(canvas);

        // 试图宽度
        int vWidth = getWidth();
        // 视图高度
        int vHeight = getHeight();

        vCenterX = vWidth /2;
        vCenterY = vHeight /2;

        drawDigitClock(canvas);
        postInvalidateDelayed(1000);
    }
}
