package com.example.sasha.singletask.choice;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.choice.categoriesRecyclerView.CategoriesAdapter;
import com.example.sasha.singletask.choice.categoriesRecyclerView.CategoriesDataSource;
import com.example.sasha.singletask.choice.categoriesRecyclerView.CategoriesItem;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.db.dataSets.CategoryDataSet;
import com.example.sasha.singletask.db.dataSets.VariantDataSet;
import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsAdapter;
import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsDataSource;
import com.example.sasha.singletask.settings.variantsRecyclerView.VariantsItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class VariantsChoiceFragment extends Fragment implements DB.GetCategoriesCallback,
        DB.GetVariantsByCategoryCallback {
    private static final Logger logger = LoggerFactory.getLogger(VariantsChoiceFragment.class);

    View view;
    CategoriesAdapter adapter;
    CategoriesDataSource dataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_variants_choice, null);

        DB.getInstance(getActivity()).setGetCategoriesCallback(this);
        DB.getInstance(getActivity()).setGetVariantsByCategoryCallback(this);

        initRecyclerView();
        DB.getInstance(getActivity()).getCategories();

        return view;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView =
                (RecyclerView) view.findViewById(R.id.categories_recycler_view);
        dataSource = new CategoriesDataSource(recyclerView);
        adapter = new CategoriesAdapter(getActivity(), dataSource, recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onReceiveCategories(List<CategoryDataSet> categories) {
        logger.debug("onReceiveCategories()");

        if (isAdded()) {
            for (CategoryDataSet category : categories) {
                dataSource.addItem(new CategoriesItem(category.getName(),
                        getString(R.string.empty_variant_string), category.getId()));
            }
        }
    }

    @Override
    public void onReceiveVariantsByCategory(List<VariantDataSet> variants, final int position) {

        logger.debug("onReceiveVariantsByCategory()");

        final String[] variantsNames = new String[variants.size() + 1];
        final Long[] ids = new Long[variants.size() + 1];
        ids[0] = 0L;
        variantsNames[0] = getString(R.string.empty_variant_string);

        int i = 1;
        for (VariantDataSet variant : variants) {
            variantsNames[i] = variant.getName();
            ids[i++] = variant.getId();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(R.layout.number_picker_dialog);
        builder.setPositiveButton(R.string.ok_btn_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.notifyDataSetChanged();
            }
        });
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        NumberPicker numberPicker = ((NumberPicker) dialog.findViewById(R.id.numberPicker));
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(variants.size());
        numberPicker.setDisplayedValues(variantsNames);

        dataSource.getItem(position).setVariantName(getString(R.string.empty_variant_string));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dataSource.getItem(position).setVariantName(variantsNames[newVal]);
            }
        });
    }
}
