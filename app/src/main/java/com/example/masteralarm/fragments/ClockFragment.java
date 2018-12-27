package com.example.masteralarm.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.masteralarm.R;
import com.example.masteralarm.views.DigitalClockView;

import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClockFragment extends BasePageFragment {
    private static String timezone = "GMT+:08:00";
    private DigitalClockView clockView;
    private TextView timezoneView;

    public ClockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clock, container, false);
        clockView = view.findViewById(R.id.timeView);
        timezoneView = view.findViewById(R.id.timezone);
        clockView.setTimezone(TimeZone.getTimeZone(timezone).getDisplayName());
        return view;
    }

    @Override
    public String getTitle() {
        return timezone;
    }
}
