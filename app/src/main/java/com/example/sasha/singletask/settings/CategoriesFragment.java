package com.example.sasha.singletask.settings;

// arrange imports
import android.database.Cursor;
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

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.helpers.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment implements DB.Callback {

    private static final String TAG = "CategoriesFragment";

    private View rootView;
    private RecyclerView mRecyclerView;
    private RecyclerListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemTouchHelper mItemTouchHelper;

    private ArrayList<Long> categoriesId = new ArrayList<>();
    private ArrayList<String> categoriesNames = new ArrayList<>();

    public static final String tabName = "categories_tab";

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int scrollPosition = 0;
        rootView = inflater.inflate(R.layout.fragment_categories, container, false);
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
        Log.d(TAG, "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
//        onOperationFinished(DB.Operation.GET_CATEGORIES, );
//        DB.getInstance(getActivity()).open();
//        DB.getInstance(getActivity()).setCallback(this);
//        DB.getInstance(getActivity()).getCategories();

        return rootView;
    }

    @Override
    public void onOperationFinished(DB.Operation operation, Cursor result, int position) {
        Log.d(TAG,"onOperationFinished----------");
        if (result.moveToFirst()) {
            do {
                String categoryName = result.getString(result.getColumnIndex("name"));
                Long categoryId = result.getLong(result.getColumnIndex("id"));
                categoriesNames.add(categoryName);
                categoriesId.add(categoryId);
//                String itemString = categoryName + ": ";
//                if (getIntent().hasExtra("task")) {
//                    long taskId = getIntent().getLongExtra("task", 0);
//                    DB.getInstance(this).getVariantByTaskAndCategory(taskId, categoryId,
//                            categoriesId.size() - 1);
//                } else {
//                    String variantName = getString(R.string.empty_variant_string);
//                    itemString += variantName;
//                    variantsNames.add(variantName);
//                    variantsId.add(0L);
//                }
//                itemsStrings.add(itemString);
            } while (result.moveToNext());
        }
        Log.d(TAG, categoriesNames.toString());
        Log.d(TAG, categoriesId.toString());
    }
}
