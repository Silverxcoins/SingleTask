package com.example.sasha.singletask;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class ChoiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        ((TextView) findViewById(R.id.textView)).setText(settings.getString("token", ""));
    }
}
