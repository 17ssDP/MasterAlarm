package com.example.masteralarm.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import me.jfenn.slideactionview.SlideActionView;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.masteralarm.MasterAlarm;
import com.example.masteralarm.R;
import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.services.AlarmService;

import java.util.Calendar;

import static java.lang.Thread.sleep;

public class CommonAwakeActivity extends AppCompatActivity implements SlideActionView.SlideActionListener{
    private static final int UPDATE_TIME = 1;

    private ImageView background;
    private MasterAlarm application;
    private TextView date;//唤醒日期
    private TextView time;//倒计时
    private SlideActionView actionView;//下方滑动组件
    private Thread thread;
    private AlarmData data;

    //连接闹钟服务
    private AlarmService.AlarmBinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (AlarmService.AlarmBinder)iBinder;
            //开始闹钟
            binder.startAlarm();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            final Calendar calendar= Calendar.getInstance();
            switch (msg.what){
                case UPDATE_TIME:
                    String str = "- "+msg.arg1 + " S";
                    time.setText(str);
                    break;
                default:;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_awake);

        //如果不是闹钟触发，自动关闭不让启动
        if (getIntent() == null){
            finish();
        }
        background = findViewById(R.id.background);
        application = (MasterAlarm)getApplication();
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        actionView = findViewById(R.id.slideView);
        data = (AlarmData)getIntent().getSerializableExtra("alarmdata");

        //加载背景图片
        Glide.with(this).load(R.mipmap.alarm_background).into(background);

        //绑定服务
        Intent intent = new Intent(this,AlarmService.class);
        if (data == null){
            intent.putExtra("isRing",true);
            intent.putExtra("isVibrate",true);
            intent.putExtra("ringPath",getSystemDefultRingtoneUri().getPath());
        }
        else {
            intent.putExtra("isRing",data.isHasSound());
            intent.putExtra("isVibrate",data.isVibrate());
            intent.putExtra("ringPath",getSystemDefultRingtoneUri().getPath());
        }
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);

        //滑动组件左字符
        actionView.setLeftIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_snooze, getTheme()));
        //滑动组件右字符
        actionView.setRightIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_close, getTheme()));
        actionView.setListener(this);

        updateTime();
    }

    @Override
    protected void onDestroy() {
        //关闭子线程
        if (thread.isAlive()){
            thread.interrupt();
        }
        unbindService(connection);
        super.onDestroy();
    }

    @Override
    public void onSlideLeft() {

        //延迟一定时间再次响铃
    }

    @Override
    public void onSlideRight() {
        binder.stopAlarm();
        thread.interrupt();
        finish();
    }

    private void updateTime(){
//        date.setText(data.getLabel());
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int time = 0;//开始计时
                while (true) {
                    Message msg = new Message();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    long cur = System.currentTimeMillis();
                    msg.arg1 = time++;
                    msg.what = UPDATE_TIME;
                    handler.sendMessage(msg);
                }
            }
        });
        thread.start();
    }

    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
    }
}
