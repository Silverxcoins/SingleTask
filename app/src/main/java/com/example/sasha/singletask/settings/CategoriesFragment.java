package com.example.sasha.singletask.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sasha.singletask.R;

public class CategoriesFragment extends Fragment {
    private View view;

    public static CategoriesFragment getInstance() {
        Bundle bundle = new Bundle();
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        categoriesFragment.setArguments(bundle);

        return categoriesFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_categories, container, false);
        return view;
    }
}
