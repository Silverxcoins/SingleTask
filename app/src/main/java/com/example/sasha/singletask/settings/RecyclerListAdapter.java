package com.example.sasha.singletask.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.example.sasha.singletask.helpers.SimpleItemTouchHelperCallback;
import com.example.sasha.singletask.helpers.ItemTouchHelperAdapter;
import com.example.sasha.singletask.helpers.ItemTouchHelperViewHolder;
import com.example.sasha.singletask.R;

public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private final List<String> mItems = new ArrayList<>();
    private static final String TAG = "RecyclerListAdapter";
    private Context context;


    public RecyclerListAdapter() {
        String[] STRINGS = new String[]{
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"
        };
        mItems.addAll(Arrays.asList(STRINGS));
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        context = parent.getContext();
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        TextView textView = (TextView) holder.itemView.findViewById(R.id.category_item_view);
        holder.textView.setText(mItems.get(position));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Recycle Click" + position, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "------" + position);
//                view.setBackgroundColor(Color.LTGRAY);
//                Animation myAnimation = AnimationUtils.loadAnimation(context, R.anim.click_anim);
//                view.startAnimation(myAnimation);
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
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

        public ItemViewHolder(View itemView) {
            super(itemView);
//            textView = (TextView) itemView;
            textView = (TextView) itemView.findViewById(R.id.category_item_view);
        }

        @Override
        public void onItemSelected() {
            textView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            textView.setBackgroundColor(0);
        }
    }
}
