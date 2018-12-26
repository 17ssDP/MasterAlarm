package com.example.masteralarm.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.example.masteralarm.MasterAlarm;

import java.util.Objects;

public abstract class BaseFragment extends Fragment {
    private MasterAlarm masterAlarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        masterAlarm = (MasterAlarm)Objects.requireNonNull(getContext()).getApplicationContext();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public MasterAlarm getMasterAlarm() {
        return masterAlarm;
    }
}
