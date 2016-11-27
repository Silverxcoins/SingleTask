package com.example.sasha.singletask.choice.categoriesRecyclerView;

public class CategoriesItem {
    private String categoryName;

    public CategoriesItem() {}

    public CategoriesItem(String variantName) {
        this.categoryName = variantName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
