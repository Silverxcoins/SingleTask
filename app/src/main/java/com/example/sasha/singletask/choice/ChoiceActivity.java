package com.example.sasha.singletask.choice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.helpers.Utils;
import com.example.sasha.singletask.settings.SettingsActivity;
import com.example.sasha.singletask.user.MainActivity;

public class ChoiceActivity extends AppCompatActivity implements SyncManager.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        initToolbar();
        setUserIdToUtils();
        SyncManager.getInstance().setCallback(this);
        if (savedInstanceState == null) {
            if (getIntent().hasExtra("afterSignIn")) {
                Utils.setUserId(getIntent().getIntExtra("id", 0));
                SyncManager.getInstance().getDataFromServer(this);
            } else {
                SyncManager.getInstance().sync(this);
            }
        }
    }

    private void initToolbar() {
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
        Utils.setUserId(getSharedPreferences(getString(R.string.PREFS_NAME),0).getInt("id",0));
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(ChoiceActivity.this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter_bottom, R.anim.empty_anim);
    }

    private void exit() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isSignedIn", false);
        editor.apply();
        Intent intent = new Intent(ChoiceActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSyncFinished(boolean wasSuccessful) {
        if (wasSuccessful) {
            System.out.println("some kind of success!");
        } else {
            System.out.println("Ну ёбаный в рот :(");
        }
    }

    @Override
    protected void onDestroy() {
        DB.getInstance(this).close();
        super.onDestroy();
    }
}
