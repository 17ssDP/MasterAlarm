package com.example.masteralarm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.masteralarm.R;

import java.util.Calendar;

public class AddLBSAlarmActivity extends AppCompatActivity {

    private TextView cancelAdd;
    private TextView add;
    private ImageView vibrateImage;
    private EditText name;
    private ImageView ringImage;
    private boolean isVibrate = false;
    private boolean isRing = false;
    private boolean isFirstLocate = true;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lbsalarm);

        cancelAdd = findViewById(R.id.lbs_cancel_alarm);
        add = findViewById(R.id.lbs_add_alarm);
        vibrateImage = findViewById(R.id.lbs_vibrateImage);
        ringImage = findViewById(R.id.lbs_ringtoneImage);
        name = findViewById(R.id.lbs_name);

        mMapView = (MapView) findViewById(R.id.mmap);
        mBaiduMap = mMapView.getMap();

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        requestLocation();
        setListener();
    }

    public void setListener(){
        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add button
            }
        });

        vibrateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVibrate = !isVibrate;
                vibrateImage.setImageResource(isVibrate?R.drawable.ic_vibrate:R.drawable.ic_vibrate_none);
                vibrateImage.animate().alpha(isVibrate ? 1 : 0.333f).setDuration(250).start();
            }
        });

        ringImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRing = !isRing;
                ringImage.setImageResource(isRing ? R.drawable.ic_ringtone : R.drawable.ic_ringtone_disabled);
                ringImage.animate().alpha(isRing ? 1 : 0.333f).setDuration(250).start();
            }
        });
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            mBaiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            mBaiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
//            StringBuilder currentPosition = new StringBuilder();
//            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
//            currentPosition.append("经线：").append(location.getLongitude()).append("\n");
//            currentPosition.append("国家：").append(location.getCountry()).append("\n");
//            currentPosition.append("省：").append(location.getProvince()).append("\n");
//            currentPosition.append("市：").append(location.getCity()).append("\n");
//            currentPosition.append("区：").append(location.getDistrict()).append("\n");
//            currentPosition.append("街道：").append(location.getStreet()).append("\n");
//            currentPosition.append("定位方式：");
//            if (location.getLocType() == BDLocation.TypeGpsLocation) {
//                currentPosition.append("GPS");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                currentPosition.append("网络");
//            }
//            positionText.setText(currentPosition);
            if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mMapView.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
    }
}
