package com.example.masteralarm.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.masteralarm.R;
import com.example.masteralarm.activity.MainActivity;

import java.util.Calendar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ForegroundService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = "com.example.masteralarm.foregroundservice";
    public static final String NOTIFICATION_CHANNEL_ID_TASK = "com.example.masteralarm";
    private static final int SERVICE_ID = 100;
    private static final String NOTIFICATION_CHANNEL_ID_INFO = "com.example.masteralarm";
    private String CHANNEL_ID = "ALARM FOREGROUND SERVICE";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("test","foreground start");
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            initChannel();
            notification = new Notification.Builder(this)
                    .setContentTitle("Alarm")
                    .setContentText("0 Clocks ready")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(Calendar.getInstance().getTimeInMillis())
                    .setChannelId(NOTIFICATION_CHANNEL_ID_SERVICE)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent).build();
        }
        else{
            notification = new Notification.Builder(this)
                    .setContentTitle("Alarm")
                    .setContentText("0 Clocks ready")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(Calendar.getInstance().getTimeInMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent).build();
        }
        startForeground(SERVICE_ID,notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_DEFAULT));
        }
    }
}
