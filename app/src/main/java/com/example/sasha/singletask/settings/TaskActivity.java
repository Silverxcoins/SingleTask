package com.example.sasha.singletask.settings;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.example.sasha.singletask.helpers.Utils;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.sasha.singletask.R.id.lv;

public class TaskActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, DB.Callback {
    private static final String TAG = "TaskActivity";

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

        taskEditText = (EditText) findViewById(R.id.nameEditText);
        initToolbar();
        setDateAndTimeListeners();
        setClearButtonsListeners();
        setDoneButtonListener();

        DB.getInstance(this).setCallback(this);

        variantsId = new ArrayList<>();
        categoriesId = new ArrayList<>();
        if (savedInstanceState == null) {
            categoriesNames = new ArrayList<>();
            variantsNames = new ArrayList<>();
            itemsStrings = new ArrayList<>();
        } else {
            categoriesNames = savedInstanceState.getStringArrayList("categoriesNames");
            variantsNames = savedInstanceState.getStringArrayList("variantsNames");
            itemsStrings = savedInstanceState.getStringArrayList("itemsStrings");
            for (long item : savedInstanceState.getLongArray("categoriesId")) {
                categoriesId.add(item);
            }
            for (long item : savedInstanceState.getLongArray("variantsId")) {
                variantsId.add(item);
            }
        }

        initList();

        DB.getInstance(this).open();
        if (getIntent().hasExtra("task")) {
            DB.getInstance(this).getTaskById(getIntent().getLongExtra("task", 0));
        }
        if (savedInstanceState == null) {
            DB.getInstance(this).getCategories();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("categoriesNames", categoriesNames);
        outState.putStringArrayList("variantsNames", variantsNames);
        outState.putStringArrayList("itemsStrings", itemsStrings);
        long[] categoriesIdArray = new long[categoriesId.size()];
        long[] variantsIdArray = new long[variantsId.size()];
        for (int i = 0; i < categoriesId.size(); i++) {
            categoriesIdArray[i] = categoriesId.get(i);
            variantsIdArray[i] = variantsId.get(i);
        }
        outState.putLongArray("categoriesId", categoriesIdArray);
        outState.putLongArray("variantsId", variantsIdArray);
        super.onSaveInstanceState(outState);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.task_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setDateAndTimeListeners() {
        timeEditText = (EditText) findViewById(R.id.timeEditText);
        timeEditText.setText("1 час");
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
                if (getIntent().hasExtra("task")) {
                    DB.getInstance(TaskActivity.this).updateTask(
                            getIntent().getLongExtra("task", 0), name, time, date, comment,
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
        String timeString = Utils.getTimeAsString(hours, minutes);
        if (!timeString.isEmpty()) {
            timeEditText.setText(timeString);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        dateEditText.setText(year + "-" + String.valueOf(month + 1) + "-" + day);
        if (Utils.isDateEarlierThanNow(year, month + 1, day)) {
            dateEditText.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
        } else {
            dateEditText.setTextColor(ContextCompat.getColor(this, R.color.colorTextPrimaryLight));
        }
    }

    private void setClearButtonsListeners() {
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
        list = (ListView) findViewById(lv);
        adapter = new ArrayAdapter<>(this,
                R.layout.list_item, itemsStrings);
        list.setAdapter(adapter);
        Utils.setListViewHeightBasedOnItems(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DB.getInstance(TaskActivity.this).getVariantsByCategory(categoriesId.get(position),
                        position);
            }
        });
    }

    @Override
    public void onOperationFinished(DB.Operation operation, Cursor result, int position) {
        Log.d(TAG,"onOperationFinished");
        if (operation == DB.Operation.GET_TASK_BY_ID) {
            if (result.moveToFirst()) {
                String taskName = result.getString(result.getColumnIndex("name"));
                int time = result.getInt(result.getColumnIndex("time"));
                String date = result.getString(result.getColumnIndex("date"));
                String comment = result.getString(result.getColumnIndex("comment"));
                taskEditText.setText(taskName);
                taskEditText.setSelection(taskName.length());
                timeEditText.setText(Utils.getTimeAsString(time));
                if (date != null) {
                    dateEditText.setText(date);
                    String[] dateNumbers = date.split("-");
                    if (Utils.isDateEarlierThanNow(Integer.valueOf(dateNumbers[0]),
                            Integer.valueOf(dateNumbers[1]),
                            Integer.valueOf(dateNumbers[2]))) {
                        dateEditText.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
                    }
                }
                if (comment != null) commentEditText.setText(comment);
            }
        } else if (operation == DB.Operation.GET_CATEGORIES) {
            if (result.moveToFirst()) {
                do {
                    String categoryName = result.getString(result.getColumnIndex("name"));
                    Long categoryId = result.getLong(result.getColumnIndex("id"));
                    categoriesNames.add(categoryName);
                    categoriesId.add(categoryId);
                    String itemString = categoryName + ": ";
                    if (getIntent().hasExtra("task")) {
                        long taskId = getIntent().getLongExtra("task", 0);
                        DB.getInstance(this).getVariantByTaskAndCategory(taskId, categoryId,
                                categoriesId.size() - 1);
                    } else {
                        String variantName = getString(R.string.empty_variant_string);
                        itemString += variantName;
                        variantsNames.add(variantName);
                        variantsId.add(0L);
                    }
                    itemsStrings.add(itemString);
                } while (result.moveToNext());
            }
            Utils.setListViewHeightBasedOnItems(list);
        } else if (operation == DB.Operation.GET_VARIANT_BY_TASK_AND_CATEGORY){
            String variantName;
            if (result.moveToFirst()) {
                variantName = result.getString(result.getColumnIndex("name"));
                variantsId.add(result.getLong(result.getColumnIndex("id")));
            } else {
                variantName = getString(R.string.empty_variant_string);
                variantsId.add(0L);
            }
            itemsStrings.set(position, itemsStrings.get(position) + variantName);
            adapter.notifyDataSetChanged();
        } else if (operation == DB.Operation.GET_VARIANTS_BY_CATEGORY) {
            showVariantsDialog(result, position);
        } else if (operation == DB.Operation.INSERT_NEW_TASK
                || operation == DB.Operation.UPDATE_TASK) {
            finish();
        }
    }

    private  void showVariantsDialog(Cursor result, final int position) {
        final String[] variants = new String[result.getCount() + 1];
        final Long[] ids = new Long[result.getCount() + 1];
        ids[0] = 0L;
        variants[0] = getString(R.string.empty_variant_string);

        if (result.moveToFirst()) {
            for (int i = 1; i < result.getCount() + 1; i++) {
                variants[i] = result.getString(result.getColumnIndex("name"));
                ids[i] = result.getLong(result.getColumnIndex("id"));
                result.moveToNext();
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.number_picker_dialog);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        numberPicker.setMaxValue(variants.length - 1);
        numberPicker.setDisplayedValues(variants);

        itemsStrings.set(position, categoriesNames.get(position) + ": " +
                getString(R.string.empty_variant_string));
        variantsId.set(position, ids[0]);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                itemsStrings.set(position, categoriesNames.get(position) + ": " + variants[newVal]);
                variantsId.set(position, ids[newVal]);
            }
        });
    }


}
