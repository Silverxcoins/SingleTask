package com.example.sasha.singletask.settings;

import android.content.Context;
import android.content.DialogInterface;
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

import android.util.Log;

import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.helpers.ItemTouchHelperAdapter;
import com.example.sasha.singletask.helpers.ItemTouchHelperViewHolder;
import com.example.sasha.singletask.R;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<String> mItems = new ArrayList<>();
    private static final String TAG = "RecyclerListAdapter";
    private Context context;

    public static String mTabName;

    public RecyclerListAdapter(String tabName) {
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
        if (tabName == CategoriesFragment.tabName) {
            mItems.addAll(Arrays.asList(CATEGORIES_STRINGS));
        } else if (tabName == TasksFragment.tabName) {
            mItems.addAll(Arrays.asList(TASKS_STRINGS));
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        // TODO: refactor it without if
        if (mTabName == CategoriesFragment.tabName) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        }
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        context = parent.getContext();

        DB.getInstance(context).open();
        DB.getInstance(context).getCategories();


        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        holder.textView.setText(mItems.get(position));
        holder.wrapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_anim));
                Log.d(TAG, "------ clicked " + position);
            }
        });
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
        String prev = mItems.remove(fromPosition);
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
