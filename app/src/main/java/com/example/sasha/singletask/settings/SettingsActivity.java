package com.example.sasha.singletask.settings;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.helpers.TabsPagerFragmentAdapter;

public class SettingsActivity extends AppCompatActivity {
    private ViewPager pager;
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();
        initTabs();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings_tag);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initTabs() {
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TabsPagerFragmentAdapter(getSupportFragmentManager()));
        tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(pager);
    }
}
