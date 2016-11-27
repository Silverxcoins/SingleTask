package com.example.sasha.singletask.settings.variantsRecyclerView;

import android.support.v7.widget.RecyclerView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class VariantsDataSource {

    private static final Logger logger = LoggerFactory.getLogger(VariantsDataSource.class);

    private final List<VariantsItem> variants = new ArrayList<>();
    private final RecyclerView recyclerView;

    public VariantsDataSource(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public ArrayList<String> getVariantsNames() {

        logger.debug("getVariantsNames()");

        ArrayList<String> variantsNames = new ArrayList<>();
        for (VariantsItem variant : variants) {
            variantsNames.add(variant.getVariantName());
        }
        return variantsNames;
    }

    public void setItems(ArrayList<String> variantsNames) {

        logger.debug("setItems()");

        for (String variantName : variantsNames) {
            addItem(new VariantsItem(variantName));
        }
    }

    public void updateItem(int position, String variantName) {

        logger.debug("updateItem()");

        variants.get(position).setVariantName(variantName);
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    public int getCount() {

        logger.debug("getCount()");

        return variants.size();
    }

    public VariantsItem getItem(int position) {

        logger.debug("getItem()");

        return variants.get(position);
    }

    private int getPosition(VariantsItem variant) {

        logger.debug("getPosition()");

        return variants.indexOf(variant);
    }

    public void addItem(VariantsItem item) {

        logger.debug("addItem()");

        variants.add(item);
        recyclerView.getAdapter().notifyItemInserted(variants.size());
    }

    private void remove(int position) {

        logger.debug("remove(int position)");

        if (!variants.isEmpty()) {
            variants.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
        }
    }

    public void remove(VariantsItem variant) {

        logger.debug("remove(VariantsItem variant)");

        if (variants.contains(variant)) {
            remove(getPosition(variant));
        }
    }
}
