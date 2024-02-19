package com.example.demo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ColorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        // 获取 TextView 对象
        TextView colorTextView = findViewById(R.id.colorTextView);

        // 设置颜色，这里以RGB值为例
        int red = 255;
        int green = 0;
        int blue = 0;
        int color = Color.rgb(red, green, blue);

        // 设置背景颜色
        colorTextView.setBackgroundColor(color);
    }
}
