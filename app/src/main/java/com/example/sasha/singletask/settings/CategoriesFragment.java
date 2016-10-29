package com.example.sasha.singletask.settings;

// arrange imports
import android.app.Activity;
import android.os.Bundle;
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
import android.support.v4.app.ListFragment;
import android.widget.ListView;

import com.example.sasha.singletask.R;

import com.example.sasha.singletask.helpers.SimpleItemTouchHelperCallback;

public class CategoriesFragment extends Fragment {

    private static final String TAG = "CategoriesFragment";

    private View rootView;
    private RecyclerView mRecyclerView;
    private RecyclerListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemTouchHelper mItemTouchHelper;

    public static CategoriesFragment getInstance() {
        Bundle bundle = new Bundle();
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        categoriesFragment.setArguments(bundle);

        return categoriesFragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initDataset();
//    }

    public interface OnListItemClickListener {
        void onListItemClick(int position);
    }

    private OnListItemClickListener mItemClickListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int scrollPosition = 0;
        rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        Log.d(TAG, "1");

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categories_recycler_view);

        Log.d(TAG, "2");

        mLayoutManager = new LinearLayoutManager(getActivity());

        Log.d(TAG, "3");

        mRecyclerView.setLayoutManager(mLayoutManager);

        Log.d(TAG, "4");

        mRecyclerView.scrollToPosition(scrollPosition);

        Log.d(TAG, "5");

        mRecyclerView.setHasFixedSize(true);

        Log.d(TAG, "6");

        mAdapter = new RecyclerListAdapter();

        Log.d(TAG, "7");

        mRecyclerView.setAdapter(mAdapter);

        Log.d(TAG, "8");

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);

        Log.d(TAG, "9");

        mItemTouchHelper = new ItemTouchHelper(callback);

        Log.d(TAG, "10");

        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        Log.d(TAG, "11");

        return rootView;
    }

//    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mItemClickListener.onListItemClick(position);
    }

//    private void initDataset() {
//        mDataset = new String[DATASET_COUNT];
//        for (int i = 0; i < DATASET_COUNT; i++) {
//            mDataset[i] = "This is element #" + i;
//        }
//    }
}


//    int scrollPosition = 0;
//
//rootView = inflater.inflate(R.layout.fragment_categories, container, false);
//
//        Log.d(TAG, "1");
//
//        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categories_recycler_view);
//
//        Log.d(TAG, "2");
//
//        mLayoutManager = new LinearLayoutManager(getActivity());
//
//        Log.d(TAG, "2");
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        Log.d(TAG, "3");
//
//        mRecyclerView.scrollToPosition(scrollPosition);
//
//        Log.d(TAG, "4");
//
//        mAdapter = new CustomAdapter(mDataset);
//
//        Log.d(TAG, "5");
//
//        mRecyclerView.setAdapter(mAdapter);
//
//        Log.d(TAG, "6");
//
//        return rootView;
//
//        int scrollPosition = 0;
//
//        rootView = inflater.inflate(R.layout.fragment_categories, container, false);
//
//        Log.d(TAG, "1");
//
//        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categories_recycler_view);
//
//        Log.d(TAG, "2");
//
//        mLayoutManager = new LinearLayoutManager(getActivity());
//
//        Log.d(TAG, "2");
//
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        Log.d(TAG, "3");
//
//        mRecyclerView.scrollToPosition(scrollPosition);
//
//        Log.d(TAG, "4");
//
//        mAdapter = new CustomAdapter(mDataset);
//
//        Log.d(TAG, "5");
//
//        mRecyclerView.setAdapter(mAdapter);
//
//        Log.d(TAG, "6");
//
//        return rootView;