package com.jiuhua.jiuhuacontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import static android.view.View.MeasureSpec.getSize;

public class MyWeekView extends View {
    private Paint textPaint;
    private Paint textPaint1;
    private float screenWidth;
    private float screenHeight;

    public MyWeekView (Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();  //在构造参数里面初始化画笔
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        screenWidth = getSize(widthMeasureSpec);  //获取屏幕区域的宽度
        screenHeight = (getSize(widthMeasureSpec)-60)/7; //设置了自定义控件的高度
        setMeasuredDimension((int)screenWidth, (int)screenHeight);//并且保存才能够提供给父控件。
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float square = (screenWidth - 60) / 8;  //每个小方格的宽度
        canvas.drawText("本周运行设置",screenWidth/2, square/3, textPaint);
        canvas.drawText("一", 100+square, square,textPaint1);
        canvas.drawText("二", 100+square*2, square,textPaint1);
        canvas.drawText("三", 100+square*3, square,textPaint1);
        canvas.drawText("四", 100+square*4, square,textPaint1);
        canvas.drawText("五", 100+square*5, square,textPaint1);
        canvas.drawText("六", 100+square*6, square,textPaint1);
        canvas.drawText("日", 100+square*7, square,textPaint1);
    }

        private void initPaint() {

        textPaint = new Paint();       //写标题文字的笔
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);//文字在中心
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(50);

        textPaint1 = new Paint();       //写周几文字的笔
        textPaint1.setColor(Color.BLACK);
        textPaint1.setTextAlign(Paint.Align.CENTER);//文字在中心
        textPaint1.setAntiAlias(true);
        textPaint1.setTextSize(35);
    }
}
