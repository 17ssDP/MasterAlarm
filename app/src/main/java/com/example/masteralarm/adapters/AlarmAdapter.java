package com.example.masteralarm.adapters;

import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.masteralarm.R;
import com.example.masteralarm.data.AlarmData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class AlarmAdapter extends RecyclerView.Adapter{
    private RecyclerView recycler;
    private SharedPreferences prefs;
    private AlarmManager alarmManager;
    private FragmentManager fragmentManager;
    private List<AlarmData> alarms;
    private int colorAccent = Color.WHITE;
    private int colorForeground = Color.TRANSPARENT;
    private int textColorPrimary = Color.WHITE;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
        AlarmViewHolder alarmViewHolder = new AlarmViewHolder(view);
        return alarmViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AlarmViewHolder alarmHolder = (AlarmViewHolder) holder;

        alarmHolder.repeat.setTextColor(textColorPrimary);
        alarmHolder.delete.setTextColor(textColorPrimary);
        alarmHolder.ringtoneImage.setColorFilter(textColorPrimary);
        alarmHolder.vibrateImage.setColorFilter(textColorPrimary);
        alarmHolder.expandImage.setColorFilter(textColorPrimary);
        alarmHolder.repeatIndicator.setColorFilter(textColorPrimary);
        alarmHolder.soundIndicator.setColorFilter(textColorPrimary);
        alarmHolder.vibrateIndicator.setColorFilter(textColorPrimary);
        alarmHolder.nameUnderline.setBackgroundColor(textColorPrimary);
    }

    @Override
    public int getItemCount() {

        return 0;
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        private View nameContainer;
        private EditText name;
        private View nameUnderline;
        private SwitchCompat enable;
        private TextView time;
        private View option;
        private AppCompatCheckBox repeat;
        private LinearLayout days;
        private View ringtone;
        private ImageView ringtoneImage;
        private TextView ringtoneText;
        private View vibrate;
        private ImageView vibrateImage;
        private ImageView expandImage;
        private TextView delete;
        private View indicators;
        private ImageView repeatIndicator;
        private ImageView soundIndicator;
        private ImageView vibrateIndicator;

        public AlarmViewHolder(View v) {
            super(v);
            nameContainer = v.findViewById(R.id.nameContainer);
            name = v.findViewById(R.id.name);
            nameUnderline = v.findViewById(R.id.underline);
            enable = v.findViewById(R.id.enable);
            time = v.findViewById(R.id.time);
            option = v.findViewById(R.id.option);
            repeat = v.findViewById(R.id.repeat);
            days = v.findViewById(R.id.days);
            ringtone = v.findViewById(R.id.ringtone);
            ringtoneImage = v.findViewById(R.id.ringtoneImage);
            ringtoneText = v.findViewById(R.id.ringtoneText);
            vibrate = v.findViewById(R.id.vibrate);
            vibrateImage = v.findViewById(R.id.vibrateImage);
            expandImage = v.findViewById(R.id.expandImage);
            delete = v.findViewById(R.id.delete);
            indicators = v.findViewById(R.id.indicators);
            repeatIndicator = v.findViewById(R.id.repeatIndicator);
            soundIndicator = v.findViewById(R.id.soundIndicator);
            vibrateIndicator = v.findViewById(R.id.vibrateIndicator);
        }
    }
}
