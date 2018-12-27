package com.example.masteralarm.adapters;

import com.example.masteralarm.fragments.BasePageFragment;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SimplePagerAdapter extends FragmentStatePagerAdapter {
    private BasePageFragment[] pageFragments;

    public SimplePagerAdapter(FragmentManager fm, BasePageFragment... fragments) {
        super(fm);
        this.pageFragments = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        return pageFragments[position];
    }

    @Override
    public int getCount() {
        return pageFragments.length;
    }
}
