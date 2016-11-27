package com.example.sasha.singletask.choice.categoriesRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.sasha.singletask.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoriesItemViewHolder extends RecyclerView.ViewHolder {

    private static final Logger logger = LoggerFactory.getLogger(CategoriesItemViewHolder.class);

    private final TextView categoryTextView;

    public CategoriesItemViewHolder(View itemView) {
        super(itemView);
        this.categoryTextView =
                (TextView) itemView.findViewById(R.id.categories_list_item_text_view);
    }

    public void bind(final CategoriesItem category) {

        logger.debug("bind");

        categoryTextView.setText(category.getText());
    }
}
