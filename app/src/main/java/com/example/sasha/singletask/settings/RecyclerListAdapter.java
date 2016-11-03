package com.example.sasha.singletask.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.util.Log;
import android.widget.Toast;

import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.helpers.ItemTouchHelperAdapter;
import com.example.sasha.singletask.helpers.ItemTouchHelperViewHolder;
import com.example.sasha.singletask.R;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final String TAG = "RecyclerListAdapter";
    private Context context;
    private ArrayList<Map> mItems = new ArrayList<Map>();

    public static String mTabName;

    public RecyclerListAdapter(String tabName, ArrayList<Map> list) {
        mTabName = tabName;

        mItems.clear();
        mItems.addAll(list);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (mTabName == CategoriesFragment.tabName) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        } else  {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        }
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        context = parent.getContext();
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        if (mItems.size() != 0) {
            try {
                holder.textView.setText(mItems.get(position).get("categoryName").toString());
                holder.wrapView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_anim));
                        Log.d(TAG, mItems.get(position).get("categoryId").toString() + " clicked");
                        Long categoryId = Long.parseLong(mItems.get(position).get("categoryId").toString());
                        Intent intent = new Intent(context, CategoryActivity.class);
                        intent.putExtra("category", categoryId);
                        context.startActivity(intent);
                    }
                });
            } catch (NullPointerException e1) {
                try {
                    holder.textView.setText(mItems.get(position).get("taskName").toString());
                    holder.wrapView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_anim));
                            Log.d(TAG, mItems.get(position).get("taskId").toString() + " clicked");
                            Long taskId = Long.parseLong(mItems.get(position).get("taskId").toString());
                            Intent intent = new Intent(context, TaskActivity.class);
                            intent.putExtra("task", taskId);
                            context.startActivity(intent);
                        }
                    });
                } catch (NullPointerException e2) {
                    Toast.makeText(context, R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }
    }

    @Override
    public void onItemDismiss(final int position) {
        new AlertDialog.Builder(context)
            .setMessage("Вы действительно хотите удалить?")
            .setCancelable(false)
            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mItems.remove(position);
                    notifyItemRemoved(position);
                }
            })
            .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    notifyItemChanged(position);
                }
            })
            .show();
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
        public final View wrapView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            wrapView = itemView;
            if (mTabName == CategoriesFragment.tabName) {
                textView = (TextView) itemView.findViewById(R.id.category_item_view);
            } else {
                textView = (TextView) itemView.findViewById(R.id.task_item_view);
            }
        }

        @Override
        public void onItemSelected() {
            /*wrapView.setBackgroundColor(Color.LTGRAY);*/
        }

        @Override
        public void onItemClear() {
            /*wrapView.setBackgroundColor(0)*/;
        }
    }
}
