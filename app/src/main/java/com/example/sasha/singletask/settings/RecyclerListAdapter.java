package com.example.sasha.singletask.settings;

import android.content.Context;
import android.content.DialogInterface;
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

    // TODO: arrange fields
    private static final String TAG = "RecyclerListAdapter";
    private Context context;
    private ArrayList<Map> mItems = new ArrayList<Map>();
    public static String mTabName;

    // TODO: rename list
    public RecyclerListAdapter(String tabName, ArrayList<Map> list) {
        String[] CATEGORIES_STRINGS = new String[]{
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"
        };

        String[] TASKS_STRINGS = new String[]{
                "One11", "Two11", "Three11", "Four11", "Five11", "Six11", "Seven11", "Eight11", "Nine11", "Ten11",
                "One11", "Two11", "Three11", "Four11", "Five11", "Six11", "Seven11", "Eight11", "Nine11", "Ten11",
                "One11", "Two11", "Three11", "Four11", "Five11", "Six11", "Seven11", "Eight11", "Nine11", "Ten11",
                "One11", "Two11", "Three11", "Four11", "Five11", "Six11", "Seven11", "Eight11", "Nine11", "Ten11",
                "One11", "Two11", "Three11", "Four11", "Five11", "Six11", "Seven11", "Eight11", "Nine11", "Ten11",
        };
        mTabName = tabName;
        // TODO ASK: remove it?
        mItems.clear();
        mItems.addAll(list);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO: refactor it without if
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
            // TODO FIX: the realization via <if tabName == CategoriesFragment.tabName>
            // doesn't work cause of not clear reason - tabNames for Category and Task fragments
            // tabNames equal tasks!!
            try {
                holder.textView.setText(mItems.get(position).get("categoryName").toString());
                holder.wrapView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_anim));
                        Log.d(TAG, mItems.get(position).get("categoryId").toString() + " clicked");
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

        // TODO: don't hardcode! remove it in resources
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
            // TODO: rewrite it without if
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
