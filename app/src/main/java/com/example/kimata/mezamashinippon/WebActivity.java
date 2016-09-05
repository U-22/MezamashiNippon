package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import javax.script.*;

/**
 * Created by kimata on 16/08/18.
 */
public class WebActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ScriptEngineManager factory= new ScriptEngineManager();
        final ScriptEngine engine = factory.getEngineByName("JavaScript");
        setContentView(R.layout.activity_websearch);

        final WebView webView = (WebView) findViewById(R.id.websearchview);

        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://google.co.jp");

        //jacascriptを許可する
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){

        });
        webView.clearCache(true);
        webView.clearHistory();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_page);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // ページurlを取得
                String url = webView.getUrl();
                webView.setOnLongClickListener(new View.OnLongClickListener(){
                    @Override
                    public boolean onLongClick(View v){
                        //String text = webView.loadUrl("javascript:document.getSelection().toString()");
                        return false;
                    }

                });

                Intent objIntent = new Intent(getApplicationContext(),SettingActivity.class);
                objIntent.putExtra("url",url);
                startActivity(objIntent);

            }
        });

    }
}