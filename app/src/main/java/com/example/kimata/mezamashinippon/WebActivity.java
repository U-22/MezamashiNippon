package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by kimata on 16/08/18.
 */
public class WebActivity extends Activity{
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websearch);
        final WebView webView = (WebView) findViewById(R.id.websearchview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://google.co.jp");


        //jacascriptを許可する
        webView.getSettings().setJavaScriptEnabled(true);
        webView.clearCache(true);
        webView.clearHistory();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_page);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ページurlを取得
                String url = webView.getUrl();
                Intent objIntent = new Intent(getApplicationContext(),SettingActivity.class);
                objIntent.putExtra("url",url);
                startActivity(objIntent);

            }
        });

    }
}
