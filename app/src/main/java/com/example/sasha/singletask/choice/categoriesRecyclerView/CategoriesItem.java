package com.example.sasha.singletask.choice.categoriesRecyclerView;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CategoriesItem implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] { categoryName, variantName });
        long[] longArray = new long[variants.size() + 2];
        longArray[0] = categoryId;
        longArray[1] = variantId;
        int i = 2;
        for (long variant : variants) {
            longArray[i++] = variant;
        }
        dest.writeLongArray(longArray);
        dest.writeInt(longArray.length);
    }

    public static final Parcelable.Creator<CategoriesItem> CREATOR
            = new Parcelable.Creator<CategoriesItem>() {

        public CategoriesItem createFromParcel(Parcel in) {
            return new CategoriesItem(in);
        }

        public CategoriesItem[] newArray(int size) {
            return new CategoriesItem[size];
        }
    };

    private CategoriesItem(Parcel in) {
        String[] strings = new String[2];
        in.readStringArray(strings);
        categoryName = strings[0];
        variantName = strings[1];

        long[] longArray = new long[in.readInt()];
        in.readLongArray(longArray);
        variants = new ArrayList<>();
        for (long variant : longArray) {
            variants.add(variant);
        }
    }
}
