package com.rmkrings.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        setTitle(getResources().getString(R.string.title_news));

        String url = getIntent().getStringExtra("URL");
        WebView mWebView = findViewById(R.id.webview);
        mWebView.loadUrl(url);
    }
}
