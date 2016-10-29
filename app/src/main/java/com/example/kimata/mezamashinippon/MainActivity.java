package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.AppLaunchChecker;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton button_setting;
        ImageButton button_debug;
        button_setting = (ImageButton) findViewById(R.id.button_setting);
        button_debug = (ImageButton) findViewById(R.id.button_debug);

        //newsactivityから呼び出されたときのエラーメッセージ
        Intent intent = getIntent();
        int errorState = intent.getIntExtra("ERROR_STATE", 0);
        if(errorState == MNStringResources.NO_SITE)
        {
            Toast.makeText(MainActivity.this, "ニュースサイトが登録されていません", Toast.LENGTH_SHORT).show();
        }else if(errorState == MNStringResources.NO_HTML){
            Toast.makeText(MainActivity.this, "ニュースがありませんでした", Toast.LENGTH_SHORT).show();
        }

        // アプリの起動初回判定
        if (AppLaunchChecker.hasStartedFromLauncher(this)) {
            Log.d("AppLaunchChecker", "2回目以降の起動です");
        } else {
            Log.d("AppLaunchChecker", "初回の起動です");
            //チュートリアルダイアログの表示
            tutorialDialog(findViewById(R.id.tutorial_text));
        }
        AppLaunchChecker.onActivityCreate(this);

        button_debug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isOnlien = MNUtil.isDeviceOnline(MainActivity.this);
                if(isOnlien){
                    Intent intent = new Intent();
                    intent.setClassName("com.example.kimata.mezamashinippon", "com.example.kimata.mezamashinippon.NewsActivity");
                    intent.putExtra("STATE", MNStringResources.ACTIVITY_START_BY_BUTTON);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "オフラインでは利用できません", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.example.kimata.mezamashinippon", "com.example.kimata.mezamashinippon.SettingActivity");
                startActivity(intent);
            }
        });
    }

    // 目覚まし日本とは？　をクリックしたらチュートリアル再び出現
    public void tutorialDialog(View v) {
        final Dialog d = new Dialog(this);
        Window window = d.getWindow();

        // タイトル非表示
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.pager_dialog);
        // 外部タッチで閉じない
        d.setCanceledOnTouchOutside(false);


        d.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        ArrayList<Integer> list = new ArrayList<Integer>();

        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        //ViewPagerにPagerAdapterをセット
        DialogPagerAdapter adapter = new DialogPagerAdapter(this, list);
        ViewPager myPager = (ViewPager) d.findViewById(R.id.viewPager);
        myPager.setAdapter(adapter);

        // 縦のサイズを90%にする
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogHeight = (int) (metrics.heightPixels * 0.9);

        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = dialogHeight;
        window.setAttributes(lp);

        d.show();
    }
    //AppLaunchChecker.onActivityCreate(this);
}
