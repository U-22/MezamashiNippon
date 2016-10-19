package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton button_setting;
        Button button_debug;
        button_setting = (ImageButton)findViewById(R.id.button_setting);
        button_debug = (Button)findViewById(R.id.button_debug);

        //起動時のチュートリアルダイアログ
        final Dialog d = new Dialog(this);
        Window window = d.getWindow();
        // タイトル非表示
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.pager_dialog);
        // 外部タッチで閉じない
        d.setCanceledOnTouchOutside(true);


        d.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        ArrayList<Integer> list = new ArrayList<Integer>();

        list.add(Color.BLUE);
        list.add(Color.GREEN);

        //ViewPagerにPagerAdapterをセット
        DialogPagerAdapter adapter = new DialogPagerAdapter(this, list);
        ViewPager myPager = (ViewPager) d.findViewById(R.id.viewPager);
        myPager.setAdapter(adapter);

        // 縦のサイズを90%にする
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dialogHeight = (int) (metrics.heightPixels * 0.9);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.height = dialogHeight;
        window.setAttributes(lp);

        d.show();

        button_debug.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClassName("com.example.kimata.mezamashinippon","com.example.kimata.mezamashinippon.NewsActivity");
                startActivity(intent);
            }
        });
        button_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.example.kimata.mezamashinippon","com.example.kimata.mezamashinippon.SettingActivity");
                startActivity(intent);
            }
        });
    }
}
