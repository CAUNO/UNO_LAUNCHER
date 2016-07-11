package com.example.yena.unolauncher;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yena on 2016-07-05.
 */
public class AppListPagerAdapter extends FragmentStatePagerAdapter {

    private List<AppListFragment> fragments = new ArrayList<AppListFragment>();

    public AppListPagerAdapter(FragmentManager fm, List<AppListFragment> fragments){
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
