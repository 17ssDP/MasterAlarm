package com.example.masteralarm.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.example.masteralarm.activity.AlarmActivity;
import com.example.masteralarm.activity.CommonAwakeActivity;
import com.example.masteralarm.activity.MainActivity;
import com.example.masteralarm.activity.MicroAwakeActivity;
import com.example.masteralarm.activity.ShakeAwakeActivity;
import com.example.masteralarm.data.PreferenceData;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intentpass) {
        Log.d("test","receive the message");
      //  Toast.makeText(context,"receive message",Toast.LENGTH_SHORT).show();
        Intent intent;
        Log.d("After intent:", "" + intentpass.getIntExtra("type", 0));
        switch (intentpass.getIntExtra("type", 0)) {
            case PreferenceData.COMMON_ALARM :
                intent = new Intent(context,CommonAwakeActivity.class);
                break;
            case PreferenceData.LBS_ALARM:
                intent = new Intent(context, CommonAwakeActivity.class);
                break;
            case PreferenceData.MICRO_ALARM:
                intent = new Intent(context, MicroAwakeActivity.class);
                break;
            case PreferenceData.SHAKE_ALARM:
                intent = new Intent(context, ShakeAwakeActivity.class);
                break;
            default:
                intent = new Intent(context, CommonAwakeActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("alarmdata",intentpass.getSerializableExtra("alarmdata"));
        intent.putExtra("type", intentpass.getIntExtra("type", 0));
        context.startActivity(intent);
    }
}
