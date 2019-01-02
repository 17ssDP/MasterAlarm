package com.example.masteralarm;
import android.util.Log;
import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.data.PreferenceData;
import com.example.masteralarm.interfaces.AlarmListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import org.litepal.LitePal;
import org.litepal.LitePalApplication;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MasterAlarm extends LitePalApplication {
    List<AlarmData> alarmData;
    List<AlarmListener> listeners;
    private SimpleExoPlayer player;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test","app start");
        alarmData = LitePal.findAll(AlarmData.class);
        listeners = new ArrayList<>();
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        //设置时区
        TimeZone.setDefault(PreferenceData.timeZone);
    }

    public List<AlarmData> getAlarmData(){
        return alarmData;
    }

    public void removeAlarm(AlarmData alarm) {
        //从数据库中删除
        LitePal.delete(AlarmData.class, alarm.getId());
        alarmData.remove(alarm);
    }

    public AlarmData newAlarm() {
        AlarmData alarm = new AlarmData(1, Calendar.getInstance());
//        alarm.save();
//        alarmData.add(alarm);
        return alarm;
    }

    private void initializeData () {

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

    public void deleteAlarm(AlarmData data){
        if (alarmData.contains(data)){
            alarmData.remove(data);
            onAlarmChange();
        }
    }
}