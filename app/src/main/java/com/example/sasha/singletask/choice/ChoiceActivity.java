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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.choice.categoriesRecyclerView.CategoriesItem;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.helpers.Utils;
import com.example.sasha.singletask.settings.SettingsActivity;
import com.example.sasha.singletask.user.MainActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ChoiceActivity extends AppCompatActivity implements SyncManager.Callback {

    private static final Logger logger = LoggerFactory.getLogger(ChoiceActivity.class);

    private static final String AFTER_SIGN_IN_KEY = "afterSignIn";
    private static final String IS_SIGNED_IN_KEY = "isSignedIn";
    private static final String ID_KEY = "id";
    private static final String SELECT_TIME_FRAGMENT_KEY = "selectTimeFragment";
    private static final String VARIANTS_CHOICE_FRAGMENT_KEY = "variantsChoiceFragment";
    private static final String CHOSEN_TASK_FRAGMENT_KEY = "chosenTaskFragment";
    private static final String CURRENT_TASK_FRAGMENT_KEY = "currentTaskFragment";
    private static final String VISIBLE_FRAGMENT_NUMBER_KEY = "visibleFragmentNumber";
    private static final String CURRENT_TASK_KEY = "currentTask";

    private ProgressBar progressBar;
    private ImageButton rightArrow;
    private ImageButton leftArrow;

    private Fragment selectTimeFragment;
    private Fragment variantsChoiceFragment;
    private Fragment chosenTaskFragment;
    private Fragment currentTaskFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        logger.debug("onCreate()");

        initToolbar();
        initProgressBar();
        setUserIdToUtils();

        leftArrow = (ImageButton) findViewById(R.id.leftArrowBtn);
        rightArrow = (ImageButton) findViewById(R.id.rightArrowBtn);
        setArrowsButtonsListeners();
        SyncManager.getInstance().setCallback(this);
        if (savedInstanceState == null) {
            setLoading(true);
            if (getIntent().hasExtra(AFTER_SIGN_IN_KEY)) {
                SyncManager.getInstance().getDataFromServer(this);
            } else {
                SyncManager.getInstance().sync(this);
            }
            selectTimeFragment = new SelectTimeFragment();

            SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS_NAME),0);
            if (settings.getLong(CURRENT_TASK_KEY,0) == 0) {
                setSelectTimeFragment();
            } else {
                currentTaskFragment = new CurrentTaskFragment();
                setCurrentTaskFragment();
            }
        } else {
            if (!savedInstanceState.containsKey(CURRENT_TASK_FRAGMENT_KEY)) {
                if (savedInstanceState.containsKey(SELECT_TIME_FRAGMENT_KEY)) {
                    selectTimeFragment = getSupportFragmentManager()
                            .getFragment(savedInstanceState, SELECT_TIME_FRAGMENT_KEY);
                } else {
                    selectTimeFragment = new SelectTimeFragment();
                }
                setSelectTimeFragment();

                if (savedInstanceState.containsKey(VARIANTS_CHOICE_FRAGMENT_KEY)) {
                    variantsChoiceFragment = getSupportFragmentManager()
                            .getFragment(savedInstanceState, VARIANTS_CHOICE_FRAGMENT_KEY);
                    if (savedInstanceState.getInt(VISIBLE_FRAGMENT_NUMBER_KEY, 0) > 0) {
                        setVariantsChoiceFragment();
                    }
                } else {
                    variantsChoiceFragment = new VariantsChoiceFragment();
                }

                if (savedInstanceState.containsKey(CHOSEN_TASK_FRAGMENT_KEY)) {
                    chosenTaskFragment = getSupportFragmentManager()
                            .getFragment(savedInstanceState, CHOSEN_TASK_FRAGMENT_KEY);
                    if (savedInstanceState.getInt(VISIBLE_FRAGMENT_NUMBER_KEY, 0) > 1) {
                        setChosenTaskFragment();
                    }
                } else {
                    chosenTaskFragment = new ChosenTaskFragment();
                }
            } else {
                currentTaskFragment = getSupportFragmentManager()
                        .getFragment(savedInstanceState, CURRENT_TASK_FRAGMENT_KEY);
                setCurrentTaskFragment();
                selectTimeFragment = new SelectTimeFragment();
                variantsChoiceFragment = new VariantsChoiceFragment();
                chosenTaskFragment = new ChosenTaskFragment();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Utils.clearBackStack(this);
        if (currentTaskFragment == null || !currentTaskFragment.isVisible()) {
            if (selectTimeFragment != null && !selectTimeFragment.isVisible()) {
                getSupportFragmentManager().putFragment(outState, SELECT_TIME_FRAGMENT_KEY,
                        selectTimeFragment);
            }
            if (variantsChoiceFragment != null
                    && (variantsChoiceFragment.isVisible() || (chosenTaskFragment != null
                    && chosenTaskFragment.isVisible()))) {
                getSupportFragmentManager().putFragment(outState, VARIANTS_CHOICE_FRAGMENT_KEY,
                        variantsChoiceFragment);
            }
            if (chosenTaskFragment != null && chosenTaskFragment.isVisible()) {
                getSupportFragmentManager().putFragment(outState, CHOSEN_TASK_FRAGMENT_KEY,
                        chosenTaskFragment);
            }

            int visibleFragmentNumber = 0;
            if (variantsChoiceFragment != null && variantsChoiceFragment.isVisible())
                visibleFragmentNumber = 1;
            else if (chosenTaskFragment != null && chosenTaskFragment.isVisible())
                visibleFragmentNumber = 2;
            outState.putInt(VISIBLE_FRAGMENT_NUMBER_KEY, visibleFragmentNumber);
        } else {
            getSupportFragmentManager().putFragment(outState, CURRENT_TASK_FRAGMENT_KEY,
                    currentTaskFragment);
        }

        super.onSaveInstanceState(outState);
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

            SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS_NAME),0);
            if (settings.getLong(CURRENT_TASK_KEY, 0) == 0
                    && currentTaskFragment != null && currentTaskFragment.isVisible()) {
                setSelectTimeFragment();
            } else if (settings.getLong(CURRENT_TASK_KEY, 0) != 0
                    && (currentTaskFragment == null || !currentTaskFragment.isVisible())) {
                currentTaskFragment = new CurrentTaskFragment();
                setCurrentTaskFragment();
            }
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

        rightArrow.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.replace(R.id.choice_container, selectTimeFragment);
        ft.commit();
    }

    private void setVariantsChoiceFragment() {

        logger.debug("setVariantsChoiceFragment()");

        leftArrow.setVisibility(View.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.choice_container, variantsChoiceFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void setChosenTaskFragment() {

        logger.debug("setChosenTaskFragment()");

        rightArrow.setVisibility(View.INVISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.choice_container, chosenTaskFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void setCurrentTaskFragment() {

        logger.debug("setCurrentTaskFragment");

        rightArrow.setVisibility(View.INVISIBLE);
        leftArrow.setVisibility(View.INVISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.choice_container, currentTaskFragment);
        ft.commit();
    }

    private void setArrowsButtonsListeners() {

        logger.debug("setArrowsButtonsListeners");

        leftArrow.setVisibility(View.INVISIBLE);
        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectTimeFragment.isVisible()) {
                    int time = ((SelectTimeFragment)selectTimeFragment).getTime();
                    CurrentChoice.getInstance().setTime(time);
                    variantsChoiceFragment = new VariantsChoiceFragment();
                    setVariantsChoiceFragment();
                    DB.getInstance(ChoiceActivity.this).getCategories();
                } else if (variantsChoiceFragment.isVisible()) {
                    List<CategoriesItem> categories =
                            ((VariantsChoiceFragment) variantsChoiceFragment)
                                    .getNotEmptyCategories();
                    CurrentChoice.getInstance().setCategories(categories);
                    chosenTaskFragment = new ChosenTaskFragment();
                    setChosenTaskFragment();
                }
            }
        });

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (currentTaskFragment == null || !currentTaskFragment.isVisible()) {
            if (variantsChoiceFragment != null && variantsChoiceFragment.isVisible()) {
                leftArrow.setVisibility(View.INVISIBLE);
            } else if (chosenTaskFragment != null && chosenTaskFragment.isVisible()) {
                rightArrow.setVisibility(View.VISIBLE);
            }
            super.onBackPressed();
        } else {
            finish();
        }
    }

    public void onTaskAccepted() {
        currentTaskFragment = new CurrentTaskFragment();
        setCurrentTaskFragment();
    }

    public void onTaskUpdated() {
        setSelectTimeFragment();
    }
}
