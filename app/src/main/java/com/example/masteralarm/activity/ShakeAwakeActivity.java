package com.example.masteralarm.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.masteralarm.R;
import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.services.AlarmService;
import com.example.masteralarm.views.CircleProgressView;

import androidx.appcompat.app.AppCompatActivity;

public class ShakeAwakeActivity  extends AppCompatActivity implements SensorEventListener {
    private final int THRESHOLD = 13;
    private final int CIRCLE_LINE_WIDTH = 10;
    private final int TEXT_SIZE = 7;
    private final int CIRCLE_RADIUS = 20;
    private SensorManager sensorManager;
    private PowerManager.WakeLock mWakeLock;
    private Canvas canvas;
    private int mTotalProgress = 100;
    private int mCurrentProgress = 0;
    private ImageView background;
    //进度条
    private CircleProgressView mTasksView;
    private CircleProgressView mCircleBar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shake_awake);
        //如果不是闹钟触发，自动关闭不让启动
        if (getIntent() == null){
            finish();
        }
        background = findViewById(R.id.background);
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
        canvas = new Canvas();
        //获得传感器管理器对象
        sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //得到默认的加速度传感器
        Boolean result = sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        Log.d("Register", result + "");
        //PowerManager.PARTIAL_WAKE_LOCK选项为CPU和所有硬件会一直工作
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ShakeAwakeActivity: My Tag");
        mWakeLock.acquire(10*60*1000L /*10 minutes*/);
        mTasksView = (CircleProgressView) findViewById(R.id.tasks_view);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("Test", "com/example/masteralarm/Sensor");
        //只要加速度传感器采集的加速度数据发生改变，就会执行onSensorChanged()的操作，正常情况下只要已注册使用传感器就会一直采数据。
        double accelerate = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
        //通过以上公式可以抛去三个方向上的重力加速度，只剩下纯加速度
        if(accelerate > THRESHOLD) {
            update();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        //只要加速度传感器采集的加速度数据精度发生改变，就会执行onAccuracyChanged()操作.
    }

    @Override
    protected void onDestroy() {
        sensorManager.unregisterListener(this);
        mWakeLock.release();
        binder.stopAlarm();
        unbindService(connection);
        super.onDestroy();
    }

    private void update() {
        if (mCurrentProgress < mTotalProgress) {
            new Thread(new ProgressRunable()).start();
        }else {
            finish();
        }

    }
    class ProgressRunable implements Runnable {
        @Override
        public void run() {
            if (mCurrentProgress < mTotalProgress) {
                mCurrentProgress += 2;
                mTasksView.setProgress(mCurrentProgress);
                try {
                    Thread.sleep(90);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
    }
}
