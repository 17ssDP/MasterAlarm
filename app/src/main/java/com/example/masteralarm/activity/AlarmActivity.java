package com.example.masteralarm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.masteralarm.MasterAlarm;
import com.example.masteralarm.R;
import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.services.AlarmService;

import java.io.IOException;

import static java.lang.Thread.sleep;

public class AlarmActivity extends AppCompatActivity {

    private MasterAlarm application;
    private AlarmData alarmData;
    private Button btn;
    private AlarmService.AlarmBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (AlarmService.AlarmBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        btn = findViewById(R.id.stop_alarm_btn);
        application = (MasterAlarm)getApplicationContext();
        alarmData = getIntent().getParcelableExtra("alarmdata");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binder.stopAlarm();
                Log.d("test","vibrator stops");
            }
        });
        Intent intent = new Intent(this,AlarmService.class);
        if (alarmData == null){
            intent.putExtra("isRing",true);
            intent.putExtra("isVibrate",true);
            intent.putExtra("ringPath",getSystemDefultRingtoneUri().getPath());
        }
        else {
            intent.putExtra("isRing",alarmData.isHasSound());
            intent.putExtra("isVibrate",alarmData.isVibrate());
            intent.putExtra("ringPath",getSystemDefultRingtoneUri().getPath());
        }

        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);

        Button btn1 = findViewById(R.id.ring_alarm_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binder.startAlarm();
            }
        });
        Button btn2 = findViewById(R.id.stop_ring_alarm_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binder.stopAlarm();
            }
        });
    }

    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
    }
}
