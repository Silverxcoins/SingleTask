package com.example.sasha.singletask.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;
import com.example.sasha.singletask.helpers.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksFragment extends Fragment implements DB.GetTasksCallback{

    private String TAG = getClass().getName();

    private View rootView;
    public TasksRecyclerListAdapter mAdapter;
    private SharedPreferences prefs;
    private ArrayList<Map> tasks = new ArrayList<Map>();


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences(getString(R.string.PREFS_NAME), 0);

        DB.getInstance(getActivity()).setGetTasksCallback(this);
        DB.getInstance(getActivity()).open();
        DB.getInstance(getActivity()).getTasks();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        return rootView;
    }

//    @Override
//    public void onResume() {
//        // retrieve data
//        String taskName = prefs.getString("mTaskName", "");
//        long taskId = prefs.getLong("mTaskId", 0);
//
//        // clean
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putLong("mTaskId", 0);
//        editor.putString("mTaskName", "");
//        editor.commit();
//
//        // add new item and notify adapter about changing data
//        if (taskId != 0 && taskName != "") {
//            Map helper = new HashMap();
//            helper.put("taskId", taskId);
//            helper.put("taskName", taskName);
//            this.tasks.add(helper);
//            try {
//                mAdapter.notifyItemInserted(this.tasks.size());
//            } catch (NullPointerException e) {
//                Log.w(TAG, "NPE exception");
//            }
//        }
//        super.onResume();
//    }

    @Override
    public void onResume() {
        // retrieve data
        String taskName = prefs.getString("mTaskName", "");
        long taskId = prefs.getLong("mTaskId", 0);

        // clean
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("mTaskId", 0);
        editor.putString("mTaskName", "");
        editor.commit();

        // add (update) item and notify adapter about changing data
        if (taskId != 0 && taskName != "") {
            Map helper = new HashMap();
            helper.put("taskId", taskId);
            helper.put("taskName", taskName);

            // update
            boolean hasTask = false;
            for (int position = 0; position < this.tasks.size(); position++) {
                long currentId = Long.parseLong(this.tasks.get(position).get("taskId").toString());
                if (currentId == taskId) {
                    this.tasks.set(position, helper);
                    hasTask = true;
                    try {
                        mAdapter.notifyItemChanged(position);
                    } catch (NullPointerException e) {
                        Log.w(TAG, "NPE exception");
                    }
                    break;
                }
            }

            // create
            if (!hasTask) {
                this.tasks.add(helper);
                try {
                    mAdapter.notifyItemInserted(this.tasks.size());
                } catch (NullPointerException e) {
                    Log.w(TAG, "NPE exception");
                }
            }
        }
        super.onResume();
    }

    @Override
    public void onReceiveTasks(List<TaskDataSet> mTasks) {
        configureView(mTasks);
    }

    private void initTaskData(List<TaskDataSet> mTasks) {
        for (TaskDataSet task : mTasks) {
            Map helper = new HashMap();
            helper.put("taskId", task.getId());
            helper.put("taskName", task.getName());
            this.tasks.add(helper);
        }
    }

    private void configureView(List<TaskDataSet> mTasks) {
        int scrollPosition = 0;
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.tasks_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setHasFixedSize(true);

        initTaskData(mTasks);
        mAdapter = new TasksRecyclerListAdapter(tasks);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper  mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
