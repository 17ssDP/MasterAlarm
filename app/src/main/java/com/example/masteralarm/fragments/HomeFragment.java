package com.example.masteralarm.fragments;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.example.masteralarm.R;
import com.example.masteralarm.activity.CommonAwakeActivity;
import com.example.masteralarm.adapters.SimplePagerAdapter;
import com.example.masteralarm.data.PreferenceData;
import com.example.masteralarm.utils.AlarmManagerUtil;
import com.example.masteralarm.utils.FormatUtils;
import com.example.masteralarm.views.PageIndicatorView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import androidx.viewpager.widget.ViewPager;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;

import com.example.masteralarm.data.AlarmData;

import static java.lang.Thread.sleep;

public class HomeFragment extends BaseFragment {

    public static final int UPDATE_CLOCK = 1;

    private FABsMenu menu;
    private TitleFAB stopwatchFab;
    private TitleFAB timerFab;
    private TitleFAB alarmFab;
    private ImageView background;
    private ViewPager viewPager;
    private ViewPager timePager;
    private TextView clock;

    private View sheet;
    private BottomSheetBehavior behavior;

    private SimplePagerAdapter pagerAdapter;
    private SimplePagerAdapter timeAdapter;
    private TabLayout tabLayout;
    private PageIndicatorView timeIndicator;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @SuppressLint("DefaultLocale")
        public void handleMessage(Message msg){
            final Calendar calendar= Calendar.getInstance();
            switch (msg.what){
                case UPDATE_CLOCK:
                    String text;
                    text = FormatUtils.format(getMasterAlarm(),calendar.getTime());
                    clock.setText(text);
                    break;
                default:break;
            }
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        menu = view.findViewById(R.id.fabsMenu);
        stopwatchFab = view.findViewById(R.id.stopwatchFab);
        timerFab = view.findViewById(R.id.timerFab);
        alarmFab = view.findViewById(R.id.alarmFab);

        timeIndicator = view.findViewById(R.id.pageIndicator);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        background = view.findViewById(R.id.background);
        clock = view.findViewById(R.id.app_clock);

        setListeners();
        setPageFragments();
        updateClock();
        Glide.with(this).load(R.mipmap.background2).into(background);
        return view;
    }

    private void setPageFragments() {
        pagerAdapter = new SimplePagerAdapter(getChildFragmentManager(), new RecyclerFragment(), new SettingFragment());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText(R.string.tab_alarm);
        tabLayout.getTabAt(1).setText(R.string.tab_setting);
        for (int i = 0; i < tabLayout.getTabCount(); i++){

        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setListeners(){
        alarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SQLiteDatabase database = LitePal.getDatabase();
                AlarmData alarmData = new AlarmData(1);
                alarmData.setEnable(true);
                alarmData.setLabel("Test Database");
                alarmData.setRepeat(new boolean[]{true, true, true, true, true, true, true});
                alarmData.setVibrate(true);
                alarmData.setTime(Calendar.getInstance());
//                alarmData.save();
//                //读取数据库
//                List<AlarmData> alarms = LitePal.findAll(AlarmData.class);
//                Log.d("Read From Database: ", alarms.get(0).getLabel());
//                Log.d("test", "Test click");
                getMasterAlarm().addAlarm(alarmData);
            }
        });

        //use to debug
        timerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("com.example.masteralarm.MY_BROADCAST");
//                intent.setComponent(new ComponentName("com.example.masteralarm","com.example.masteralarm.receivers.AlarmBroadcastReceiver"));
//                getContext().sendBroadcast(intent);
                Intent intent = new Intent(getContext(),CommonAwakeActivity.class);
                getContext().startActivity(intent);
            }
        });
        stopwatchFab.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View view) {
                Calendar choose = Calendar.getInstance();
                Calendar cur = Calendar.getInstance();
                choose.add(13,10);
                //如果时间更小，加上一天
//                if (cur.after(choose)){
//                    choose.add(5,1);
//                }
                AlarmData alarmData = new AlarmData(1);
                alarmData.setEnable(true);
                alarmData.setLabel("Test Database");
                alarmData.setRepeat(new boolean[]{false,false,false,false,false,false,false});
                alarmData.setVibrate(true);
                alarmData.setTime(choose);
                alarmData.setTone(getSystemDefultRingtoneUri());
                alarmData.setHasSound(true);

                AlarmManagerUtil.setAlarm(getMasterAlarm(),alarmData);
                Log.d("test","set complete");
            }
        });
    }

    private Calendar createTimePicker(){
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateClock(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message msg = new Message();
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    msg.what = UPDATE_CLOCK;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_RINGTONE);
    }
}
