package com.example.masteralarm.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.masteralarm.R;
import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.utils.FormatUtils;

import java.util.Calendar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static org.litepal.LitePalApplication.getContext;

public class AddAlarmActivity extends AppCompatActivity {
    private TextView setTime;
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
        setTime = findViewById(R.id.set_time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        setTime.setText(FormatUtils.formatShort(getApplicationContext(), calendar.getTime()));
        cancelAdd = (TextView)findViewById(R.id.cancel_alarm);
        add = (TextView) findViewById(R.id.add_alarm);
        vibrate = (Switch) findViewById(R.id.isVibrate);
        name = (EditText) findViewById(R.id.name);
        setListeners();
    }
    public void setListeners() {
        cancelAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        setTime.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar calendar = createTimePicker();
                Log.d("test",calendar.getTime().toString());
                setTime.setText(FormatUtils.formatShort(getApplicationContext(), calendar.getTime()));
            }
        });

        vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    private Calendar createTimePicker(){
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
            }
        }, hour, minute, true).show();
        return c;
    }

}
