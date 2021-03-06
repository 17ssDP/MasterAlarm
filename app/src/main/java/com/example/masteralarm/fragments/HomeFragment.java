package com.example.masteralarm.fragments;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.example.masteralarm.R;
import com.example.masteralarm.activity.AddLBSAlarmActivity;
import com.example.masteralarm.activity.CommonAwakeActivity;
import com.example.masteralarm.activity.AddAlarmActivity;
import com.example.masteralarm.activity.MicroAwakeActivity;
import com.example.masteralarm.adapters.SimplePagerAdapter;
import com.example.masteralarm.data.LBSAlarmData;
import com.example.masteralarm.utils.AlarmManagerUtil;
import com.example.masteralarm.utils.FormatUtils;
import com.example.masteralarm.utils.HttpUtil;
import com.example.masteralarm.views.PageIndicatorView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;
import androidx.viewpager.widget.ViewPager;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.example.masteralarm.data.AlarmData;
import static android.app.Activity.RESULT_OK;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static java.lang.Thread.sleep;

public class HomeFragment extends BaseFragment {

    public static final int UPDATE_CLOCK = 1;
    public static final int RESULT_ADDLBSALARM = 10;
    public static final int UPDATE_BACKGROUND = 2;

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
    private String bingPic;

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
                case UPDATE_BACKGROUND:
                    Glide.with(Objects.requireNonNull(getContext())).load(bingPic).into(background);
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

        sheet = view.findViewById(R.id.bottomSheet);
        behavior = BottomSheetBehavior.from(sheet);

        menu = view.findViewById(R.id.fabsMenu);
        stopwatchFab = view.findViewById(R.id.stopwatchFab);
        timerFab = view.findViewById(R.id.timerFab);
        alarmFab = view.findViewById(R.id.alarmFab);
        timePager = view.findViewById(R.id.timePager);
        timeIndicator = view.findViewById(R.id.pageIndicator);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        background = view.findViewById(R.id.background);
        clock = view.findViewById(R.id.app_clock);

        setListeners();
        setPageFragments();
        updateClock();

        //加载背景图片
        SharedPreferences prefs = getDefaultSharedPreferences(getContext());
        String bingPic = prefs.getString("bing_pic", null);
        if(bingPic != null) {
            Glide.with(this).load(bingPic).into(background);
        } else {
            loadBingPic();
        }
        //开启前台服务
        getMasterAlarm().startForeground(view.getContext());
        return view;
    }

    private void setPageFragments() {
        pagerAdapter = new SimplePagerAdapter(getChildFragmentManager(), new RecyclerFragment(), new LBSAlarmFragment(), new SettingFragment());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++){
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            switch (i){
                case 0:
                    tab.setText(R.string.tab_alarm);
                    break;
                case 1:
                    tab.setText("LBSAlarm");
                    break;
                case 2:
                    tab.setText(R.string.tab_setting);
                    break;
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 2){
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
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
                Intent intent = new Intent(getContext(),AddAlarmActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        //use to debug
        timerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent("com.example.masteralarm.MY_BROADCAST");
//                intent.setComponent(new ComponentName("com.example.masteralarm","com.example.masteralarm.receivers.AlarmBroadcastReceiver"));
//                getContext().sendBroadcast(intent);
                Intent intent = new Intent(getContext(),MicroAwakeActivity.class);
                getContext().startActivity(intent);
            }
        });
        stopwatchFab.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),AddLBSAlarmActivity.class);
                startActivityForResult(intent,RESULT_ADDLBSALARM);
//                LBSAlarmData data = new LBSAlarmData();
//                data.setIsEnable(true);
//                data.setIsRing(true);
//                data.setIsVibrate(false);
//                data.setName("Test");
//                data.setStart("Home");
//                data.setEnd("School");
//                data.setLatitude(132.0336);
//                data.setLongitude(20.56);
//                data.save();
//                getMasterAlarm().addLBSAlarm(data);


//                Calendar choose = Calendar.getInstance();
//                Calendar cur = Calendar.getInstance();
//                choose.add(13,10);
//                //如果时间更小，加上一天
////                if (cur.after(choose)){
////                    choose.add(5,1);
////                }
//                AlarmData alarmData = new AlarmData(1);
//                alarmData.setEnable(true);
//                alarmData.setLabel("Test Database");
//                alarmData.setRepeat(new boolean[]{false,false,false,false,false,false,false});
//                alarmData.setVibrate(false);
//                alarmData.setCalendarTime(choose);
//                alarmData.setTone(getSystemDefultRingtoneUri());
//                alarmData.setHasSound(true);
//
//                AlarmManagerUtil.setAlarm(getMasterAlarm(),alarmData);
//                Log.d("test","set complete");
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
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK) {
                    AlarmData alarmData = (AlarmData)data.getSerializableExtra("New_Alarm");
                    getMasterAlarm().addAlarm(alarmData);
                    Log.d("AddAlarm: ", alarmData.getLabel());
                }
                break;
            case RESULT_ADDLBSALARM:
                if (resultCode == RESULT_OK) {
                    LBSAlarmData lbsAlarmData = (LBSAlarmData) data.getSerializableExtra("New_Alarm");
                    Log.d("home fragment","receive result");
                    getMasterAlarm().addLBSAlarm(lbsAlarmData);
                }
                break;
            default:
        }
    }

    /* 加载每日Bing一图*/
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(HomeFragment.this.getContext()).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = UPDATE_BACKGROUND;
                        handler.sendMessage(message);
                    }
                }).start();
            }
        });
    }
}
