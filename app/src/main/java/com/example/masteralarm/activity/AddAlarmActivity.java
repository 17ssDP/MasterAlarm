package com.example.masteralarm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.masteralarm.R;
import com.example.masteralarm.data.AlarmData;

import java.util.Calendar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AddAlarmActivity extends AppCompatActivity {
    private TextView cancelAdd;
    private TextView add;
    private Switch vibrate;
    private EditText name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alarm);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }
        cancelAdd = (TextView)findViewById(R.id.cancel_alarm);
        add = (TextView) findViewById(R.id.add_alarm);
        vibrate = findViewById(R.id.vibrate);
        name = findViewById(R.id.name);
        setListeners();
    }
    public void setListeners() {
        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmData alarmData = new AlarmData(1);
                alarmData.setEnable(true);
                alarmData.setLabel("Test Database");
                alarmData.setRepeat(new boolean[]{true, true, true, true, true, true, true});
                alarmData.setVibrate(true);
                alarmData.setCalendarTime(Calendar.getInstance());
                alarmData.save();
                Intent intent = new Intent();
                intent.putExtra("New_Alarm", alarmData);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
