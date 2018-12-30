package com.example.masteralarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.masteralarm.activity.AlarmActivity;
import com.example.masteralarm.activity.MainActivity;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intentpass) {
        Log.d("test","receive the message");
        Intent intent = new Intent(context,AlarmActivity.class);
        intent.putExtra("alarmdata",intentpass.getParcelableExtra("alarmdata"));
        context.startActivity(intent);
    }
}
