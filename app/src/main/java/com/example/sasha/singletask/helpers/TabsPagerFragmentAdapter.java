package com.example.sasha.singletask.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.sasha.singletask.settings.CategoriesFragment;
import com.example.sasha.singletask.settings.TasksFragment;

public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {

    private final String[] tabs;

    public TabsPagerFragmentAdapter(FragmentManager fm) {
        super(fm);
        tabs = new String[] {"Задания", "Категории"};
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 1) {
            return TasksFragment.getInstance();
        } else {
            return CategoriesFragment.getInstance();
        }
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
