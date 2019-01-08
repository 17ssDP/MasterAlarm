package com.example.masteralarm.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.masteralarm.data.AlarmData;

import java.util.Calendar;

public class AlarmManagerUtil {
    //broadcast massage
    public static final String ALARM_ACTION = "com.example.masteralarm.MY_BROADCAST";

    public static void setAlarmTime(Context context, long timeInMillis, Intent intent) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent sender = PendingIntent.getBroadcast(context, intent.getIntExtra("id", 0),
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        int interval = (int) intent.getLongExtra("intervalMillis", 0);
        am.setWindow(AlarmManager.RTC_WAKEUP, timeInMillis, interval, sender);
    }

    public static void cancelAlarm(Context context, String action, int id) {
        Intent intent = new Intent(action);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent
                .FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    public static void setAlarm(Context context, int flag, int hour, int minute, int id, int
            week, String tips, int soundOrVibrator) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long intervalMillis = 0;
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get
                (Calendar.DAY_OF_MONTH), hour, minute, 10);
        if (flag == 0) {
            intervalMillis = 0;
        } else if (flag == 1) {
            intervalMillis = 24 * 3600 * 1000;
        } else if (flag == 2) {
            intervalMillis = 24 * 3600 * 1000 * 7;
        }
        Intent intent = new Intent(ALARM_ACTION);
        intent.setComponent(new ComponentName("com.example.masteralarm","com.example.masteralarm.receivers.AlarmBroadcastReceiver"));
        intent.putExtra("intervalMillis", intervalMillis);
        intent.putExtra("msg", tips);
        intent.putExtra("id", id);
        intent.putExtra("soundOrVibrator", soundOrVibrator);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, PendingIntent
                .FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, calMethod(week, calendar.getTimeInMillis()), sender);
        } else {
            if (flag == 0) {
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
            } else {
                am.setRepeating(AlarmManager.RTC_WAKEUP, calMethod(week, calendar.getTimeInMillis
                        ()), intervalMillis, sender);
            }
        }
    }

    //set alarm function
    public static void setAlarm(Context context, AlarmData alarmData) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = alarmData.getCalendarTime();
        Calendar cur = Calendar.getInstance();
        long intervalMillis = 24 * 3600 * 1000;
        Intent intent = new Intent(ALARM_ACTION);

        //如果已经关闭则不需要设置闹钟
        if (!alarmData.isEnable()){
            return;
        }

        cur.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR,cur.get(Calendar.YEAR));
        calendar.set(Calendar.DAY_OF_YEAR,cur.get(Calendar.DAY_OF_YEAR));
        if (!alarmData.isRepeat()){
            //如果还是落后，日期加一
            if (calendar.before(cur)){
                calendar.add(5,1);
            }
        }
        else {
            int day = cur.get(Calendar.DAY_OF_WEEK);
            boolean[] isRepeat = alarmData.getRepeat();
            //day为1时是星期天
            day--;
            while (true){
                if (isRepeat[day]){
                    if (cur.before(calendar)){
                        break;
                    }
                    //如果不重复，日期加一天
                    day = (day+1)%7;
                    calendar.add(5,1);
                }
            }
        }

        //保证应用在关闭状态也能接收到广播
        if(android.os.Build.VERSION.SDK_INT >=12) {
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);//3.1以后的版本需要设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        }

        intent.setPackage(context.getPackageName());
        intent.setComponent(new ComponentName("com.example.masteralarm","com.example.masteralarm.receivers.AlarmBroadcastReceiver"));
        intent.putExtra("type",alarmData.getType());
        intent.putExtra("alarmdata",alarmData);
        intent.putExtra("intervalMillis", intervalMillis);
        intent.putExtra("alarmid",alarmData.getId());
        intent.putExtra("type", alarmData.getType());
        Log.d("before intent:", "" + alarmData.getType());
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmData.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
//            am.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, sender);
        } else {
            //setExact
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }

    private static long calMethod(int weekflag, long dateTime) {
        long time = 0;
        //weekflag == 0表示是按天为周期性的时间间隔或者是一次行的，weekfalg非0时表示每周几的闹钟并以周为时间间隔
        if (weekflag != 0) {
            Calendar c = Calendar.getInstance();
            int week = c.get(Calendar.DAY_OF_WEEK);
            if (1 == week) {
                week = 7;
            } else if (2 == week) {
                week = 1;
            } else if (3 == week) {
                week = 2;
            } else if (4 == week) {
                week = 3;
            } else if (5 == week) {
                week = 4;
            } else if (6 == week) {
                week = 5;
            } else if (7 == week) {
                week = 6;
            }

            if (weekflag == week) {
                if (dateTime > System.currentTimeMillis()) {
                    time = dateTime;
                } else {
                    time = dateTime + 7 * 24 * 3600 * 1000;
                }
            } else if (weekflag > week) {
                time = dateTime + (weekflag - week) * 24 * 3600 * 1000;
            } else if (weekflag < week) {
                time = dateTime + (weekflag - week + 7) * 24 * 3600 * 1000;
            }
        } else {
            if (dateTime > System.currentTimeMillis()) {
                time = dateTime;
            } else {
                time = dateTime + 24 * 3600 * 1000;
            }
        }
        return time;
    }


}
