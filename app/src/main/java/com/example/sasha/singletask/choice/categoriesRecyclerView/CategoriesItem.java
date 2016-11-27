package com.example.sasha.singletask.choice.categoriesRecyclerView;

public class CategoriesItem {
    private String categoryName;
    private String variantName;
    private long id;

    public CategoriesItem() {}

    public CategoriesItem(String categoryName, String variantName, long id) {
        this.categoryName = categoryName;
        this.variantName = variantName;
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getVariantName() { return variantName; }

    public long getId() { return id; }

    public String getText() { return categoryName + ": " + variantName; }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }
}
