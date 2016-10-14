package com.example.kimata.mezamashinippon;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TestActivity extends AppCompatActivity {
    private Handler uihandler;
    private TextView result;
    private MNHtml myhtml;
    private MNSite targetSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        result = (TextView)findViewById(R.id.result);
        uihandler = new Handler();
        setSupportActionBar(toolbar);

        targetSite = new MNSite("http://www.gizmodo.jp/");
        targetSite.setStartIdentifier("リンゴマークが丸になっただけのiPhoneじ");
        (new Thread(new Runnable() {
            @Override
            public void run() {
                /*targetSite.findStartClassName("http://www.gizmodo.jp/2016/10/google-pixel-hands-on.html");
                Log.d("sClassName", "sClassName: " + targetSite.getStartClassName());
                final String temp = targetSite.generateMainContent("http://www.gizmodo.jp/2016/10/google-pixel-hands-on.html");
                //final String temp = targetSite.getHtml("http://www.gizmodo.jp/2016/10/google-pixel-hands-on.html");*/
                MNHtml html = new MNHtml("http://gigazine.net/news/20161014-nikon-d5500a-star-photo/", "preface","cntimage");
                html.generateMainContent();
                final String temp = html.getMainContents();
                html.getPicture();
                uihandler.post(new Runnable() {
                    @Override
                    public void run() {
                        result.setText(temp);
                    }
                });
                //Log.d("main", "main: " + targetSite.generateMainContent("http://gigazine.net/news/20161012-sns-police-surveillance/"));
            }
        }

        )).start();

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
