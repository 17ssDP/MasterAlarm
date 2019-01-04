package com.example.masteralarm.data;

import com.baidu.mapapi.model.LatLng;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class LBSAlarmData extends LitePalSupport implements Serializable {

    private int id;
    private int isEnable;
    private double latitude;
    private double longitude;
    private String name;
    private String start;
    private String end;
    private int isVibrate;
    private int isRing;

    public LBSAlarmData(){

    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsEnable() {
        return isEnable == 1;
    }

    public void setIsEnable(boolean isEnable) {
        this.isEnable = isEnable?1:0;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsVibrate() {
        return isVibrate==1;
    }

    public void setIsVibrate(boolean isVibrate) {
        this.isVibrate = isVibrate?1:0;
    }

    public boolean getIsRing() {
        return isRing == 1;
    }

    public void setIsRing(boolean isRing) {
        this.isRing = isRing?1:0;
    }
}
