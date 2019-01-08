package com.example.masteralarm.data;

import java.util.Locale;
import java.util.TimeZone;

public class PreferenceData {
    public static final int COMMON_ALARM = 1;
    public static final int SHAKE_ALARM = 2;
    public static final int BUBBLE_ALARM = 3;
    public static final int LBS_ALARM = 4;
    public static final int DEFAULT_ALARM = 5;

    public static boolean is24HourFormat = true;
    public static TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
    public static Locale locale= Locale.CHINA;
}
