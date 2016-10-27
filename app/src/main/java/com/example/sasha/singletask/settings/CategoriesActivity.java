package com.example.sasha.singletask.settings;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.sasha.singletask.R;

public class CategoriesActivity extends AppCompatActivity {

    private static final String TAG = "CategoriesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            CategoriesFragment fragment = new CategoriesFragment();
            transaction.replace(R.id.categories_list_container, fragment);
            transaction.commit();
        }

        Log.d(TAG, "success onCreate() CategoriesActivity");
    }
}
