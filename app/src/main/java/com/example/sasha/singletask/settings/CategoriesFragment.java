package com.example.sasha.singletask.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.helpers.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.Map;

public class CategoriesFragment extends Fragment {

    private final String TAG = "CategoriesFragment";
    private View rootView;
    public RecyclerListAdapter mAdapter;

    public static final ArrayList<Map> items = new ArrayList<Map>();
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

    private void configureView() {
        int scrollPosition = 0;
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categories_recycler_view);
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
