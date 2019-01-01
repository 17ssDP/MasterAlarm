package com.example.masteralarm.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Calendar;

public class AlarmData extends LitePalSupport implements Serializable {
    private int id;
    private boolean isEnable;   // 闹钟是否处于开启状态
    private boolean isVibrate;  //是否震动
    private boolean[] repeat;   //重复信息
    private String label;       //闹钟标签
    private Calendar time;      //闹钟时间
    private transient Uri tone;          //铃声
    private boolean hasSound;       //是否有声音

    public AlarmData() {

    }

    public AlarmData(int id) {
        this.id = id;
    }

    protected AlarmData(Parcel in) {
        id = in.readInt();
        isEnable = in.readByte() != 0;
        isVibrate = in.readByte() != 0;
        repeat = in.createBooleanArray();
        label = in.readString();
        hasSound = in.readByte() != 0;
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

    public Uri getTone() {
        return tone;
    }

    public void setTone(Uri tone) {
        this.tone = tone;
    }

    public boolean isRepeat() {
        for (boolean day : repeat) {
            if (day)
                return true;
        }

        return false;
    }

    public boolean isHasSound() {
        return hasSound;
    }

    public void setHasSound(boolean hasSound) {
        this.hasSound = hasSound;
    }
}
