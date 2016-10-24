package com.example.kimata.mezamashinippon;

import android.app.Service;
import android.os.IBinder;
import android.content.Intent;
import android.util.Log;

/**
 * Created by umino on 16/10/23.
 */

public class MNService extends Service {
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        Thread thread = new Thread(null, mTask, "NewsServiceThread");
        thread.start();
        Log.d("MNSerivec", "onCreate: ");
    }

    Runnable mTask = new Runnable() {
        @Override
        public void run() {
            Intent alarmBroadcast = new Intent();
            alarmBroadcast.setAction("NewsAction");
            sendBroadcast(alarmBroadcast);
            MNService.this.stopSelf();
            Log.d("MNSerive", "run: ");
        }
    };
}
