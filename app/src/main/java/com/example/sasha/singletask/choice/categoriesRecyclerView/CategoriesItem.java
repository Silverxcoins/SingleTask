package com.example.sasha.singletask.choice.categoriesRecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesItem {
    private String categoryName;
    private String variantName;
    private long categoryId;
    private long variantId;
    private List<Long> variants;

    public CategoriesItem() {}

    public CategoriesItem(String categoryName, String variantName,
                          long categoryId) {
        this.categoryName = categoryName;
        this.variantName = variantName;
        this.categoryId = categoryId;
        variants = new ArrayList<>();
        variantId = 0;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getVariantName() { return variantName; }

    public long getCategoryId() { return categoryId; }

    public long getVariantId() { return variantId; }

    public String getText() { return categoryName + ": " + variantName; }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public void setVariantIdByVariantPosition(int position) {
        this.variantId = variants.get(position);
    }

    public void addVariant(long variantId) { variants.add(variantId); }
}
