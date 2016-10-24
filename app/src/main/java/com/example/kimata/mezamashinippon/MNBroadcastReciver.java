package com.example.kimata.mezamashinippon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by umino on 16/10/23.
 */

public class MNBroadcastReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent news = new Intent();
        news.setClassName(MNStringResources.PACKAGE, MNStringResources.PACKAGE + ".NewsActivity");
        news.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(news);
        Log.d("MNBroadcastReciver", "onReceive: ");
    }
}
