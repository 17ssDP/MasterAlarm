package com.example.masteralarm;

import android.app.Application;
import android.util.Log;

import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.interfaces.AlarmListener;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;

public class MasterAlarm extends LitePalApplication {

    List<AlarmData> alarmData;
    List<AlarmListener> listeners;
    @Override
    public void onCreate() {
        super.onCreate();
        alarmData = new ArrayList<>();
        listeners = new ArrayList<>();
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
        alarmData.remove(data);
        onAlarmChange();
    }
}
