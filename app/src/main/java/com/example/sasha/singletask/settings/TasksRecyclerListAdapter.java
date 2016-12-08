package com.example.sasha.singletask.settings;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;
import com.example.sasha.singletask.helpers.ItemTouchHelperAdapter;
import com.example.sasha.singletask.helpers.ItemTouchHelperViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TasksRecyclerListAdapter extends RecyclerView.Adapter<TasksRecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private Context context;
    private final ArrayList<Map> mItems;
    public TasksRecyclerListAdapter(ArrayList<Map> tasks) {
        mItems = tasks;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        holder.textView.setText(mItems.get(position).get("taskName").toString());
        handleItemClick(holder, position);
    }

    private void handleItemClick(final ItemViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long taskId = Long.parseLong(mItems.get(position).get("taskId").toString());
                Intent intent = new Intent(context, TaskActivity.class);
                intent.putExtra("task", taskId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
//        mItems.remove(position);
//        notifyItemRemoved(position);

        notifyItemChanged(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Map prev = mItems.remove(fromPosition);
        mItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        public final TextView textView;
        public final View itemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.task_item_view);
            this.itemView = itemView;
        }

        @Override
        public void onItemSelected() {
//            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
//            itemView.setBackgroundColor(0);
        }
    }
}