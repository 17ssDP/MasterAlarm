package com.example.masteralarm.data;

import org.litepal.crud.LitePalSupport;

import java.util.Calendar;

public class AlarmData extends LitePalSupport {
    private int id;
    private boolean isEnable;   // 闹钟是否处于开启状态
    private boolean isVibrate;  //是否震动
    private boolean[] repeat;   //重复信息
    private String label;       //闹钟标签
    private Calendar time;      //闹钟时间
    private ToneData tone;          //铃声

    public AlarmData() {

    }

    public AlarmData(int id) {
        this.id = id;
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
}
