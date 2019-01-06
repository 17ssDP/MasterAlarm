package com.example.masteralarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.masteralarm.activity.AlarmActivity;
import com.example.masteralarm.activity.CommonAwakeActivity;
import com.example.masteralarm.activity.MainActivity;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intentpass) {
        Log.d("test","receive the message");
      //  Toast.makeText(context,"receive message",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context,CommonAwakeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("alarmdata",intentpass.getSerializableExtra("alarmdata"));
        context.startActivity(intent);
    }
}
