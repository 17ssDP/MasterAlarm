package com.example.masteralarm;

import android.app.Application;
import android.util.Log;

public class MasterAlarm extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test","app start");
    }
}
