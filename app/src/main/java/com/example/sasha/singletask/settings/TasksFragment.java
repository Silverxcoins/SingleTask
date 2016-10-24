package com.example.sasha.singletask.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sasha.singletask.R;

public class TasksFragment extends Fragment {

    private View view;

    public static TasksFragment getInstance() {
        Bundle bundle = new Bundle();
        TasksFragment tasksFragment = new TasksFragment();
        tasksFragment.setArguments(bundle);

        return tasksFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasks, container, false);
        return view;
    }
}
