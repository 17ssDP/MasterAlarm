package com.example.masteralarm.fragments;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.example.masteralarm.R;
import com.example.masteralarm.adapters.RecyclerAdapter;
import com.example.masteralarm.interfaces.AlarmListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class RecyclerFragment extends BasePageFragment implements AlarmListener {
    private RecyclerView recyclerView;
    private View empty;
    private RecyclerAdapter alarmsAdapter;

    //颜色属性
    private Disposable colorAccentSubscription;
    private Disposable colorForegroundSubscription;
    private Disposable textColorPrimarySubscription;


    public RecyclerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        empty = view.findViewById(R.id.empty);
        ((TextView) view.findViewById(R.id.emptyText)).setText(R.string.msg_alarms_empty);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        alarmsAdapter = new RecyclerAdapter(getMasterAlarm(),recyclerView,getFragmentManager());
        recyclerView.setAdapter(alarmsAdapter);

        getMasterAlarm().addListener(this);

        onChanged();

        initialSetting();
        return view;
    }

    @Override
    public void onAlarmChanged() {
        //当增加或者删除闹钟时，更新界面的显示内容
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
    public String getTitle() {
        return "Alarm";
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

    @Override
    public void onDestroyView() {
        colorAccentSubscription.dispose();
        colorForegroundSubscription.dispose();
        textColorPrimarySubscription.dispose();
        super.onDestroyView();
    }
}
