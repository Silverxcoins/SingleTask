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
import com.example.sasha.singletask.choice.categoriesRecyclerView.CategoriesItem;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.db.dataSets.CategoryDataSet;
import com.example.sasha.singletask.helpers.SimpleItemTouchHelperCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;


public class CategoriesFragment extends Fragment implements DB.GetCategoriesCallback {

    private String TAG = getClass().getName();

    private View rootView;
    private CategoriesRecyclerListAdapter mAdapter;
    private SharedPreferences prefs;
    private ArrayList<Map> categories = new ArrayList<Map>();

    public static final ArrayList<Map> items = new ArrayList<Map>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getActivity().getSharedPreferences(getString(R.string.PREFS_NAME), 0);

        DB.getInstance(getActivity()).setGetCategoriesCallback(this);
        DB.getInstance(getActivity()).open();
        DB.getInstance(getActivity()).getCategories();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_categories, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        // retrieve data
        String categoryName = prefs.getString("mCategoryName", "");
        long categoryId = prefs.getLong("mCategoryId", 0);

        // clean
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("mCategoryId", 0);
        editor.putString("mCategoryName", "");
        editor.commit();

        // add (update) item and notify adapter about changing data
        if (categoryId != 0 && categoryName != "") {
            Map helper = new HashMap();
            helper.put("categoryId", categoryId);
            helper.put("categoryName", categoryName);

            // update
            boolean hasCategory = false;
            for (int position = 0; position < this.categories.size(); position++) {
                long currentId = Long.parseLong(this.categories.get(position).get("categoryId").toString());
                if (currentId == categoryId) {
                    this.categories.set(position, helper);
                    hasCategory = true;
                    try {
                        mAdapter.notifyItemChanged(position);
                    } catch (NullPointerException e) {
                        Log.w(TAG, "NPE exception");
                    }
                    break;
                }
            }

            // create
            if (!hasCategory) {
                this.categories.add(helper);
                try {
                    mAdapter.notifyItemInserted(this.categories.size());
                } catch (NullPointerException e) {
                    Log.w(TAG, "NPE exception");
                }
            }
        }
        super.onResume();
    }

    @Override
    public void onReceiveCategories(List<CategoryDataSet> categories) {
        configureView(categories);
    }

    private void initCategoryData(List<CategoryDataSet> mCategories) {
        for (CategoryDataSet category : mCategories) {
            Map helper = new HashMap();
            helper.put("categoryId", category.getId());
            helper.put("categoryName", category.getName());
            this.categories.add(helper);
        }
    }

    private void configureView(List<CategoryDataSet> mCategories) {
        int scrollPosition = 0;

        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.categories_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
        mRecyclerView.setHasFixedSize(true);

        initCategoryData(mCategories);
        mAdapter = new CategoriesRecyclerListAdapter(categories);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper  mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }
}
