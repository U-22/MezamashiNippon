package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.app.SharedElementCallback;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import org.jsoup.nodes.Document;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by kimata on 16/08/12.
 */

/** public変数
 * 　ArrayList<String> urlList : 登録されているurlのリスト
 * 　String alarmHour :　アラームの時間
 * 　String alarmTime :　アラームの分
 * 　int nNumber : 表示するニュース数
 */

public class SettingActivity extends Activity implements View.OnClickListener {
    TimePickerDialog dialog;
    public int alarmHour;
    public int alarmMin;
    public String alarmTime;
    public int nNumber;
    public ArrayList<String> urlList = new ArrayList<String>();
    private ArrayList<MNSite> m_siteList = new ArrayList<MNSite>();
    private Document m_settingFile;
    private AlarmManager m_alarmManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // NullPointerException防止のためのリスト先頭要素
        urlList.add("ここに登録したサイトのURLが表示されます");
        // 前回の設定読み込み
        loadSettingData();
        setnNumber();

        // webViewから遷移してきた時のみurlをリストビューに追加
        Intent intent1 = getIntent();
        final String webUrl = intent1.getStringExtra("url");
        final String articleUrl = intent1.getStringExtra("samplePageURL");
        final String webStartIdentifier = intent1.getStringExtra("startArticle");
        if (webUrl != null ){
            urlList.add(webUrl);
            //MNSiteを生成しリストに追加
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    MNSite newSite = new MNSite(webUrl);
                    newSite.setStartIdentifier(webStartIdentifier);
                    newSite.findStartClassName(articleUrl);
                    newSite.findRssUrl();
                    m_siteList.add(newSite);
                }
            })).start();
            Toast.makeText(SettingActivity.this,
                    "ニュースサイトを追加しました。", Toast.LENGTH_SHORT).show();
        }
        // NullPointerException防止の先頭要素を削除
        urlList.removeAll(Collections.singletonList("ここに登録したサイトのURLが表示されます"));
        // ListViewを更新して表示

        showListView(urlList);

        ImageButton webButton;
        webButton = (ImageButton) findViewById(R.id.web_botton);
        webButton.setOnClickListener(this);

        findViewById(R.id.time_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //時刻設定ボタンが押された時の処理

                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                //ダイアログの生成及び初期値の設定
                dialog = new TimePickerDialog(SettingActivity.this, onTimeSetListner, hour, minute, true);
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
                if (item == "ここに登録したサイトのURLが表示されます"){
                    //削除しない
                    return false;
                }
                // 項目を削除
                adapter.remove(item);
                urlList.remove(position);
                list.setAdapter(adapter);
                showListView(urlList);
                m_siteList.remove(position);
                clearMNSiteList();
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
        startActivity(intent);
    }

    // TimePickerDialog内のボタンリスナー
    TimePickerDialog.OnTimeSetListener onTimeSetListner = new OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            Toast.makeText(SettingActivity.this,
                    "アラームを " + Integer.toString(hourOfDay) + " : "
                            + Integer.toString(minute) + " にセットしました", Toast.LENGTH_SHORT).show();
            alarmHour = hourOfDay;
            alarmMin = minute;

            // 設定時刻になったらニュース画面に遷移する設定
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            //calendar.add(Calendar.SECOND, 5);
            calendar.set(Calendar.HOUR, alarmHour);
            calendar.set(Calendar.MINUTE, alarmMin);
            if(calendar.getTimeInMillis() < System.currentTimeMillis())
            {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            Intent intent = new Intent(getApplicationContext(), MNBroadcastReciver.class);
            PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(),0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // セットする
            m_alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            //m_alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);
            m_alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending);


            String time = String.format("%02d : %02d", hourOfDay, minute);
            TextView textView = (TextView) findViewById(R.id.timeDialog);
            textView.setText(time);
        }
    };

    private void saveSettingData() {
        // アラームボタン設定の保存

        // アラーム時刻保存
        TextView textView = (TextView)findViewById(R.id.timeDialog);
        alarmTime = textView.getText().toString();


        // ニュース数
        Spinner spinner = (Spinner)findViewById(R.id.newsNumber);
        int newsNumber = spinner.getSelectedItemPosition();

        // URL保存はurlListをそのまま利用

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        // データを登録する
        editor.putString("AlarmTime_key", alarmTime);
        editor.putInt("AlarmHour_key", alarmHour);
        editor.putInt("AlarmMin_key", alarmMin);
        editor.putInt("NewsNumber_key", newsNumber);

        Gson gson = new Gson();
        String json = gson.toJson(urlList);
        editor.putString("Url_key", json);

        editor.commit();

        saveMNSiteList();
    }

    private void saveMNSiteList(){
        SharedPreferences sp = getSharedPreferences(MNStringResources.SETTING_FILE_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String siteList = gson.toJson(m_siteList);
        sp.edit().putString("MNSiteList", siteList).commit();
    }

    private void clearMNSiteList(){
        SharedPreferences sp = getSharedPreferences(MNStringResources.SETTING_FILE_NAME, MODE_PRIVATE);
        sp.edit().clear().commit();
    }

    private void loadSettingData() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        TextView textView = (TextView) findViewById(R.id.timeDialog);
        textView.setText(sp.getString("AlarmTime_key", "時刻"));
        alarmHour = sp.getInt("AlarmHour_key", 0);
        alarmMin = sp.getInt("AlarmMin_key", 0);
        ((Spinner) findViewById(R.id.newsNumber)).setSelection(sp.getInt("NewsNumber_key", 0));

        ListView listView = (ListView) findViewById(R.id.url_list);
        String json = sp.getString("Url_key", null);
        Gson gson = new Gson();
        String[] URLS = gson.fromJson(json, String[].class);

        if (URLS != null && URLS.length != 0){
            urlList = new ArrayList<String>(Arrays.asList(URLS));
        }

        loadMNSiteList();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingActivity.this, R.layout.row, R.id.row_textview);
        listView.setAdapter(adapter);
        showListView(urlList);
    }

    private void loadMNSiteList(){
        SharedPreferences sp = getSharedPreferences(MNStringResources.SETTING_FILE_NAME, MODE_PRIVATE);
        String siteList = sp.getString("MNSiteList", "");
        Gson gson = new Gson();
        MNSite[] siteArray = gson.fromJson(siteList, MNSite[].class);
        if(siteArray != null && siteArray.length != 0)
        {
            m_siteList = new ArrayList<MNSite>(Arrays.asList(siteArray));
        }
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

    private void setnNumber(){
        // Spinnerオブジェクトを取得
        Spinner spinner = (Spinner)findViewById(R.id.newsNumber);

        // 選択されているアイテムを取得
        String item = (String)spinner.getSelectedItem();
        nNumber=Integer.parseInt(item);
    }

    private Boolean checkSettingFileExisit()
    {
        final String[] fileList = this.fileList();
        final int size = fileList.length;
        for(int i= 0; i < size; i++)
        {
            String fileName = fileList[i];
            if(fileName.equals(MNStringResources.SETTING_FILE_NAME))
            {
                return true;
            }
        }
        return false;
    }

    private void loadSettingFile()
    {
        //m_settingFile =
    }
}