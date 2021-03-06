package com.example.masteralarm;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TimePicker;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.data.LBSAlarmData;
import com.example.masteralarm.data.PreferenceData;
import com.example.masteralarm.interfaces.AlarmListener;
import com.example.masteralarm.interfaces.LBSAlarmListener;
import com.example.masteralarm.services.ForegroundService;
import com.example.masteralarm.utils.AlarmManagerUtil;
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
    public static final String ALARM_ACTION = "com.example.masteralarm.MY_BROADCAST";

    List<AlarmData> alarmData;
    List<LBSAlarmData> lbsAlarmData;
    List<AlarmListener> listeners;
    List<LBSAlarmListener> lbsAlarmListeners;
    @Override
    public void onCreate() {
        super.onCreate();

        alarmData = LitePal.findAll(AlarmData.class);
        lbsAlarmData = LitePal.findAll(LBSAlarmData.class);
        listeners = new ArrayList<>();
        lbsAlarmListeners = new ArrayList<>();
        //设置时区
        TimeZone.setDefault(PreferenceData.timeZone);

        //百度地图在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        SDKInitializer.setCoordType(CoordType.BD09LL);

    }

    public List<AlarmData> getAlarmData(){
        return alarmData;
    }

    public List<LBSAlarmData> getLbsAlarmData() {
        return lbsAlarmData;
    }

    public void removeLBSAlarm(LBSAlarmData alarmData){
        //从数据库中删除
        LitePal.delete(LBSAlarmData.class,alarmData.getId());
        lbsAlarmData.remove(alarmData);
    }

    public void removeAlarm(AlarmData alarm) {
        //从数据库中删除
        LitePal.delete(AlarmData.class, alarm.getId());
        alarmData.remove(alarm);
    }

    public void onLBSAlarmChange() {
        for (LBSAlarmListener listener : lbsAlarmListeners){
            listener.onLBSAlarmChanged();
        }
    }

    public void onAlarmChange () {
        for (AlarmListener listener : listeners) {
            listener.onAlarmChanged();
        }
    }

    public void addLBSAlarmListener (LBSAlarmListener listener){
        lbsAlarmListeners.add(listener);
    }

    public void addListener (AlarmListener listener){
        listeners.add(listener);
    }

    public void addLBSAlarm(LBSAlarmData data){
        lbsAlarmData.add(data);
        //当加入LBS闹钟时需要的操作
        onLBSAlarmChange();
    }

    public void addAlarm (AlarmData data){
        alarmData.add(data);

        //调用系统启动闹钟提醒
        AlarmManagerUtil.setAlarm(this,data);
        onAlarmChange();
    }

    public void deleteLBSAlarm(LBSAlarmData data){
        if (lbsAlarmData.contains(data)){
            lbsAlarmData.remove(data);
            LitePal.delete(LBSAlarmData.class, data.getId());
            onLBSAlarmChange();
        }
    }

    public void deleteAlarm(AlarmData data){
        if (alarmData.contains(data)){
            alarmData.remove(data);
            //delete from database
            LitePal.delete(AlarmData.class, data.getId());
            AlarmManagerUtil.cancelAlarm(this,ALARM_ACTION,data.getId());
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

    public void startForeground(Context context){
        Intent intent = new Intent(context,ForegroundService.class);
        startService(intent);
    }
}