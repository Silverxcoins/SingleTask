package com.example.sasha.singletask.helpers;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.example.sasha.singletask.settings.CategoriesFragment;
import com.example.sasha.singletask.settings.TasksFragment;

import java.util.ArrayList;
import java.util.Map;

public class TabsPagerFragmentAdapter extends FragmentStatePagerAdapter {

    private final String[] tabs;
    private ArrayList<Map> categoryItems = new ArrayList<>();
    private ArrayList<Map> taskItems = new ArrayList<>();

    public TabsPagerFragmentAdapter(FragmentManager fm, ArrayList<Map> categories, ArrayList<Map> tasks) {
        super(fm);
        tabs = new String[] {"Задания", "Категории"};
        taskItems = tasks;
        categoryItems = categories;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        // TODO FIX: swap positions (Tasks must be first)
        // tabs = new String[] {"Задания", "Категории"};
        if (position == 0) {
            return CategoriesFragment.getInstance(categoryItems);
        } else {
            return TasksFragment.getInstance(taskItems);
        }
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }
}
