package com.example.sasha.singletask.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.helpers.SimpleItemTouchHelperCallback;

public class TasksFragment extends Fragment {

    private View view;

    private static final String TAG = "CategoriesFragment";

    private View rootView;
    private RecyclerView mRecyclerView;
    private RecyclerListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemTouchHelper mItemTouchHelper;

    public static final String tabName = "tasks_tab";

    public static TasksFragment getInstance() {
        Bundle bundle = new Bundle();
        TasksFragment tasksFragment = new TasksFragment();
        tasksFragment.setArguments(bundle);

        return tasksFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int scrollPosition = 0;
        rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categories_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerListAdapter(tabName);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        return rootView;
    }
}
