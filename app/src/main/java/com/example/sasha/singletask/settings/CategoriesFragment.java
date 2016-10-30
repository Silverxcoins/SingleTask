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

public class CategoriesFragment extends Fragment implements DB.Callback {

    private static final String TAG = "CategoriesFragment";

    private View rootView;
    private ArrayList<Map> items = new ArrayList<Map>();
    private RecyclerListAdapter mAdapter;
    public static final String tabName = "categories_tab";

    public static CategoriesFragment getInstance() {
        Log.d(TAG, "getInstance() =======");
        Bundle bundle = new Bundle();
        CategoriesFragment categoriesFragment = new CategoriesFragment();
        categoriesFragment.setArguments(bundle);

        return categoriesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        Log.d(TAG, "initData()-----------");
        DB.getInstance(getActivity()).open();
        DB.getInstance(getActivity()).setCallback(this);
        DB.getInstance(getActivity()).getCategories();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        configureView();
        return rootView;
    }

    private void getAllCategories(DB.Operation operation, Cursor result, int position) {
        if (result.moveToFirst()) {
            do {
                String categoryName = result.getString(result.getColumnIndex("name"));
                Long categoryId = result.getLong(result.getColumnIndex("id"));
                Map helper = new HashMap();
                helper.put("categoryId", categoryId);
                helper.put("categoryName", categoryName);
                items.add(helper);
            } while (result.moveToNext());
        }
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

    @Override
    public void onOperationFinished(DB.Operation operation, Cursor result, int position) {
        Log.d(TAG, "+++++++++++++++++++++++++++++categories");
        getAllCategories(operation, result, position);
        Log.d(TAG, items.toString());
        mAdapter.updateItems(items);
//        DB.getInstance(getActivity()).close();
//        configureView();
//        mAdapter.notifyDataSetChanged();
//        DB.getInstance(getActivity()).close();
    }
}
