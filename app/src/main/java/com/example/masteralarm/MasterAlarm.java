package com.example.masteralarm;

import android.app.Application;
import android.util.Log;

import org.litepal.LitePalApplication;

public class MasterAlarm extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test","app start");
    }
}
