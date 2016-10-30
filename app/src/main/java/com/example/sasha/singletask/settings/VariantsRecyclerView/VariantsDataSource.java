package com.example.sasha.singletask.settings.VariantsRecyclerView;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VariantsDataSource {
    private final List<VariantsItem> variants = new ArrayList<>();

    private RecyclerView recyclerView;

    public VariantsDataSource(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public ArrayList<String> getVariantsNames() {
        ArrayList<String> variantsNames = new ArrayList<>();
        for (VariantsItem variant : variants) {
            variantsNames.add(variant.getVariantName());
        }
        return variantsNames;
    }

    public void setItems(ArrayList<String> variantsNames) {
        for (String variantName : variantsNames) {
            addItem(new VariantsItem(variantName));
        }
    }

    public void updateItem(int position, String variantName) {
        variants.get(position).setVariantName(variantName);
        recyclerView.getAdapter().notifyItemChanged(position);
    }

    public int getCount() {
        return variants.size();
    }

    public VariantsItem getItem(int position) {
        return variants.get(position);
    }

    private int getPosition(VariantsItem variant) {
        return variants.indexOf(variant);
    }

    public void addItem(VariantsItem item) {
        variants.add(item);
        recyclerView.getAdapter().notifyItemInserted(variants.size());
    }

    private void remove(int position) {
        if (!variants.isEmpty()) {
            variants.remove(position);
            recyclerView.getAdapter().notifyItemRemoved(position);
        }
    }

    public void remove(VariantsItem variant) {
        if (variants.contains(variant)) {
            remove(getPosition(variant));
        }
    }
}
