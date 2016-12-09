package com.example.sasha.singletask.settings;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.db.dataSets.CategoryDataSet;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;
import com.example.sasha.singletask.db.dataSets.VariantDataSet;
import com.example.sasha.singletask.helpers.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.sasha.singletask.R.id.lv;

public class TaskActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener,
        DB.GetTaskByIdCallback, DB.GetCategoriesCallback, DB.GetVariantByTaskAndCategoryCallback,
        DB.GetVariantsByCategoryCallback, DB.UpdateOrInsertTaskCallback {

    private static final Logger logger = LoggerFactory.getLogger(TaskActivity.class);

    private static final String TASK_KEY = "task";
    private static final String CATEGORIES_NAMES_KEY = "categoriesNames";
    private static final String VARIANTS_NAMES_KEY = "variantsNames";
    private static final String ITEMS_STRINGS_KEY = "itemsStrings";
    private static final String CATEGORIES_ID_KEY = "categoriesId";
    private static final String VARIANTS_ID_KEY = "variantsId";

    private EditText timeEditText;
    private EditText dateEditText;
    private EditText commentEditText;
    private EditText taskEditText;

    private ArrayAdapter<String> adapter;
    private ListView list;
    private ArrayList<Long> categoriesId;
    private ArrayList<String> categoriesNames;
    private ArrayList<Long> variantsId;
    private ArrayList<String> variantsNames;
    private ArrayList<String> itemsStrings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        logger.debug("onCreate()");

        taskEditText = (EditText) findViewById(R.id.nameEditText);
        initToolbar();
        setDateAndTimeListeners();
        setClearButtonsListeners();
        setDoneButtonListener();

        DB.getInstance(this).setGetTaskByIdCallback(this);
        DB.getInstance(this).setGetCategoriesCallback(this);
        DB.getInstance(this).setGetVariantByTaskAndCategoryCallback(this);
        DB.getInstance(this).setGetVariantsByCategoryCallback(this);
        DB.getInstance(this).setUpdateOrInsertTaskCallback(this);

        variantsId = new ArrayList<>();
        categoriesId = new ArrayList<>();
        if (savedInstanceState == null) {
            categoriesNames = new ArrayList<>();
            variantsNames = new ArrayList<>();
            itemsStrings = new ArrayList<>();
        } else {
            categoriesNames = savedInstanceState.getStringArrayList(CATEGORIES_NAMES_KEY);
            variantsNames = savedInstanceState.getStringArrayList(VARIANTS_NAMES_KEY);
            itemsStrings = savedInstanceState.getStringArrayList(ITEMS_STRINGS_KEY);
            for (long item : savedInstanceState.getLongArray(CATEGORIES_ID_KEY)) {
                categoriesId.add(item);
            }
            for (long item : savedInstanceState.getLongArray(VARIANTS_ID_KEY)) {
                variantsId.add(item);
            }
        }

        initList();

        DB.getInstance(this).open();
        if (getIntent().hasExtra(TASK_KEY)) {
            DB.getInstance(this).getTaskById(getIntent().getLongExtra(TASK_KEY, 0));
        }
        if (savedInstanceState == null) {
            DB.getInstance(this).getCategories();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        logger.debug("onSaveInstanceState()");

        outState.putStringArrayList(CATEGORIES_NAMES_KEY, categoriesNames);
        outState.putStringArrayList(VARIANTS_NAMES_KEY, variantsNames);
        outState.putStringArrayList(ITEMS_STRINGS_KEY, itemsStrings);
        long[] categoriesIdArray = new long[categoriesId.size()];
        long[] variantsIdArray = new long[variantsId.size()];
        for (int i = 0; i < categoriesId.size(); i++) {
            categoriesIdArray[i] = categoriesId.get(i);
            variantsIdArray[i] = variantsId.get(i);
        }
        outState.putLongArray(CATEGORIES_ID_KEY, categoriesIdArray);
        outState.putLongArray(VARIANTS_ID_KEY, variantsIdArray);
        super.onSaveInstanceState(outState);
    }

    private void initToolbar() {

        logger.debug("initToolbar()");

        Toolbar toolbar = (Toolbar) findViewById(R.id.task_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setDateAndTimeListeners() {

        logger.debug("setDateAndTimeListeners()");

        timeEditText = (EditText) findViewById(R.id.timeEditText);
        timeEditText.setText(R.string.default_time_string);
        timeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    TimePickerDialog timepickerdialog = new TimePickerDialog(TaskActivity.this,
                            TaskActivity.this, 1, 0, true);
                    timepickerdialog.setTitle("");
                    timepickerdialog.show();
                    v.clearFocus();
                }
            }
        });

        dateEditText = (EditText) findViewById(R.id.dateEditText);
        dateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar c = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            TaskActivity.this,
                            TaskActivity.this,
                            c.get(Calendar.YEAR),
                            c.get(Calendar.MONTH),
                            c.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.setTitle("");
                    datePickerDialog.show();
                    v.clearFocus();
                }
            }
        });
    }

    private void setDoneButtonListener() {

        logger.debug("setDoneButtonListener()");

        findViewById(R.id.taskDoneButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (taskEditText.getText().toString().isEmpty()) {
                    Toast.makeText(TaskActivity.this, getString(R.string.task_needs_name),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = taskEditText.getText().toString();
                int time = Utils.getTimeAsInt(timeEditText.getText().toString());
                String date = dateEditText.getText().toString();
                String comment = commentEditText.getText().toString();
                if (getIntent().hasExtra(TASK_KEY)) {
                    DB.getInstance(TaskActivity.this).updateTask(
                            getIntent().getLongExtra(TASK_KEY, 0), name, time, date, comment,
                            variantsId, categoriesId);
                } else {
                    DB.getInstance(TaskActivity.this).insertNewTask(name, time, date, comment,
                            variantsId);
                }
            }
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hours, int minutes) {

        logger.debug("onTimeSet()");

        String timeString = Utils.getTimeAsString(hours, minutes);
        if (!timeString.isEmpty()) {
            timeEditText.setText(timeString);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        logger.debug("onDateSet()");

        dateEditText.setText(year + "-" + String.valueOf(month + 1) + "-" + day);
        if (Utils.isDateEarlierThanNow(year, month + 1, day)) {
            dateEditText.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
        } else {
            dateEditText.setTextColor(ContextCompat.getColor(this, R.color.colorTextPrimaryLight));
        }
    }

    private void setClearButtonsListeners() {

        logger.debug("setClearButtonsListeners()");

        findViewById(R.id.btnClearDate).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateEditText.getText().clear();
            }
        });

        commentEditText = (EditText) findViewById(R.id.commentEditText);
        findViewById(R.id.btnClearComment).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentEditText.getText().clear();
            }
        });
    }

    private void initList() {

        logger.debug("initList()");

        list = (ListView) findViewById(lv);
        adapter = new ArrayAdapter<>(this,
                R.layout.list_item, itemsStrings);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DB.getInstance(TaskActivity.this).getVariantsByCategory(categoriesId.get(position),
                        position);
            }
        });
    }

    @Override
    public void onReceiveTaskById(TaskDataSet task) {

        logger.debug("onReceiveTaskById()");

        if (task != null) {
            taskEditText.setText(task.getName());
            taskEditText.setSelection(task.getName().length());
            timeEditText.setText(Utils.getTimeAsString(task.getTime()));
            if (task.getDate() != null) {
                dateEditText.setText(task.getDate());
                String[] dateNumbers = task.getDate().split("-");
                if (Utils.isDateEarlierThanNow(Integer.valueOf(dateNumbers[0]),
                        Integer.valueOf(dateNumbers[1]),
                        Integer.valueOf(dateNumbers[2]))) {
                    dateEditText.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
                }
            }
            if (task.getComment() != null) commentEditText.setText(task.getComment());
        }
    }

    @Override
    public void onReceiveCategories(List<CategoryDataSet> categories) {

        logger.debug("onReceiveCategories()");

        for (CategoryDataSet category : categories) {
            categoriesNames.add(category.getName());
            categoriesId.add(category.getId());
            String itemString = category.getName() + ": ";
            if (getIntent().hasExtra(TASK_KEY)) {
                long taskId = getIntent().getLongExtra(TASK_KEY, 0);
                DB.getInstance(this).getVariantByTaskAndCategory(taskId, category.getId(),
                        categoriesId.size() - 1);
            } else {
                String variantName = getString(R.string.empty_variant_string);
                itemString += variantName;
                variantsNames.add(variantName);
                variantsId.add(0L);
            }
            itemsStrings.add(itemString);
        }
    }

    @Override
    public void onReceiveVariantByTaskAndCategory(VariantDataSet variant, int position) {

        logger.debug("onReceiveVariantByTaskAndCategory()");

        String variantName;
        if (variant != null) {
            variantsId.add(variant.getId());
            variantName = variant.getName();
        } else {
            variantsId.add(0L);
            variantName = getString(R.string.empty_variant_string);
        }
        itemsStrings.set(position, itemsStrings.get(position) + variantName);
        adapter.notifyDataSetChanged();
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        itemsStrings.set(position, categoriesNames.get(position) + ": " +
                getString(R.string.empty_variant_string));
        variantsId.set(position, ids[0]);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                itemsStrings.set(position, categoriesNames.get(position) + ": "
                        + variantsNames[newVal]);
                variantsId.set(position, ids[newVal]);
            }
        });
    }

    @Override
    public void onUpdateOrInsertTaskFinished(long taskId, String taskName) {

        logger.debug("Update or insert task success");

        SharedPreferences prefs = getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("mTaskId", taskId);
        editor.putString("mTaskName", taskName);
        editor.commit();

        finish();
    }

    // TODO max lines in comment

}
