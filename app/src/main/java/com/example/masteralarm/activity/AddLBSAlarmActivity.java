package com.example.masteralarm.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.masteralarm.R;
import com.example.masteralarm.data.LBSAlarmData;

import java.util.Calendar;

public class AddLBSAlarmActivity extends AppCompatActivity {
    public final int UPDATE_START = 1;
    public final int UPDATE_END = 2;

    private TextView cancelAdd;
    private TextView add;
    private TextView startAddress;
    private TextView endAddress;
    private ImageView vibrateImage;
    private EditText name;
    private ImageView ringImage;
    private boolean isVibrate = false;
    private boolean isRing = false;
    private boolean isFirstLocate = true;
    private String dataName;
    private String dataStart;
    private String dataEnd;
    private double latitude;
    private double longitude;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient;
    private GeoCoder geoCoder;
    private LBSAlarmData data;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_START:
                    dataStart = (String)msg.obj;
                    startAddress.setText((String)msg.obj);
                    break;
                case UPDATE_END:
                    dataEnd = (String)msg.obj;
                    endAddress.setText((String)msg.obj);
                    break;
                default:break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lbsalarm);

        cancelAdd = findViewById(R.id.lbs_cancel_alarm);
        add = findViewById(R.id.lbs_add_alarm);
        vibrateImage = findViewById(R.id.lbs_vibrateImage);
        ringImage = findViewById(R.id.lbs_ringtoneImage);
        name = findViewById(R.id.lbs_name);
        startAddress = findViewById(R.id.lbs_start_address);
        endAddress = findViewById(R.id.lbs_end_address);

        vibrateImage.setImageResource(isVibrate?R.drawable.ic_vibrate:R.drawable.ic_vibrate_none);
        ringImage.setImageResource(isRing ? R.drawable.ic_ringtone : R.drawable.ic_ringtone_disabled);

        mMapView = (MapView) findViewById(R.id.mmap);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMapClickListener(listener);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        geoCoder = GeoCoder.newInstance();
        geoCoder.setOnGetGeoCodeResultListener(resultListener);

        data = new LBSAlarmData();
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
                dataName = name.getText().toString();
                if (dataName.isEmpty() || dataStart == null || dataEnd == null){
                    Toast.makeText(AddLBSAlarmActivity.this,"数据未填写完整",Toast.LENGTH_SHORT).show();
                }
                else {
                    data.setIsEnable(true);
                    data.setIsRing(isRing);
                    data.setIsVibrate(isVibrate);
                    data.setName(dataName);
                    data.setStart(dataStart);
                    data.setEnd(dataEnd);
                    data.setLatitude(latitude);
                    data.setLongitude(longitude);

                    //保存数据
                    data.save();
                    Intent intent = new Intent();
                    intent.putExtra("New_Alarm", data);
                    setResult(RESULT_OK, intent);

                    finish();
                }
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
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setOpenGps(true);
        //防止偏移
        option.setCoorType("bd09ll");
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
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);

        //发送消息更新界面
        Message msg = new Message();
        msg.what = UPDATE_START;
        msg.obj = location.getAddrStr();
        handler.sendMessage(msg);
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
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

    BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
        /**
         * 地图单击事件回调函数
         * @param point 点击的地理坐标
         */
        public void onMapClick(LatLng point){
//            Log.d("test","click map");
            latitude = point.latitude;
            longitude = point.longitude;
            Log.d("click map","lat:lon    "+latitude+"  "+longitude);
            markPoint(point);
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(point));
        }
        /**
         * 地图内 Poi 单击事件回调函数
         * @param poi 点击的 poi 信息
         */
        public boolean onMapPoiClick(MapPoi poi){
            latitude = poi.getPosition().latitude;
            longitude = poi.getPosition().longitude;
            markPoint(poi.getPosition());
            geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(poi.getPosition()));
            return false;
        }
    };

    OnGetGeoCoderResultListener resultListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            //获取反向地理编码结果
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                return;
            }
            //获取反向地理编码结果
            Message msg = new Message();
            msg.what = UPDATE_END;
            msg.obj = result.getAddress();
            handler.sendMessage(msg);
        }
    };

    private void markPoint(LatLng point){
        mBaiduMap.clear();

        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_end);

        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);

        mBaiduMap.addOverlay(option);
    }
}
