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
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.example.masteralarm.MasterAlarm;
import com.example.masteralarm.R;
import com.example.masteralarm.activity.CommonAwakeActivity;
import com.example.masteralarm.activity.MainActivity;
import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.data.LBSAlarmData;
import com.example.masteralarm.data.PreferenceData;
import com.example.masteralarm.interfaces.AlarmListener;
import com.example.masteralarm.interfaces.LBSAlarmListener;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class ForegroundService extends Service implements LBSAlarmListener,AlarmListener {
    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = "com.example.masteralarm.foregroundservice";
    public static final String NOTIFICATION_CHANNEL_ID_TASK = "com.example.masteralarm";
    private static final int SERVICE_ID = 100;
    private static final String NOTIFICATION_CHANNEL_ID_INFO = "com.example.masteralarm";
    private String CHANNEL_ID = "ALARM FOREGROUND SERVICE";

    //判断是否为近邻点的参数
    public static final double GAP_LATITUDE = 0.008;
    public static final double GAP_LONGITUDE = 0.006;

    private List<LBSAlarmData> dataList;
    private List<AlarmData> alarmDataList;
    private MasterAlarm application;
    private LocationClient mLocationClient;
    public BDAbstractLocationListener myListener;
    NotificationManager mNotificationManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        application = (MasterAlarm)getApplication();
        dataList = application.getLbsAlarmData();
        alarmDataList = application.getAlarmData();


        application.addLBSAlarmListener(this);
        application.addListener(this);
        onLBSAlarmChanged();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            initChannel();
            notification = new Notification.Builder(this)
                    .setContentTitle("Alarm")
                    .setContentText(alarmDataList.size() + " Clocks ready")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(Calendar.getInstance().getTimeInMillis())
                    .setChannelId(NOTIFICATION_CHANNEL_ID_SERVICE)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent).build();
        }
        else{
            notification = new Notification.Builder(this)
                    .setContentTitle("Alarm")
                    .setContentText(alarmDataList.size()+" Clocks ready")
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

    @Override
    public void onLBSAlarmChanged() {
        if (dataList.isEmpty()){
            if (mLocationClient != null && mLocationClient.isStarted()){
                mLocationClient.start();
            }
        }
        else {
            initLocationClient();
        }
    }

    private void checkLocation(double lat, double lon){
        for (LBSAlarmData data:dataList){
            if (data.getIsEnable()){
                if (isNear(lat,lon,data.getLatitude(),data.getLongitude())){
                    data.setIsEnable(false);
                    data.updateAll("id = ?",""+data.getId());
//                    Log.d("foreground service","we get there");
//                    Toast.makeText(application,"Get position",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(application,CommonAwakeActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("type",PreferenceData.LBS_ALARM);
                    intent.putExtra("alarmdata",data);
                    startActivity(intent);
                }
            }
        }
    }

    private boolean isNear(double srcLat, double srcLon, double dstLat, double dstLon){
        double gapLat = Math.abs(srcLat - dstLat);
        double gapLon = Math.abs(srcLon - dstLon);

        return gapLat < GAP_LATITUDE && gapLon < GAP_LONGITUDE;
    }

    @Override
    public void onAlarmChanged() {
        setNotification();
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
//            Log.d("foreground service","("+bdLocation.getLatitude()+","+bdLocation.getLongitude()+")");
            double lat = bdLocation.getLatitude();
            double lon = bdLocation.getLongitude();
            checkLocation(lat,lon);
        }

    }

    private void initLocationClient(){

        myListener = new MyLocationListener();

        mLocationClient = new LocationClient(getApplicationContext());

        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option=new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        int span = 1000;
        option.setScanSpan(span);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要。设置为true后，可以再listener中通过getCountry()、getProvice()、getCity()等方法得到具体的地区街道信息

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }


    private void setNotification(){
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            initChannel();
            notification = new Notification.Builder(this)
                    .setContentTitle("Alarm")
                    .setContentText(alarmDataList.size() + " Clocks ready")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(Calendar.getInstance().getTimeInMillis())
                    .setChannelId(NOTIFICATION_CHANNEL_ID_SERVICE)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent).build();
        }
        else{
            notification = new Notification.Builder(this)
                    .setContentTitle("Alarm")
                    .setContentText(alarmDataList.size()+" Clocks ready")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(Calendar.getInstance().getTimeInMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent).build();
        }
        mNotificationManager.notify(SERVICE_ID,notification);
    }
}
