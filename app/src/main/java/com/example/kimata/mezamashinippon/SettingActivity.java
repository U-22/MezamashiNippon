package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by kimata on 16/08/12.
 */

/** 変数
 * 　urlList : 登録されているurlのリスト
 * 　WebUrl : 最後にwebViewで見たサイトのURL
 */

public class SettingActivity extends Activity implements View.OnClickListener {
    TimePickerDialog dialog;
    private Button webButton;
    // url保存用のリスト
    public ArrayList<String> urlList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // NullPointerException防止のためのリスト先頭要素
        urlList.add("first");
        // 前回の設定読み込み
        loadSettingData();

        // webViewから遷移してきた時のみurlをリストビューに追加
        Intent intent1 = getIntent();
        String webUrl = intent1.getStringExtra("url");
        if (webUrl != null ){
            urlList.add(webUrl);
        }
        // NullPointerException防止の先頭要素を削除
        urlList.removeAll(Collections.singletonList("first"));
        // ListViewを更新して表示
        showListView(urlList);

        webButton = (Button) findViewById(R.id.web_botton);
        webButton.setOnClickListener(this);

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
        // 設定保存ボタン押下のとき
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.save_setting);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveSettingData();
                Intent intent = new Intent();
                intent.setClassName("com.example.kimata.mezamashinippon", "com.example.kimata.mezamashinippon.MainActivity");
                startActivity(intent);
            }
        });

        //　url長押しで削除
        ListView listView = (ListView) findViewById(R.id.url_list);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ListView list = (ListView) parent;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.row,R.id.row_textview);
                String item = (String) list.getItemAtPosition(position);
                Log.d("Debug","itemの値"+item);
                // 項目を削除
                adapter.remove(item);
                urlList.remove(position);
                list.setAdapter(adapter);
                Log.d("Debug","remove後のurlList"+urlList);
                showListView(urlList);
                return false;
            }
        });
    }

    @Override
    // webから探すボタンの処理
    public void onClick(View v) {
        saveSettingData();
        Intent intent = new Intent();
        intent.setClassName("com.example.kimata.mezamashinippon", "com.example.kimata.mezamashinippon.WebActivity");

        //intent.putStringArrayListExtra("urlList",urlList);
        startActivity(intent);
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

    private void saveSettingData() {
        // アラームボタン設定の保存
        ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        boolean alarmSwitch = toggleButton.isChecked();

        // アラーム時刻保存
        TextView textView = (TextView)findViewById(R.id.timeDialog);
        String alarmTime = textView.getText().toString();

        // ニュース数
        Spinner spinner = (Spinner)findViewById(R.id.newsNumber);
        int newsNumber = spinner.getSelectedItemPosition();

        // URL保存はurlListをそのまま利用

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        // データを登録する
        editor.putBoolean("AlarmSwitch_key", alarmSwitch);
        editor.putString("AlarmTime_key", alarmTime);
        editor.putInt("NewsNumber_key", newsNumber);

        Gson gson = new Gson();
        String json = gson.toJson(urlList);
        editor.putString("Url_key", json);

        editor.commit();
    }

    private void loadSettingData() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setChecked(sp.getBoolean("AlarmSwitch_key", false));
        TextView textView = (TextView) findViewById(R.id.timeDialog);
        textView.setText(sp.getString("AlarmTime_key", "設定時刻がここに表示されます"));
        ((Spinner) findViewById(R.id.newsNumber)).setSelection(sp.getInt("NewsNumber_key", 0));

        ListView listView = (ListView) findViewById(R.id.url_list);
        String json = sp.getString("Url_key", "");
        Gson gson = new Gson();
        String[] URLS = gson.fromJson(json, String[].class);
        urlList = new ArrayList<>(Arrays.asList(URLS));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingActivity.this, R.layout.row, R.id.row_textview);
        listView.setAdapter(adapter);
        showListView(urlList);
        Log.d("urlList","loadsetting後のurlList"+urlList);
    }

    private void showListView(ArrayList<String> urlList){
        // 登録したwebサイトのURLの一覧であるListViewを表示するメソッド
        if (urlList != null && urlList.size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.row,R.id.row_textview);
            ListView listView = (ListView) findViewById(R.id.url_list);
            for (int i=0; i < urlList.size(); i++){
                adapter.add(urlList.get(i));
            }
            listView.setAdapter(adapter);
        }
    }
}