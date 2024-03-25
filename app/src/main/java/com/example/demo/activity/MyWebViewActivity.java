package com.example.demo.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.demo.R;

public class MyWebViewActivity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        webView=findViewById(R.id.webView);
        String url = getIntent().getStringExtra("url");
        // 设置WebView允许加载JavaScript代码
        webView.getSettings().setJavaScriptEnabled(true);
        String deviceName = Build.DEVICE; // 获取设备名称
        String osVersion = Build.VERSION.RELEASE; // 获取操作系统版本号
// 拼接User-Agent字符串
        String newUserAgent = "Mozilla/5.0 (Linux; Android " + osVersion + "; " + deviceName + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Mobile Safari/537.36";

// 加载URL
        //webView.loadUrl(url);
        webView.getSettings().setUserAgentString(newUserAgent);


        // 设置WebViewClient以便在WebView控件中显示网页内容，而不是通过外部浏览器显示
        webView.setWebViewClient(new WebViewClient());
        if(url!=null){
            webView.loadUrl(url);
        }else{
            // 加载指定的URL
            webView.loadUrl("https://www.dongqiudi.com/articles/3310135.html");
        }

        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}