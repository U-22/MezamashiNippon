package com.example.kimata.mezamashinippon;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {
    private Handler uihandler;
    private TextView result;
    private MNHtml myhtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        result = (TextView)findViewById(R.id.result);
        uihandler = new Handler();
        setSupportActionBar(toolbar);

        (new Thread(new Runnable() {
            @Override
            public void run() {
                myhtml = new MNHtml("http://gigazine.net/news/20160810-project-sauron/");
                myhtml.setStartIdentifier("映画化もされた冒険小説「指輪物語」");
                myhtml.setEndIdentifier("なお、シマンテックとカスペルスキーは、それぞれスパイウェア・Remsecに関する報告書をまとめて公開しています。");
                myhtml.findStartElement();
                myhtml.findEndElement();
                //UIスレッドに渡す
                uihandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.setText(myhtml.getStartElementString() + myhtml.getEndELementString());
                    }
                });
            }
        })).start();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
