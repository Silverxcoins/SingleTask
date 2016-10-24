package com.example.sasha.singletask.choice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.settings.SettingsActivity;

public class ChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        ((TextView) findViewById(R.id.textView)).setText(settings.getString("token", ""));

        ////
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        ////
    }
}
