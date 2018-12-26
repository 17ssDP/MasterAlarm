package com.example.masteralarm.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.masteralarm.MasterAlarm;
import com.example.masteralarm.R;
import com.example.masteralarm.fragments.BaseFragment;
import com.example.masteralarm.fragments.HomeFragment;
import com.example.masteralarm.fragments.SplashFragment;

public class MainActivity extends AppCompatActivity {

    private MasterAlarm masterAlarm;
    private BaseFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        masterAlarm = (MasterAlarm)getApplicationContext();
        if (savedInstanceState == null){
            Log.d("test","slash fragment start");
            fragment = new SplashFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment,fragment).commit();
        }
        else {
            fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment,fragment).commit();
        }
    }
}
