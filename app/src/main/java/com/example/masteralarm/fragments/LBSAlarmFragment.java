package com.example.masteralarm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.example.masteralarm.R;
import com.example.masteralarm.adapters.LBSAlarmAdapter;
import com.example.masteralarm.interfaces.LBSAlarmListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class LBSAlarmFragment extends BasePageFragment implements LBSAlarmListener {

    private RecyclerView recyclerView;
    private View empty;
    private LBSAlarmAdapter alarmsAdapter;

    //颜色属性
    private Disposable colorAccentSubscription;
    private Disposable colorForegroundSubscription;
    private Disposable textColorPrimarySubscription;

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

        initialSetting();

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
        colorAccentSubscription.dispose();
        colorForegroundSubscription.dispose();
        textColorPrimarySubscription.dispose();
        super.onDestroyView();
    }

    private void initialSetting(){
        colorAccentSubscription = Aesthetic.Companion.get()
                .colorAccent()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        alarmsAdapter.setColorAccent(integer);
                    }
                });

        colorForegroundSubscription = Aesthetic.Companion.get()
                .colorCardViewBackground()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        alarmsAdapter.setColorForeground(integer);
                    }
                });

        textColorPrimarySubscription = Aesthetic.Companion.get()
                .textColorPrimary()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        alarmsAdapter.setTextColorPrimary(integer);
                    }
                });
    }
}
