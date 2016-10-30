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
    // показ и прокрутка страниц (swipe)
    private ViewPager pager;
    // вкладки
    private TabLayout tabs;

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
            }
        });

        DB.getInstance(this).open();
        DB.getInstance(this).setCallback(this);
        DB.getInstance(this).getCategories();
        DB.getInstance(this).getTasks();

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
        Log.d(TAG, "CALLCALLCALLCALLCALLCALLCALL");
        Log.d(TAG, catItems.toString() + "*****************");
        Log.d(TAG, tasksItems.toString() + "*****************");
        pager.setAdapter(new TabsPagerFragmentAdapter(getSupportFragmentManager(), catItems, tasksItems));
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

    private void getAllCategories(DB.Operation operation, Cursor result, int position) {
        if (result.moveToFirst()) {
            do {
                String categoryName = result.getString(result.getColumnIndex("name"));
                Long categoryId = result.getLong(result.getColumnIndex("id"));
                Map helper = new HashMap();
                helper.put("categoryId", categoryId);
                helper.put("categoryName", categoryName);
                catItems.add(helper);
            } while (result.moveToNext());
        }
    }

    private void getAllTasks(DB.Operation operation, Cursor result, int position) {
        if (result.moveToFirst()) {
            do {
                String taskName = result.getString(result.getColumnIndex("name"));
                Long taskId = result.getLong(result.getColumnIndex("id"));
                Map helper = new HashMap();
                helper.put("taskId", taskId);
                helper.put("taskName", taskName);
                tasksItems.add(helper);
            } while (result.moveToNext());
        }
    }

    @Override
    public void onOperationFinished(DB.Operation operation, Cursor result, int position) {
        if (operation == DB.Operation.GET_CATEGORIES) {
            getAllCategories(operation, result, position);
        } else if (operation == DB.Operation.GET_TASKS) {
            getAllTasks(operation, result, position);
        }
        Log.d(TAG, "+++++++++++++++++++++++++++++categories");

//        getAllTasks(operation, result, position);
        Log.d(TAG, tasksItems.toString() + "///////////////////");
        Log.d(TAG, catItems.toString() + "///////////////////");
//        mAdapter.updateItems(items);

//        initTabs();
//        initToolbar();
        pager.getAdapter().notifyDataSetChanged();
    }
}
