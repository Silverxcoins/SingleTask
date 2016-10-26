package com.example.sasha.singletask.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.db.dataSets.CategoryDataSet;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;
import com.example.sasha.singletask.db.dataSets.TaskVariantDataSet;
import com.example.sasha.singletask.db.dataSets.VariantDataSet;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DB {
    private static DB instance;

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    ObjectMapper mapper = new ObjectMapper();

    private static Context ctx;

    public static DB getInstance(Context ctx) {
        if (instance == null) {
            instance = new DB();
            DB.ctx = ctx;
        }
        return instance;
    }

    private DB() {}

    // открыть подключение
    public void open() {
        dbHelper = new DbHelper(ctx, ctx.getString(R.string.db_name), null, 1);
        db = dbHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (dbHelper!=null) dbHelper.close();
    }

    public void insert(String tableName, ContentValues cv) {
        db.insert(tableName, null, cv);
    }

    public String getAllTasksInJson() {
        Cursor cursor = db.query(ctx.getString(R.string.table_task_name), null, null, null, null, null, null);

        List<TaskDataSet> tasks = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                tasks.add(new TaskDataSet(cursor));
            } while (cursor.moveToNext());
        }

        try {
            return mapper.writeValueAsString(tasks);
        } catch (IOException e) {
            return null;
        }
    }

    public String getAllCategoriesInJson() {
        Cursor cursor = db.query(ctx.getString(R.string.table_category_name), null, null, null, null, null, null);

        List<CategoryDataSet> categories = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                categories.add(new CategoryDataSet(cursor));
            } while (cursor.moveToNext());
        }

        try {
            return mapper.writeValueAsString(categories);
        } catch (IOException e) {
            return null;
        }
    }

    public String getAllVariantsInJson() {
        Cursor cursor = db.query(ctx.getString(R.string.table_variant_name), null, null, null, null, null, null);

        List<VariantDataSet> variants = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                variants.add(new VariantDataSet(cursor));
            } while (cursor.moveToNext());
        }

        try {
            return mapper.writeValueAsString(variants);
        } catch (IOException e) {
            return null;
        }
    }

    public String getAllTasksVariantsInJson() {
        Cursor cursor = db.query(ctx.getString(R.string.table_task_variant_name), null, null, null, null, null, null);

        List<TaskVariantDataSet> tvs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                tvs.add(new TaskVariantDataSet(cursor));
            } while (cursor.moveToNext());
        }

        try {
            return mapper.writeValueAsString(tvs);
        } catch (IOException e) {
            return null;
        }
    }

    public void insertTasksFromJson(String json) {
        final JsonNode[] nodes;
        try {
            nodes = mapper.readValue(json, JsonNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (JsonNode node : nodes) {
            TaskDataSet task = new TaskDataSet(node);

            ContentValues cv = new ContentValues();
            cv.put("serverId", task.getId());
            cv.put("name", task.getName());
            cv.put("comment", task.getComment());
            cv.put("date", task.getDate());
            cv.put("time", task.getTime());
            cv.put("lastUpdate", task.getLastUpdate());

            insert(ctx.getString(R.string.table_task_name), cv);
        }
    }

    public void insertCategoriesFromJson(String json) {
        final JsonNode[] nodes;
        try {
            nodes = mapper.readValue(json, JsonNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (JsonNode node : nodes) {
            CategoryDataSet category = new CategoryDataSet(node);

            ContentValues cv = new ContentValues();
            cv.put("serverId", category.getId());
            cv.put("name", category.getName());
            cv.put("parent", category.getParent());
            cv.put("lastUpdate", category.getLastUpdate());

            insert(ctx.getString(R.string.table_category_name), cv);
        }
    }

    public void insertVariantsFromJson(String json) {
        final JsonNode[] nodes;
        try {
            nodes = mapper.readValue(json, JsonNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (JsonNode node : nodes) {
            VariantDataSet variant = new VariantDataSet(node);

            ContentValues cv = new ContentValues();
            cv.put("serverId", variant.getId());
            cv.put("name", variant.getName());
            cv.put("category", variant.getCategory());

            insert(ctx.getString(R.string.table_variant_name), cv);
        }
    }

    public void insertTasksVariantsFromJson(String json) {
        final JsonNode[] nodes;
        try {
            nodes = mapper.readValue(json, JsonNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (JsonNode node : nodes) {
            TaskVariantDataSet tv = new TaskVariantDataSet(node);

            ContentValues cv = new ContentValues();
            cv.put("task", tv.getTask());
            cv.put("variant", tv.getVariant());

            insert(ctx.getString(R.string.table_task_variant_name), cv);
        }
    }
}
