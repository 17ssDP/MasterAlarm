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
import com.example.masteralarm.data.LBSAlarmData;
import com.example.masteralarm.data.PreferenceData;
import com.example.masteralarm.services.AlarmService;
import com.example.masteralarm.utils.AlarmManagerUtil;

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
    private LBSAlarmData lbsAlarmData;
    private int type;
    private boolean isRing;
    private boolean isVibrate;
    private String path;
    private int delay = PreferenceData.ALARM_DELAY;//延迟5分钟

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

        background = findViewById(R.id.background);
        application = (MasterAlarm)getApplication();
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        actionView = findViewById(R.id.slideView);

        //如果不是闹钟触发，自动关闭不让启动
        if (getIntent() == null){
            finish();
        }

        Intent intentpass = getIntent();
        type = intentpass.getIntExtra("type",0);

        //根据类型设置参数
        if (type == PreferenceData.COMMON_ALARM){
            data = (AlarmData)intentpass.getSerializableExtra("alarmdata");
            if (data == null||!data.isEnable()){
                finish();
            }
            date.setText(data.getLabel());
            isRing = data.isHasSound();
            isVibrate = data.isVibrate();
            path = getSystemDefultRingtoneUri().getPath();
        }
        else if (type == PreferenceData.LBS_ALARM){
            lbsAlarmData = (LBSAlarmData)intentpass.getSerializableExtra("alarmdata");
            if (lbsAlarmData == null){
                finish();
            }
            date.setText(lbsAlarmData.getName());
            isRing =lbsAlarmData.getIsRing();
            isVibrate = lbsAlarmData.getIsVibrate();
            path = getSystemDefultRingtoneUri().getPath();
        }

        //加载背景图片
        Glide.with(this).load(R.mipmap.alarm_background).into(background);

        //绑定服务
        Intent intent = new Intent(this,AlarmService.class);

        intent.putExtra("isRing",isRing);
        intent.putExtra("isVibrate",isVibrate);
        intent.putExtra("ringPath",path);

        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);

        //滑动组件左图标
        actionView.setLeftIcon(VectorDrawableCompat.create(getResources(), R.drawable.ic_snooze, getTheme()));
        //滑动组件右图标
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
        if (type == PreferenceData.LBS_ALARM){
            binder.stopAlarm();
            thread.interrupt();
            finish();
        }
        else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(12,delay);//延迟5分钟
            AlarmData alarmData = new AlarmData(data.getId()+100);
            alarmData.setEnable(true);
            alarmData.setHasSound(isRing);
            alarmData.setVibrate(isVibrate);
            alarmData.setLabel(data.getLabel());
            alarmData.setRepeat(new boolean[]{false,false,false,false,false,false,false});
            alarmData.setCalendarTime(calendar);
            AlarmManagerUtil.setAlarm(application,alarmData);
            binder.stopAlarm();
            thread.interrupt();
            finish();
        }
    }

    @Override
    public void onSlideRight() {

        binder.stopAlarm();
        thread.interrupt();
        finish();
    }

    private void updateTime(){
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
