package com.example.sasha.singletask.settings;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.sasha.singletask.R;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();
        initViewPager();
        initTabLayout();
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
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        SettingsPagerAdapter adapter = new SettingsPagerAdapter(getSupportFragmentManager());
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