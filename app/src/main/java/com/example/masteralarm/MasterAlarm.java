package com.example.masteralarm;

import android.app.Application;
import android.util.Log;

import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.interfaces.AlarmListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MasterAlarm extends LitePalApplication {
    private List<AlarmData> alarms;
    List<AlarmData> alarmData;
    List<AlarmListener> listeners;
    private SimpleExoPlayer player;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test","app start");
        alarms = LitePal.findAll(AlarmData.class);
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
    }

    public List<AlarmData> getAlarms(){
        return alarms;
    }

    public void removeAlarm(AlarmData alarm) {
        //从数据库中删除
        LitePal.delete(AlarmData.class, alarm.getId());
        alarms.remove(alarm);
    }

    public AlarmData newAlarm() {
        AlarmData alarm = new AlarmData(1, Calendar.getInstance());
        alarm.save();
        alarms.add(alarm);
        return alarm;
    }

    private void initializeData () {

    }
    public List<AlarmData> getAlarmData () {
        return alarmData;
    }

    public void onAlarmChange () {
        for (AlarmListener listener : listeners) {
            listener.onAlarmChanged();
        }
    }
    public void addListener (AlarmListener listener){
        listeners.add(listener);
    }
    public void addAlarm (AlarmData data){
        alarmData.add(data);
        onAlarmChange();
    }

    public void deleteAlarm (AlarmData data){
        alarmData.remove(data);
        onAlarmChange();
    }
}