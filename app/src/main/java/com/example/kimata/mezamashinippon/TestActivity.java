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

        targetSite = new MNSite("http://gigazine.net/");
        targetSite.setStartIdentifier("映画化もされた冒険小説");
        targetSite.setEndIdentifier("めて公開しています。");
        (new Thread(new Runnable() {
            @Override
            public void run() {
                targetSite.generateStartPath("http://gigazine.net/news/20160810-project-sauron/");
                targetSite.findRssUrl();
                Calendar calendar = Calendar.getInstance();
                calendar.set(2016, 7, 21);
                targetSite.addNewArticle(calendar.getTime());
                targetSite.generateHtml();
                final ArrayList<MNHtml> htmlList = targetSite.getHtmlList();
                htmlList.get(0).findStartElement();
                htmlList.get(0).findEndElement();
                htmlList.get(0).generateMainContents();
                final String temp = htmlList.get(0).getMainContents();
                Log.d("element", htmlList.get(0).getStartElementString());
                uihandler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       result.setText(temp);
                                   }
                               }

                );
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
