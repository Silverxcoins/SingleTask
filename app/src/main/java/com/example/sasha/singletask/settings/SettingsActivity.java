package com.example.sasha.singletask.settings;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.sasha.singletask.R;

import static com.example.sasha.singletask.R.id.pager;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private SettingsPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();
        initViewPager();
        initTabLayout();

        handleFabClick();
    }

    private void handleFabClick() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (viewPager.getCurrentItem() == 0) {
                    intent = new Intent(getBaseContext(), TaskActivity.class);
                    startActivity(intent);
                } else if (viewPager.getCurrentItem() == 1) {
                    intent = new Intent(getBaseContext(), CategoryActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void initToolbar() {
        // toolbar initialization
        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        toolbar.setTitle(R.string.settings_tag);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(pager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new SettingsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TasksFragment(), getString(R.string.tasks_tab_title));
        adapter.addFragment(new CategoriesFragment(), getString(R.string.categories_tab_title));
        viewPager.setAdapter(adapter);
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.empty_anim, R.anim.exit_bottom);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle clicking on the back arrow
        if (item.getItemId() == android.R.id.home) {
            finish();
            // add animation for transition between activities
            overridePendingTransition(R.anim.empty_anim, R.anim.exit_bottom);
        }
        return super.onOptionsItemSelected(item);
    }
}