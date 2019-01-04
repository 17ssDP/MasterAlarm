package com.example.masteralarm.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.masteralarm.MasterAlarm;
import com.example.masteralarm.R;
import com.example.masteralarm.data.LBSAlarmData;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

public class LBSAlarmAdapter extends RecyclerView.Adapter {
    List<LBSAlarmData> lbsAlarmData;
    private MasterAlarm application;
    private RecyclerView recycler;
    private FragmentManager fragmentManager;
    private int colorForeground = Color.TRANSPARENT;
    private int textColorPrimary = Color.WHITE;
    private int colorAccent = Color.WHITE;

    public LBSAlarmAdapter(MasterAlarm alarm, RecyclerView recycler, FragmentManager manager){
        application = alarm;
        this.recycler = recycler;
        lbsAlarmData = application.getLbsAlarmData();
        fragmentManager = manager;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private Switch enable;
        private TextView label;
        private ImageView ringtoneImage;
        private ImageView vibrateImage;
        private TextView startPoint;
        private TextView endPoint;
        private TextView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            enable = itemView.findViewById(R.id.lbs_enable);
            label = itemView.findViewById(R.id.lbs_name);
            ringtoneImage = itemView.findViewById(R.id.lbs_soundIndicator);
            vibrateImage = itemView.findViewById(R.id.lbs_vibrateIndicator);
            startPoint = itemView.findViewById(R.id.lbs_start_point);
            endPoint = itemView.findViewById(R.id.lbs_end_point);
            delete = itemView.findViewById(R.id.lbs_delete);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lbs_alarm,parent,false);
        RecyclerAdapter.ViewHolder holder = new RecyclerAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
        final LBSAlarmAdapter.ViewHolder holder = (ViewHolder)h;

        final LBSAlarmData data = lbsAlarmData.get(position);
        holder.label.setText(data.getName());
        holder.label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //进入地点选择界面
            }
        });
        holder.startPoint.setText(data.getStart());
        holder.endPoint.setText(data.getEnd());
        holder.enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                data.setIsEnable(b);
            }
        });
        holder.vibrateImage.setImageResource(data.getIsVibrate()?R.drawable.ic_vibrate:R.drawable.ic_vibrate_none);
        holder.vibrateImage.setColorFilter(textColorPrimary);
        holder.ringtoneImage.setImageResource(data.getIsRing()?R.drawable.ic_ringtone:R.drawable.ic_ringtone_disabled);
        holder.ringtoneImage.setColorFilter(textColorPrimary);
    }

    @Override
    public int getItemCount() {
        return lbsAlarmData.size();
    }


}
