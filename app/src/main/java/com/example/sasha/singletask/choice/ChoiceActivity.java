package com.example.sasha.singletask.choice;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

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

    private ProgressBar progressBar;

    private Fragment selectTimeFragment;
    private Fragment variantsChoiceFragment;
    private Fragment chosenTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        logger.debug("onCreate()");

        initToolbar();
        initProgressBar();
        setUserIdToUtils();
        setArrowsButtonsListeners();
        SyncManager.getInstance().setCallback(this);
        if (savedInstanceState == null) {
            setLoading(true);
            if (getIntent().hasExtra(AFTER_SIGN_IN_KEY)) {
                SyncManager.getInstance().getDataFromServer(this);
            } else {
                SyncManager.getInstance().sync(this);
            }
        }

        selectTimeFragment = new SelectTimeFragment();
        variantsChoiceFragment = new VariantsChoiceFragment();
        chosenTaskFragment = new ChosenTaskFragment();

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
                    setLoading(true);
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

    private void initProgressBar() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void setUserIdToUtils() {

        logger.debug("setUserIdToUtils()");

        Long userId = getSharedPreferences(getString(R.string.PREFS_NAME),0).getLong(ID_KEY,0);
        Utils.setUserId(userId);
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

        setLoading(false);
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
        ft.replace(R.id.choice_container, selectTimeFragment);
        ft.commit();
    }

    private void setVariantsChoiceFragment() {

        logger.debug("setVariantsChoiceFragment()");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.choice_container, variantsChoiceFragment);
        ft.commit();
    }

    private void setChosenTaskFragment() {

        logger.debug("setChosenTaskFragment()");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.choice_container, chosenTaskFragment);
        ft.commit();
    }

    private void setArrowsButtonsListeners() {

        logger.debug("setArrowsButtonsListeners");

        findViewById(R.id.rightArrowBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeFragment.isVisible()) {
                    setVariantsChoiceFragment();
                } else if (variantsChoiceFragment.isVisible()) {
                    setChosenTaskFragment();
                }
            }
        });
    }
}
