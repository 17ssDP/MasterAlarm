package com.example.masteralarm.data;


import android.content.Context;
import android.net.Uri;
import android.os.Parcel;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.CancellationException;

import static android.preference.PreferenceManager.getDefaultSharedPreferencesName;

public class AlarmData extends LitePalSupport implements Serializable {
    private int id;
    private int isEnable;   // 闹钟是否处于开启状态
    private int isVibrate;  //是否震动
    private int day1;
    private int day2;
    private int day3;   //重复信息, 1 代表响
    private int day4;
    private int day5;
    private int day6;
    private int day7;
    private String label;       //闹钟标签
    private long time;      //闹钟时间
    private transient Uri tone;          //铃声
    private int hasSound;       //是否有声音
    private int type = 1;       //闹钟类型：0为普通闹钟、1为甩手闹钟、2为吹气闹钟、3为地点闹钟

    public AlarmData(int id) {

    }

    public AlarmData(int id, Calendar time) {
        this.id = id;
        this.time = time.getTimeInMillis();
    }
//    public AlarmData(int id, boolean isEnable, boolean isVibrate, boolean[] repeat, String label, Calendar time, ToneData tone) {
//        this.id = id;
//        isEnable = isEnable;
//        isVibrate = isVibrate;
//        repeat = repeat;
//        label = label;
//        time = time;
//        tone = tone;
//    }
    public AlarmData(int id, Context context) {
        this.id = id;
//        SharedPreferences sharedPreferences = context.getSharedPreferences(getDefaultSharedPreferencesName(context),
//                getDefaultSharedPreferencesMode());
    }

    public boolean isRepeat() {
        if(day1 == 1 || day2 == 1 || day3 == 1|| day4 == 1 || day5 == 1|| day6 == 1 || day7 == 1)
            return true;
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnable() {
        return isEnable == 1;
    }

    public void setEnable(boolean enable) {
        isEnable = enable? 1 : 0;
    }

    public boolean isVibrate() {
        return isVibrate == 1;
    }

    public void setVibrate(boolean vibrate) {
        isVibrate = vibrate? 1 : 0;
    }

    public boolean[] getRepeat() {
        return new boolean[]{day1 == 1, day2 == 1, day3 == 1, day4 == 1, day5 == 1, day6 == 1, day7 == 1};
    }

    public void setRepeat(boolean[] repeat) {
        day1 = repeat[0]? 1 : 0;
        day2 = repeat[0]? 1 : 0;
        day3 = repeat[0]? 1 : 0;
        day4 = repeat[0]? 1 : 0;
        day5 = repeat[0]? 1 : 0;
        day6 = repeat[0]? 1 : 0;
        day7 = repeat[0]? 1 : 0;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Uri getTone() {
        return tone;
    }

    public void setTone(Uri tone) {
        this.tone = tone;
    }

    public boolean isHasSound() {
        return hasSound == 1;
    }

    public void setHasSound(boolean hasSound) {
        this.hasSound = hasSound? 1 : 0;
    }

    public Calendar getCalendarTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return  calendar;
    }

    public void setCalendarTime(Calendar calendarTime) {
        time = calendarTime.getTimeInMillis();
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        type = type;
    }
}
