package com.example.masteralarm.activity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.masteralarm.MasterAlarm;
import com.example.masteralarm.R;
import com.example.masteralarm.data.AlarmData;
import com.example.masteralarm.data.PreferenceData;
import com.example.masteralarm.utils.FormatUtils;

import java.util.Calendar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static org.litepal.LitePalApplication.getContext;

public class AddAlarmActivity extends AppCompatActivity {
    public static final int UPDATE_TIME = 1;
    private TextView setTime;
    private TextView cancelAdd;
    private TextView add;
    private CheckBox repeat;
    private ImageView vibrate;
    private EditText name;
    private Calendar calendar;
    private ImageView ringImage;
    private boolean isVibrate = false;
    private boolean isRing = false;
    private Switch commonAlarm;
    private Switch shakeAlarm;
    private Switch microAlarm;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_TIME:
                    calendar = (Calendar)msg.obj;
                    setTime.setText(FormatUtils.formatShort(getApplicationContext(), calendar.getTime()));
                    break;
                default:break;
            }
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alarm);
        setTime = findViewById(R.id.set_time);
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        setTime.setText(FormatUtils.formatShort(getApplicationContext(), calendar.getTime()));
        cancelAdd = (TextView)findViewById(R.id.cancel_alarm);
        add = (TextView) findViewById(R.id.add_alarm);
        repeat = findViewById(R.id.repeat);
        vibrate = (ImageView) findViewById(R.id.vibrateImage);
        name = (EditText) findViewById(R.id.name);
        ringImage = (ImageView)findViewById(R.id.ringtoneImage);
        commonAlarm = findViewById(R.id.common_alarm);
        shakeAlarm = findViewById(R.id.shake_alarm);
        microAlarm = findViewById(R.id.micro_alarm);
        commonAlarm.setChecked(true);
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
                alarmData.setLabel(name.getText().toString());
                alarmData.setRepeat(repeat.isChecked()?new boolean[]{true, true, true, true, true, true, true}:new boolean[]{false,false,false,false,false,false,false});
                alarmData.setVibrate(isVibrate);
                alarmData.setCalendarTime(calendar);
                alarmData.setHasSound(isRing);
                if(shakeAlarm.isChecked())
                    alarmData.setType(PreferenceData.SHAKE_ALARM);
                else if(microAlarm.isChecked())
                    alarmData.setType(PreferenceData.MICRO_ALARM);
                else
                    alarmData.setType(PreferenceData.COMMON_ALARM);

                //保存在数据库
                Log.d("Type", alarmData.getType() + "");
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
            }
        });

        vibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVibrate = !isVibrate;
                vibrate.setImageResource(isVibrate?R.drawable.ic_vibrate:R.drawable.ic_vibrate_none);
                vibrate.animate().alpha(isVibrate ? 1 : 0.333f).setDuration(250).start();
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
        commonAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                      shakeAlarm.setChecked(false);
                      microAlarm.setChecked(false);
                      Log.d("Commom Alarm", commonAlarm.isChecked() + "");
                }
            }
        });
        shakeAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    commonAlarm.setChecked(false);
                    microAlarm.setChecked(false);
                    Log.d("shake Alarm", shakeAlarm.isChecked() + "");
                }else {
                    commonAlarm.setChecked(true);
                    Log.d("shake Alarm", shakeAlarm.isChecked() + "");
                }
            }
        });
        microAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    commonAlarm.setChecked(false);
                    shakeAlarm.setChecked(false);
                    Log.d("micro Alarm", microAlarm.isChecked() + "");
                }else {
                    commonAlarm.setChecked(true);
                    Log.d("micro Alarm", microAlarm.isChecked() + "");
                }
            }
        });
    }

    public Calendar createTimePicker(){
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog pickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                c.setTimeInMillis(System.currentTimeMillis());
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                Message message = new Message();
                message.what = UPDATE_TIME;
                message.obj = c;
                handler.sendMessage(message);
            }
        }, hour, minute, true);
        pickerDialog.show();
        return c;
    }
}
