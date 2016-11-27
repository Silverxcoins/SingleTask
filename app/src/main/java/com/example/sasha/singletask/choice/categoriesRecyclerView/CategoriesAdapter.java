package com.example.sasha.singletask.choice.categoriesRecyclerView;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsItemViewHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoriesAdapter {

//    private static final Logger logger = LoggerFactory.getLogger(CategoriesAdapter.class);
//
//    private final Activity activity;
//    private final CategoriesDataSource dataSource;
//
//    public CategoriesAdapter(Activity activity, CategoriesDataSource dataSource) {
//        this.activity = activity;
//        this.dataSource = dataSource;
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        logger.debug("onCreateViewHolder()");
//
//        return new CategoriesItemViewHolder(
//                activity.getLayoutInflater().inflate(R.layout.list_item, parent, false),
//                dataSource
//        );
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//        logger.debug("onBindNewHolder");
//
//        ((VariantsItemViewHolder) holder).bind(dataSource.getItem(position));
//        if (dataSource.getItem(position).getVariantName() == null) {
//            ((VariantsItemViewHolder) holder).setFocusOnVariantName();
//        }
//        if (position == dataSource.getCount() - 1
//                && dataSource.getItem(position).getVariantName() == null) {
//            ((VariantsItemViewHolder) holder).setEditable();
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//
//        logger.debug("getItemCount()");
//
//        return dataSource.getCount();
//    }
}
