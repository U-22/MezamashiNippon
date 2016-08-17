package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by kimata on 16/08/12.
 */
public class SettingActivity extends Activity implements View.OnClickListener {

    TimePickerDialog dialog;
    private Button timePickerButton;
    private Button webButton;
    private Button saveButton;


    /*
    // web検索ボタンのリスナー
    View.OnClickListener webButtonClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v){
            setContentView(R.layout.activity_websearch);

            //webView.setWebViewClient(new WebViewClient());
            //webView.loadUrl("http://google.co.jp");
        }
    };
    // webページ追加ボタンのリスナー
    View.OnClickListener addPageButtonClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            // ページurlを取得
            String url = String.format(webView.getUrl());
            // 設定画面に戻る
            setContentView(R.layout.activity_setting);
            // urlをリストビューに追加
            ListView listView = (ListView) findViewById(R.id.url_list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingActivity.this,
                    android.R.layout.simple_list_item_1);
            listView.setAdapter(adapter);
            adapter.add(url);

            // Toastでも表示
            Toast.makeText(SettingActivity.this,
                    "サイト " + url + " を登録しました", Toast.LENGTH_SHORT).show();
        }
    };
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        webButton = (Button) findViewById(R.id.web_botton);
        webButton.setOnClickListener(this);
        //addPageButton = (Button)findViewById(R.id.add_page);
        //addPageButton.setOnClickListener(this);

        findViewById(R.id.pickTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //時刻設定ボタンが押された時の処理

                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                //ダイアログの生成及び初期値の設定
                dialog = new TimePickerDialog(SettingActivity.this, android.R.style.Theme_Black, onTimeSetListner, hour, minute, false);

                dialog.show();

            }
        });
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.save_setting);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.example.kimata.mezamashinippon", "com.example.kimata.mezamashinippon.MainActivity");
                startActivity(intent);
            }
        });
        /*
        // Webから探すボタンの処理
        findViewById(R.id.web_botton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //ブラウザでページ追加ボタンを押したとき
                findViewById(R.id.add_page).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 設定画面に戻る
                        setContentView(R.layout.activity_setting);
                        // urlをtextViewに表示
                        String url = String.format(webView.getUrl());
                        ListView listView = (ListView)findViewById(R.id.url_list);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingActivity.this,
                                android.R.layout.simple_list_item_1);
                        listView.setAdapter(adapter);
                        adapter.add(url);

                        // Toastでも表示
                        Toast.makeText(SettingActivity.this,
                                "サイト "+url+ " を登録しました", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        }); */
    }
    @Override
    public void onClick(View v){
        setContentView(R.layout.activity_websearch);
        final WebView webView = (WebView) findViewById(R.id.websearchview);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://google.co.jp");
        //ブラウザでページ追加ボタンを押したとき
        findViewById(R.id.add_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ページurlを取得
                String url = String.format(webView.getUrl());
                // 設定画面に戻る
                setContentView(R.layout.activity_setting);

                // urlをリストビューに追加
                ListView listView = (ListView) findViewById(R.id.url_list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingActivity.this,
                        android.R.layout.simple_list_item_1);
                listView.setAdapter(adapter);
                adapter.add(url);
                // Toastでも表示
                Toast.makeText(SettingActivity.this,
                        "サイト " + url + " を登録しました", Toast.LENGTH_SHORT).show();
                restartSettingMenu();
            }

        });
    }
    private void restartSettingMenu(){
        webButton = (Button) findViewById(R.id.web_botton);
        webButton.setOnClickListener(this);
        //addPageButton = (Button)findViewById(R.id.add_page);
        //addPageButton.setOnClickListener(this);

        findViewById(R.id.pickTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //時刻設定ボタンが押された時の処理

                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                //ダイアログの生成及び初期値の設定
                dialog = new TimePickerDialog(SettingActivity.this, android.R.style.Theme_Black, onTimeSetListner, hour, minute, false);

                dialog.show();

            }
        });
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.save_setting);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.example.kimata.mezamashinippon", "com.example.kimata.mezamashinippon.MainActivity");
                startActivity(intent);
            }
        });

    }

    // TimePickerDialog内のボタンリスナー
    TimePickerDialog.OnTimeSetListener onTimeSetListner = new OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            Toast.makeText(SettingActivity.this,
                    "アラームを " + Integer.toString(hourOfDay) + " : "
                            + Integer.toString(minute) + " にセットしました", Toast.LENGTH_SHORT).show();
            String time = String.format("%02d : %02d", hourOfDay, minute);
            TextView textView = (TextView) findViewById(R.id.timeDialog);
            textView.setText(time);
        }
    };

    private void saveSettigData(Context context, String time, boolean setAlarm, String urls ){
        // アプリ標準の Preference　を取得
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        // データを登録する
        editor.putString("time",time);
        editor.putString("urls",urls);
        editor.commit();
    }
    private String loadTime(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("time", "");
    }
    private String loadUrl(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("urls","");
    }
}