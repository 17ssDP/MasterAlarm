package com.example.masteralarm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.masteralarm.R;
import com.example.masteralarm.adapters.LBSAlarmAdapter;
import com.example.masteralarm.interfaces.LBSAlarmListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LBSAlarmFragment extends BasePageFragment implements LBSAlarmListener {

    private RecyclerView recyclerView;
    private View empty;
    private LBSAlarmAdapter alarmsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lbs_alarm, container, false);

        recyclerView = view.findViewById(R.id.lbs_recycler);
        empty = view.findViewById(R.id.lbs_empty);
        ((TextView) view.findViewById(R.id.lbs_emptyText)).setText(R.string.msg_alarms_empty);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        alarmsAdapter = new LBSAlarmAdapter(getMasterAlarm(),recyclerView,getFragmentManager());
        recyclerView.setAdapter(alarmsAdapter);

        getMasterAlarm().addLBSAlarmListener(this);

        onChanged();
        return view;
    }

    @Override
    public String getTitle() {
        return "LBS Alarm";
    }

    @Override
    public void onLBSAlarmChanged() {
        if (alarmsAdapter != null) {
            alarmsAdapter.notifyDataSetChanged();
            onChanged();
        }
    }

    private void onChanged() {
        if (empty != null && alarmsAdapter != null)
            empty.setVisibility(alarmsAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
