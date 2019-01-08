package com.example.masteralarm.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.example.masteralarm.R;
import com.example.masteralarm.activity.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BasePageFragment{

    private final static int REQUEST_CODE = 1;

    //界面元素
    private TextView tv_login;
    private TextView tv_vibrate;
    private Switch isUpdateBackground;
    private TextView tv_about;


    public SettingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        tv_login = view.findViewById(R.id.setting_login);
        tv_vibrate = view.findViewById(R.id.setting_vibrate_frequency);
        isUpdateBackground = view.findViewById(R.id.setting_isUpdate);
        tv_about = view.findViewById(R.id.setting_about);
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),LoginActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public String getTitle() {
        return "Setting";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE:
                tv_login.setText(data.getStringExtra("name"));
                break;
            default:break;
        }
    }
}
