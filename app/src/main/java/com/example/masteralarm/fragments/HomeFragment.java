package com.example.masteralarm.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.example.masteralarm.R;
import com.example.masteralarm.adapters.SimplePagerAdapter;
import com.example.masteralarm.utils.ImageUtils;
import com.example.masteralarm.views.PageIndicatorView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import androidx.viewpager.widget.ViewPager;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;

public class HomeFragment extends BaseFragment {

    private FABsMenu menu;
    private TitleFAB stopwatchFab;
    private TitleFAB timerFab;
    private TitleFAB alarmFab;
    private ImageView background;
    private ViewPager viewPager;
    private ViewPager timePager;

    private SimplePagerAdapter pagerAdapter;
    private SimplePagerAdapter timeAdapter;
    private TabLayout tabLayout;
    private PageIndicatorView timeIndicator;

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

//        timeIndicator = view.findViewById(R.id.pageIndicator);
        timePager = view.findViewById(R.id.timePager);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        setPageFragments();
//        setClockFragments();
        ImageUtils.setBackgroundImage(background);
        return view;
    }

    private void setPageFragments() {
        pagerAdapter = new SimplePagerAdapter(getChildFragmentManager(), new RecyclerFragment(), new SettingFragment());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
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

    private void setClockFragments() {
        if (timePager != null && timeIndicator != null) {
            List<ClockFragment> fragments = new ArrayList<>();

            ClockFragment fragment = new ClockFragment();
            fragments.add(fragment);

            timeAdapter = new SimplePagerAdapter(getChildFragmentManager(), fragments.toArray(new ClockFragment[0]));
            timePager.setAdapter(timeAdapter);
            timeIndicator.setViewPager(timePager);
            timeIndicator.setVisibility(fragments.size() > 1 ? View.VISIBLE : View.GONE);
        }
    }

    private void setListeners(){

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
