package com.example.masteralarm.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;
import androidx.annotation.Nullable;

import static java.lang.Thread.sleep;

public class AlarmService extends Service {
    private Vibrator vibrator;
    private MediaPlayer mediaPlayer;
    private boolean isVibrate;
    private boolean isRing;
    private String path;//音乐路径
    private AlarmBinder binder;//绑定器
    private int alarmTime = 60000;//持续时间，毫秒
    private long[] frequency = new long[]{1000,500};//震动频率
    private boolean isOn;//是否出于闹钟状态

    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int sup = super.onStartCommand(intent, flags, startId);
        isRing = intent.getBooleanExtra("isRing",true);
        isVibrate = intent.getBooleanExtra("isVibrate",true);
        path = intent.getStringExtra("ringPath");

        binder = new AlarmBinder();
        isOn = true;
        try {
            if (isRing){
                mediaPlayer.setDataSource(this,getSystemDefultRingtoneUri());
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sup;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class AlarmBinder extends Binder{
        public void startAlarm(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isVibrate){
                        vibrator.vibrate(frequency,0);
                    }
                    if (isRing){
                        mediaPlayer.start();
                    }
                    Log.d("test","begin alarm");

                    //等待一定时间后，关闭闹钟
                    try {
                        sleep(alarmTime);//单位毫秒
                        stopAlarm();
                    }
                    catch (Exception e){
                        Log.d("test","error on sleep in service");
                    }
                }
            }).start();
        }

        public void stopAlarm(){
            if (isOn){
                if (vibrator.hasVibrator()){
                    vibrator.cancel();
                }
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }
                isOn = false;

                //关闭服务，避免占用资源
                stopSelf();
            }
        }

        public void setAlarmTime(int time){
            alarmTime = time;
        }

        public void setVibrator(long[] newFrequency){
            if (newFrequency.length >= 2){
                frequency = newFrequency;
            }
        }
    }

    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
    }
}
