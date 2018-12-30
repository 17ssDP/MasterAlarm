package com.example.masteralarm;

import android.app.Application;
import android.util.Log;

import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.data.PreferenceData;
import com.example.masteralarm.interfaces.AlarmListener;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class MasterAlarm extends LitePalApplication {

    List<AlarmData> alarmData;
    List<AlarmListener> listeners;
    @Override
    public void onCreate() {
        super.onCreate();
        alarmData = new ArrayList<>();
        listeners = new ArrayList<>();

        //设置时区
        TimeZone.setDefault(PreferenceData.timeZone);
    }

    private void initializeData(){

    }

    public List<AlarmData> getAlarmData(){
        return alarmData;
    }

    public void onAlarmChange(){
        for (AlarmListener listener:listeners){
            listener.onAlarmChanged();
        }
    }

    public void addListener(AlarmListener listener){
        listeners.add(listener);
    }

    public void addAlarm(AlarmData data){
        alarmData.add(data);
        onAlarmChange();
    }

    public void deleteAlarm(AlarmData data){
        if (alarmData.contains(data)){
            alarmData.remove(data);
            onAlarmChange();
        }
    }
}
