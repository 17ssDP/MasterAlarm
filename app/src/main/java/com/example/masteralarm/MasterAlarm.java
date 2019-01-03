package com.example.masteralarm;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TimePicker;

import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.data.PreferenceData;
import com.example.masteralarm.interfaces.AlarmListener;
import com.example.masteralarm.services.ForegroundService;
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
    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d("test","app start");
 //       LitePal.deleteAll(AlarmData.class);
        alarmData = LitePal.findAll(AlarmData.class);
//        Log.d("Alarm Number", alarmData.size() + " ");
//        Log.d("Alarm", alarmData.get(0).getLabel());
//        Log.d("Alarm Calendar", alarmData.get(0).getTime().toString());
        listeners = new ArrayList<>();
        //设置时区
        TimeZone.setDefault(PreferenceData.timeZone);
//        startForeground();

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

    public Calendar createTimePicker(Context context){
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog pickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
            }
        }, hour, minute, true);
        pickerDialog.show();
        while (pickerDialog.isShowing()){
            try {
                wait(100);
            }
            catch (Exception e){
                break;
            }
        }
        return c;
    }

    private void startForeground(Context context){
        Intent intent = new Intent(context,ForegroundService.class);
        startService(intent);
    }
}