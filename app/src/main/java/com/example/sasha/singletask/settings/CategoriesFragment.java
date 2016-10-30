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

public class CategoriesFragment extends Fragment {

    private static final String TAG = "CategoriesFragment";

    private View rootView;
    public static ArrayList<Map> items = new ArrayList<Map>();
    public static RecyclerListAdapter mAdapter;
    public static final String tabName = "categories_tab";

    private static CategoriesFragment categoriesFragment;

    public static CategoriesFragment getInstance(ArrayList<Map> mItems) {
        Bundle bundle = new Bundle();

        if (categoriesFragment == null) {
            categoriesFragment = new CategoriesFragment();
            categoriesFragment.setArguments(bundle);
        } else {
            items.clear();
            items.addAll(mItems);
//            mAdapter.notifyDataSetChanged();
        }
        return categoriesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        configureView();
        return rootView;
    }

    // TODO FIX: this method calls multiple times and creates a lot of the same objects

    private void configureView() {
        int scrollPosition = 0;
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categories_recycler_view);
        RecyclerView.LayoutManager    mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new RecyclerListAdapter(tabName, items);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback    callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper  mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
