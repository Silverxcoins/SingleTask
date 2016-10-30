package com.example.sasha.singletask.settings;

import android.content.Intent;
import android.database.Cursor;
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
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.helpers.TabsPagerFragmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity implements DB.Callback {

    private static final String TAG = "SettingsActivity";
    private ViewPager pager;
    private TabLayout tabs;
    public ArrayList<Map> categoryItems = new ArrayList<Map>();
    public ArrayList<Map> taskItems = new ArrayList<Map>();

    private ArrayList<Map> catItems = new ArrayList<Map>();
    private ArrayList<Map> tasksItems = new ArrayList<Map>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "FLOAT BUTTON CLIKED");
                Intent intent = new Intent(getBaseContext(), TaskActivity.class);
                startActivity(intent);
            }
        });

        initToolbar();
        initTabs();

        DB.getInstance(this).open();
        DB.getInstance(this).setCallback(this);
        DB.getInstance(this).getTasks();
        DB.getInstance(this).getCategories();

//        Intent intent = new Intent(this, TaskActivity.class);
//        startActivity(intent);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitle(R.string.settings_tag);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private TabsPagerFragmentAdapter mAdapter;

    private void initTabs() {
        pager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerFragmentAdapter(getSupportFragmentManager(), taskItems, categoryItems);
        pager.setAdapter(mAdapter);
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
        Log.d(TAG, "restore");
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

    public void getAllCategories(DB.Operation operation, Cursor result, int position) {
        if (result.moveToFirst()) {
            do {
                String categoryName = result.getString(result.getColumnIndex("name"));
                Long categoryId = result.getLong(result.getColumnIndex("id"));
                Map helper = new HashMap();
                helper.put("categoryId", categoryId);
                helper.put("categoryName", categoryName);
                categoryItems.add(helper);
            } while (result.moveToNext());
        }
    }

    public void getAllTasks(DB.Operation operation, Cursor result, int position) {
        if (result.moveToFirst()) {
            do {
                String taskName = result.getString(result.getColumnIndex("name"));
                Long taskId = result.getLong(result.getColumnIndex("id"));
                // TODO FIX: allocating too much memory! optimize this code!
                Map helper = new HashMap();
                helper.put("taskId", taskId);
                helper.put("taskName", taskName);
                taskItems.add(helper);
            } while (result.moveToNext());
        }
    }

    @Override
    public void onOperationFinished(DB.Operation operation, Cursor result, int position) {
        Log.d(TAG, "onOperationFinished()");
        if (operation == DB.Operation.GET_CATEGORIES) {
            getAllCategories(operation, result, position);
        } else if (operation == DB.Operation.GET_TASKS) {
            getAllTasks(operation, result, position);
        }
        mAdapter.notifyDataSetChanged();
    }
}
