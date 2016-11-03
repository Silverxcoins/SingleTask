package com.example.sasha.singletask.choice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.helpers.Utils;
import com.example.sasha.singletask.settings.SettingsActivity;
import com.example.sasha.singletask.user.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChoiceActivity extends AppCompatActivity implements SyncManager.Callback {

    private static final Logger logger = LoggerFactory.getLogger(ChoiceActivity.class);

    private static final String AFTER_SIGN_IN_KEY = "afterSignIn";
    private static final String IS_SIGNED_IN_KEY = "isSignedIn";
    private static final String ID_KEY = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        logger.debug("onCreate()");

        initToolbar();
        setUserIdToUtils();
        SyncManager.getInstance().setCallback(this);
        if (savedInstanceState == null) {
            if (getIntent().hasExtra(AFTER_SIGN_IN_KEY)) {
                Utils.setUserId(getIntent().getIntExtra(ID_KEY, 0));
                SyncManager.getInstance().getDataFromServer(this);
            } else {
                SyncManager.getInstance().sync(this);
            }
        }

        setSelectTimeFragment();
    }

    private void initToolbar() {

        logger.debug("initToolbar()");

        Toolbar toolbar = (Toolbar) findViewById(R.id.choice_toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == getString(R.string.synchronize_title)) {
                    SyncManager.getInstance().sync(ChoiceActivity.this);
                } else if (item.getTitle() == getString(R.string.settings_title)) {
                    startSettingsActivity();
                } else {
                    exit();
                }
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
    }

    private void setUserIdToUtils() {

        logger.debug("setUserIdToUtils()");

        Utils.setUserId(getSharedPreferences(getString(R.string.PREFS_NAME),0).getInt(ID_KEY,0));
    }

    private void startSettingsActivity() {

        logger.debug("startSettingsActivity()");

        Intent intent = new Intent(ChoiceActivity.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_bottom, R.anim.empty_anim);
    }

    private void exit() {

        logger.debug("exit()");

        SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_SIGNED_IN_KEY, false);
        editor.apply();
        Intent intent = new Intent(ChoiceActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSyncFinished(boolean wasSuccessful) {

        logger.debug("onSyncFinished()");

        if (wasSuccessful) {
            logger.info("Sync success");
        } else {
            logger.warn("Sync failed");
        }
    }

    @Override
    protected void onDestroy() {

        logger.debug("onDestroy()");

        DB.getInstance(this).close();
        super.onDestroy();
    }

    private void setSelectTimeFragment() {

        logger.debug("setSelectTimeFragment()");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.replace(R.id.choice_container, new SelectTimeFragment());
        ft.commit();
    }
}
