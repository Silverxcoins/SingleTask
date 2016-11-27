package com.example.sasha.singletask.choice.categoriesRecyclerView;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsItemViewHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoriesAdapter extends RecyclerView.Adapter {

    private static final Logger logger = LoggerFactory.getLogger(CategoriesAdapter.class);

    private final Activity activity;
    private final CategoriesDataSource dataSource;
    private final RecyclerView recyclerView;

    public CategoriesAdapter(Activity activity, CategoriesDataSource dataSource,
                             RecyclerView recyclerView) {
        this.activity = activity;
        this.dataSource = dataSource;
        this.recyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        logger.debug("onCreateViewHolder()");

        final View view = activity.getLayoutInflater().inflate(R.layout.categories_list_item,
                parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = recyclerView.getChildLayoutPosition(view);
                long categoryId = dataSource.getItem(position).getId();
                DB.getInstance(activity).getVariantsByCategory(categoryId, position);
            }
        });
        return new CategoriesItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        logger.debug("onBindNewHolder");

        ((CategoriesItemViewHolder) holder).bind(dataSource.getItem(position));
    }

    @Override
    public int getItemCount() {

        logger.debug("getItemCount()");

        return dataSource.getCount();
    }
}
