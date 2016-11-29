package com.example.sasha.singletask.choice.categoriesRecyclerView;

import android.support.v7.widget.RecyclerView;

import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsDataSource;
import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CategoriesDataSource {
    private static final Logger logger = LoggerFactory.getLogger(VariantsDataSource.class);

    private final List<CategoriesItem> categories = new ArrayList<>();
    private final RecyclerView recyclerView;

    public CategoriesDataSource(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public ArrayList<String> getStrings() {

        logger.debug("getStrings()");

        ArrayList<String> strings = new ArrayList<>();
        for (CategoriesItem category : categories) {
            strings.add(category.getText());
        }
        return strings;
    }

    public List<CategoriesItem> getCategories() { return categories; }

    public void updateVariant(int position, String variantName) {

        logger.debug("updateVariant()");

        categories.get(position).setVariantName(variantName);
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    public int getCount() {

        logger.debug("getCount()");

        return categories.size();
    }

    public CategoriesItem getItem(int position) {

        logger.debug("getItem()");

        return categories.get(position);
    }

    private int getPosition(CategoriesItem category) {

        logger.debug("getPosition()");

        return categories.indexOf(category);
    }

    public void addItem(CategoriesItem item) {

        logger.debug("addItem()");

        categories.add(item);
        recyclerView.getAdapter().notifyItemInserted(categories.size());
    }
}
