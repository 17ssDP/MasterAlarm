package com.example.masteralarm.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.masteralarm.adapters.AlarmAdapter;

import org.litepal.crud.LitePalSupport;

import java.util.Calendar;
import java.util.concurrent.CancellationException;

import static android.preference.PreferenceManager.getDefaultSharedPreferencesName;

public class AlarmData extends LitePalSupport {
    private int id;
    private boolean isEnable;   // 闹钟是否处于开启状态
    private boolean isVibrate;  //是否震动
    private boolean[] repeat;   //重复信息
    private String label;       //闹钟标签
    private Calendar time;      //闹钟时间
    private ToneData tone;          //铃声
    private boolean hasSound;       //是否有声音

    public AlarmData(int id) {

    }

    public AlarmData(int id, Calendar time) {
        this.id = id;
        this.time = time;
    }
    public AlarmData(int id, boolean isEnable, boolean isVibrate, boolean[] repeat, String label, Calendar time, ToneData tone) {
        this.id = id;
        isEnable = isEnable;
        isVibrate = isVibrate;
        repeat = repeat;
        label = label;
        time = time;
        tone = tone;
    }
    public AlarmData(int id, Context context) {
        this.id = id;
//        SharedPreferences sharedPreferences = context.getSharedPreferences(getDefaultSharedPreferencesName(context),
//                getDefaultSharedPreferencesMode());
    }

    public boolean isRepeat() {
        for (boolean day : repeat) {
            if (day)
                return true;
        }

        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isVibrate() {
        return isVibrate;
    }

    public void setVibrate(boolean vibrate) {
        isVibrate = vibrate;
    }

    public boolean[] getRepeat() {
        return repeat;
    }

    public void setRepeat(boolean[] repeat) {
        this.repeat = repeat;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public ToneData getTone() {
        return tone;
    }

    public void setTone(ToneData tone) {
        this.tone = tone;
    }

    public boolean isHasSound() {
        return hasSound;
    }

    public void setHasSound(boolean hasSound) {
        this.hasSound = hasSound;
    }
}
