package com.example.sasha.singletask.settings;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.helpers.TabsPagerFragmentAdapter;

public class SettingsActivity extends AppCompatActivity  {

    private static final String TAG = "SettingsActivity";
    // показ и прокрутка страниц (swipe)
    private ViewPager pager;
    // вкладки
    private TabLayout tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "FLOAT BUTTON CLIKED");
            }
        });

        initToolbar();
        initTabs();

//        Intent intent = new Intent(this, TaskActivity.class);
//        startActivity(intent);
    }

    private void initToolbar() {
        // полоса меню в верхней части
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitle(R.string.settings_tag);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            // ASK: что они делают? у нас по-моему они не работают
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab", tabs.getSelectedTabPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null
                && savedInstanceState.containsKey("tab")
                && savedInstanceState.getInt("tab") == 2) {
            tabs.getTabAt(2).select();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.empty_anim, R.anim.exit_bottom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.empty_anim, R.anim.exit_bottom);
        }
        return super.onOptionsItemSelected(item);
    }
}
