package com.example.sasha.singletask.settings;

// TODO: arrange imports and remove not used modules
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.choice.SyncManager;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.helpers.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TODO: implement onSavedInstance (onRestore)
public class TasksFragment extends Fragment {
    // TODO: arrange these fields in right order
    private static final String TAG = "TasksFragment";

    private View rootView;
    public static ArrayList<Map> items = new ArrayList<Map>();
    public static RecyclerListAdapter mAdapter;
    public static final String tabName = "tasks_tab";

    private static TasksFragment tasksFragment;

    public static TasksFragment getInstance(ArrayList<Map> mItems) {
        Bundle bundle = new Bundle();

        if (tasksFragment == null) {
            tasksFragment = new TasksFragment();
            tasksFragment.setArguments(bundle);
        } else {
            items.clear();
            items.addAll(mItems);
            mAdapter.notifyDataSetChanged();
        }

        return tasksFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        configureView();
        return rootView;
    }

    private void configureView() {
        int scrollPosition = 0;
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.tasks_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new RecyclerListAdapter(tabName, items);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper  mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
