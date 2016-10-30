package com.example.sasha.singletask.helpers;

import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.example.sasha.singletask.settings.CategoriesFragment;
import com.example.sasha.singletask.settings.TasksFragment;

public class TabsPagerFragmentAdapter extends FragmentPagerAdapter {

    private final String[] tabs;

    private ArrayList<Map> catItems = new ArrayList<Map>();
    private ArrayList<Map> taskItems = new ArrayList<Map>();

    public TabsPagerFragmentAdapter(FragmentManager fm, ArrayList<Map> mCatItems, ArrayList<Map> mTaskItems) {
        super(fm);
        tabs = new String[] {"Задания", "Категории"};
        catItems = mCatItems;
        taskItems = mTaskItems;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        System.out.println("JJJJJJJJJJJJ" + catItems.toString());
        System.out.println("JJJJJJJJJJJJ" + taskItems.toString());
        if (position == 1) {
            return TasksFragment.getInstance(taskItems);
//            return CategoriesFragment.getInstance();
        } else {
            return CategoriesFragment.getInstance(catItems);
        }
//        return CategoriesFragment.getInstance();
    }

    @Override
    public int getCount() {
        return tabs.length;
    }
}
