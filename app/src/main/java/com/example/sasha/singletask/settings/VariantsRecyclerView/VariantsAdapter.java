package com.example.sasha.singletask.settings.VariantsRecyclerView;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.sasha.singletask.R;

public class VariantsAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private VariantsDataSource dataSource;
    private LinearLayoutManager manager;

    public VariantsAdapter(Activity activity, VariantsDataSource dataSource,
                           LinearLayoutManager manager) {
        this.activity = activity;
        this.dataSource = dataSource;
        this.manager = manager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VariantsItemViewHolder(
                activity.getLayoutInflater().inflate(R.layout.variants_item, parent, false),
                dataSource
        );
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((VariantsItemViewHolder) holder).bind(dataSource.getItem(position));
        if (dataSource.getItem(position).getVariantName() == null) {
            ((VariantsItemViewHolder) holder).setFocusOnVariantName();
        }
        if (position == dataSource.getCount() - 1
                && dataSource.getItem(position).getVariantName() == null) {
            ((VariantsItemViewHolder) holder).setEditable();
        }
    }

    @Override
    public int getItemCount() {
        return dataSource.getCount();
    }
}
