package com.example.sasha.singletask.choice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.db.dataSets.CategoryDataSet;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;
import com.example.sasha.singletask.db.dataSets.TaskVariantDataSet;
import com.example.sasha.singletask.db.dataSets.VariantDataSet;
import com.example.sasha.singletask.settings.SettingsActivity;
import com.example.sasha.singletask.user.MainActivity;

public class ChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.choice_toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle() == getString(R.string.synchronize_title)) {

                } else if (item.getTitle() == getString(R.string.settings_title)) {
                    startSettingsActivity();
                } else {
                    exit();
                }
                return false;
            }
        });
        toolbar.inflateMenu(R.menu.menu);


        ///////
        TaskDataSet.setUserId(getSharedPreferences(getString(R.string.PREFS_NAME),0).getInt("id",0));
        CategoryDataSet.setUserId(getSharedPreferences(getString(R.string.PREFS_NAME),0).getInt("id",0));
        VariantDataSet.setUserId(getSharedPreferences(getString(R.string.PREFS_NAME),0).getInt("id",0));
        TaskVariantDataSet.setUserId(getSharedPreferences(getString(R.string.PREFS_NAME),0).getInt("id",0));
        DB db = DB.getInstance(this);
        db.open();

        db.insertTasksVariantsFromJson("[{\"task\":11,\"variant\":5}]");
        Log.d("!!!!!!!!!!!!!!!!!!!!!!!", db.getAllTasksVariantsInJson());
        db.close();
        ///////
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
}
