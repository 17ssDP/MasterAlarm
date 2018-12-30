package com.example.masteralarm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.masteralarm.MasterAlarm;
import com.example.masteralarm.R;
import com.example.masteralarm.data.AlarmData;

import static java.lang.Thread.sleep;

public class AlarmActivity extends AppCompatActivity {

    private MasterAlarm application;
    private AlarmData alarmData;
    private Vibrator vibrator;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        btn = findViewById(R.id.stop_alarm_btn);
        application = (MasterAlarm)getApplicationContext();
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        alarmData = getIntent().getParcelableExtra("alarmdata");
        final boolean isVibrate = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isVibrate) {
                    Log.d("test","ready to vibrate");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    else vibrator.vibrate(new long[]{1000, 500,}, 0);
                }
            }
        }).start();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (vibrator.hasVibrator()){
//                    vibrator.cancel();
//                }
                vibrator.cancel();
                Log.d("test","vibrator stops");
            }
        });

    }
}
