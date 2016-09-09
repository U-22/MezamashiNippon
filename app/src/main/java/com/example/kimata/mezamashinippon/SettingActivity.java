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
 * 　boolean alarmSet : アラームを鳴らすか鳴らさないか
 * 　String alarmHour :　アラームの時間
 * 　String alarmTime :　アラームの分
 * 　int nNumber : 表示するニュース数
 */

public class SettingActivity extends Activity implements View.OnClickListener {
    TimePickerDialog dialog;
    public boolean alarmSet;
    public int alarmHour;
    public int alarmMin;
    public String alarmTime;
    public int nNumber;
    public ArrayList<String> urlList = new ArrayList<>();
    private ArrayList<MNSite> m_siteList = new ArrayList<>();
    private Document m_settingFile;


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
        String webUrl = intent1.getStringExtra("url");
        if (webUrl != null ){
            urlList.add(webUrl);
            //MNSiteを生成しリストに追加
            MNSite newSite = new MNSite(webUrl);
            //TODO:以下の情報がとってこれるようになったら別スレッドで以下の処理を実行
            //newSite.setStartIdentifier();
            //newSite.setEndIdentifier();
            //newSite.generateStartPath();
            //newSite.findRssUrl();
            m_siteList.add(newSite);
        }
        // NullPointerException防止の先頭要素を削除
        urlList.removeAll(Collections.singletonList("ここに登録したサイトのURLが表示されます"));
        // ListViewを更新して表示

        showListView(urlList);

        ImageButton webButton;
        webButton = (ImageButton) findViewById(R.id.web_botton);
        webButton.setOnClickListener(this);

        findViewById(R.id.pickTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //時刻設定ボタンが押された時の処理

                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                //ダイアログの生成及び初期値の設定
                dialog = new TimePickerDialog(SettingActivity.this, android.R.style.Theme_Black, onTimeSetListner, hour, minute, true);

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
            calendar.set(Calendar.HOUR, alarmHour);
            calendar.set(Calendar.MINUTE, alarmMin);
            Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
            PendingIntent pending = PendingIntent.getBroadcast(getApplicationContext(),0, intent, 0);
            // セットする
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);


            String time = String.format("%02d : %02d", hourOfDay, minute);
            TextView textView = (TextView) findViewById(R.id.timeDialog);
            textView.setText(time);
        }
    };

    private void saveSettingData() {
        // アラームボタン設定の保存
        Switch s = (Switch) findViewById(R.id.switch1);
        alarmSet = s.isChecked();

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
        editor.putBoolean("AlarmSwitch_key", alarmSet);
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

    private void loadSettingData() {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Switch s = (Switch) findViewById(R.id.switch1);
        s.setChecked(sp.getBoolean("AlarmSwitch_key", false));
        alarmSet = s.isChecked();

        TextView textView = (TextView) findViewById(R.id.timeDialog);
        textView.setText(sp.getString("AlarmTime_key", "時間が表示されます"));
        alarmHour = sp.getInt("AlarmHour_key", 0);
        alarmMin = sp.getInt("AlarmMin_key", 0);
        ((Spinner) findViewById(R.id.newsNumber)).setSelection(sp.getInt("NewsNumber_key", 0));

        ListView listView = (ListView) findViewById(R.id.url_list);
        String json = sp.getString("Url_key", null);
        Gson gson = new Gson();
        String[] URLS = gson.fromJson(json, String[].class);

        if (URLS != null && URLS.length != 0){
            urlList = new ArrayList<>(Arrays.asList(URLS));
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
            m_siteList = new ArrayList<>(Arrays.asList(siteArray));
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