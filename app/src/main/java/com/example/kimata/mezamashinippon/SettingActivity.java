package com.example.kimata.mezamashinippon;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by kimata on 16/08/12.
 */
public class SettingActivity extends Activity {

    TimePickerDialog dialog;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViewById(R.id.pickTime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //時刻設定ボタンが押された時の処理

                Calendar calendar = Calendar.getInstance();
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                final int minute = calendar.get(Calendar.MINUTE);

                //ダイアログの生成及び初期値の設定
                dialog = new TimePickerDialog(SettingActivity.this, android.R.style.Theme_Light, onTimeSetListner, hour, minute, false);

                dialog.show();

            }
        });
    }
}