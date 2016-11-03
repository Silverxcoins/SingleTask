package com.example.sasha.singletask.settings;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.settings.VariantsRecyclerView.VariantsAdapter;
import com.example.sasha.singletask.settings.VariantsRecyclerView.VariantsDataSource;
import com.example.sasha.singletask.settings.VariantsRecyclerView.VariantsItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements DB.Callback {

    private static final Logger logger = LoggerFactory.getLogger(CategoryActivity.class);

    private static final String CATEGORY_KEY = "category";
    private static final String VARIANTS_KEY = "variants";
    private static final String TEXT_IN_FOCUS_KEY = "textInFocus";
    private static final String NAME_KEY = "name";

    private VariantsDataSource dataSource;
    private EditText categoryNameEditText;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        logger.debug("onCreate()");

        categoryNameEditText = (EditText) findViewById(R.id.nameEditText);
        initToolbar();
        initVariantsRecyclerView();
        setDoneButtonListener();

        DB.getInstance(this).setCallback(this);
        DB.getInstance(this).open();
        if (savedInstanceState == null) {
            if (getIntent().hasExtra(CATEGORY_KEY)) {
                DB.getInstance(this).getCategoryById(getIntent().getLongExtra(CATEGORY_KEY, 0));
            }
        } else {
            ArrayList<String> variantsNames = savedInstanceState.getStringArrayList(VARIANTS_KEY);
            if (savedInstanceState.containsKey(TEXT_IN_FOCUS_KEY)) {
                variantsNames.set(variantsNames.size() - 1,
                        savedInstanceState.getString(TEXT_IN_FOCUS_KEY));
            }
            dataSource.setItems(savedInstanceState.getStringArrayList(VARIANTS_KEY));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        logger.debug("onSaveInstanceState()");

        outState.putStringArrayList(VARIANTS_KEY, dataSource.getVariantsNames());
        if (getCurrentFocus() instanceof EditText && getCurrentFocus() != categoryNameEditText
                && !((EditText) getCurrentFocus()).getText().toString().isEmpty()) {
            outState.putString(
                    TEXT_IN_FOCUS_KEY,
                    ((EditText) getCurrentFocus()).getText().toString()
            );
        }
        super.onSaveInstanceState(outState);
    }

    private void initToolbar() {

        logger.debug("initToolbar()");

        Toolbar toolbar = (Toolbar) findViewById(R.id.category_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initVariantsRecyclerView() {

        logger.debug("initVariantsRecyclerView()");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.variantsRecyclerView);
        dataSource = new VariantsDataSource(recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        VariantsAdapter adapter = new VariantsAdapter(this, dataSource);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        (findViewById(R.id.btnAddVariant)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewInFocus = getCurrentFocus();
                if (dataSource.getCount() == 0
                        || viewInFocus == null
                        || viewInFocus == categoryNameEditText
                        || !((EditText) viewInFocus).getText().toString().isEmpty()) {
                    dataSource.addItem(new VariantsItem());
                    layoutManager.scrollToPosition(dataSource.getCount() - 1);
                } else {
                    viewInFocus.setEnabled(true);
                    viewInFocus.requestFocus();
                }
            }
        });
    }

    private void setDoneButtonListener() {

        logger.debug("setDoneButtonListener()");

        findViewById(R.id.btnDoneCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(CategoryActivity.this, R.string.category_need_name,
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getCurrentFocus() instanceof EditText
                        && getCurrentFocus() != categoryNameEditText) {
                    dataSource.updateItem(
                            dataSource.getCount() - 1,
                            ((EditText) getCurrentFocus()).getText().toString()
                    );
                    getCurrentFocus().clearFocus();
                }

                if (getIntent().hasExtra(CATEGORY_KEY)) {
                    DB.getInstance(CategoryActivity.this).updateCategory(
                            getIntent().getLongExtra(CATEGORY_KEY, 0),
                            categoryNameEditText.getText().toString(),
                            dataSource.getVariantsNames());
                } else {
                    DB.getInstance(CategoryActivity.this).insertNewCategory(
                            categoryNameEditText.getText().toString(),
                            dataSource.getVariantsNames()
                    );
                }
            }
        });
    }

    @Override
    public void onOperationFinished(DB.Operation operation, Cursor result, int position) {

        logger.debug("onOperationFinished() Operation: {}", operation.name());

        if (operation == DB.Operation.GET_CATEGORY_BY_ID) {

            String categoryName = "";
            if (result.moveToFirst()) {
                categoryName = result.getString(result.getColumnIndex(NAME_KEY));
            }
            categoryNameEditText.setText(categoryName);
            DB.getInstance(this).getVariantsByCategory(getIntent().getLongExtra(CATEGORY_KEY, 0), 0);

        } else if (operation == DB.Operation.GET_VARIANTS_BY_CATEGORY) {

            if (result.moveToFirst()) {
                do {
                    String variantName = result.getString(result.getColumnIndex(NAME_KEY));
                    dataSource.addItem(new VariantsItem(variantName));
                } while (result.moveToNext());
            }

        } else if (operation == DB.Operation.INSERT_NEW_CATEGORY) {

            logger.info("Add new category success");
            finish();

        } else if(operation == DB.Operation.UPDATE_CATEGORY) {

            logger.info("Update category success");
            finish();

        }
    }
}

