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

    private ObjectMapper mapper = new ObjectMapper();

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

//        db.delete("Task", null, null);
//        db.delete("Category", null, null);
//        db.delete("Variant", null, null);
//        db.delete("VariantTask", null, null);
//
//        ContentValues cv = new ContentValues();
//        cv.put("name", "firstTask");
//        cv.put("comment", "firstTaskComment");
//        cv.put("time", 90);
//        cv.put("lastUpdate", "2002-12-12 01:01:01");
//        db.insert("Task", null, cv);
//
//        Cursor cursor = db.rawQuery("SELECT MAX(id) AS q FROM Task", new String[] {});
//        cursor.moveToFirst();
//        long firstTaskId = cursor.getLong(cursor.getColumnIndex("q"));
//
//        cv = new ContentValues();
//        cv.put("name", "secondTask");
//        cv.put("date", "2006-12-12 01:01:01");
//        cv.put("time", 60);
//        cv.put("lastUpdate", "2003-12-12 01:01:01");
//        db.insert("Task", null, cv);
//
//        cursor = db.rawQuery("SELECT MAX(id) AS q FROM Task", new String[] {});
//        cursor.moveToFirst();
//        long secondTaskId = cursor.getLong(cursor.getColumnIndex("q"));
//
//        cv = new ContentValues();
//        cv.put("name", "firstCategory");
//        cv.put("parent", 0);
//        cv.put("lastUpdate", "2002-12-12 01:01:01");
//        db.insert("Category", null, cv);
//        cursor = db.rawQuery("SELECT MAX(id) AS q FROM Category", new String[] {});
//        cursor.moveToFirst();
//        long catId = cursor.getInt(cursor.getColumnIndex("q"));
//        System.out.println("!!!!!!!!!!!! " + catId);
//
//        cv = new ContentValues();
//        cv.put("name", "secondCategory");
//        cv.put("parent", catId);
//        cv.put("lastUpdate", "2002-12-12 01:01:01");
//        db.insert("Category", null, cv);
//        cursor = db.rawQuery("SELECT MAX(id) AS q FROM Category", new String[] {});
//        cursor.moveToFirst();
//        long cat2Id = cursor.getLong(cursor.getColumnIndex("q"));
//
//        cv = new ContentValues();
//        cv.put("name", "firstVariant");
//        cv.put("category", catId);
//        db.insert("Variant", null, cv);
//        cursor = db.rawQuery("SELECT MAX(id) AS q FROM Variant", new String[] {});
//        cursor.moveToFirst();
//        long varId = cursor.getLong(cursor.getColumnIndex("q"));
//
//        cv = new ContentValues();
//        cv.put("name", "secondVariant");
//        cv.put("category", cat2Id);
//        db.insert("Variant", null, cv);
//        cursor = db.rawQuery("SELECT MAX(id) AS q FROM Variant", new String[] {});
//        cursor.moveToFirst();
//        long var2Id = cursor.getLong(cursor.getColumnIndex("q"));
//
//        cv = new ContentValues();
//        cv.put("task", firstTaskId);
//        cv.put("variant", varId);
//        db.insert("VariantTask", null, cv);
//
//        cv = new ContentValues();
//        cv.put("task", firstTaskId);
//        cv.put("variant", var2Id);
//        db.insert("VariantTask", null, cv);

    }

    // закрыть подключение
    public void close() {
        if (dbHelper!=null) dbHelper.close();
    }

    private long insert(String tableName, ContentValues cv) {
        return db.insert(tableName, null, cv);
    }

    private Cursor selectByServerId(String tableName, int serverId) {
        String selection = "serverId = ?";
        String[] selectionArgs = { String.valueOf(serverId) };
        return db.query(tableName, null, selection, selectionArgs, null, null, null);
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
            System.out.println("ПОЛУЧИЛИ ТАСКИ ИЗ ДБ" + mapper.writeValueAsString(tasks));
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
            System.out.println("ПОЛУЧИЛИ КАТЕГОРИИ ИЗ ДБ" + mapper.writeValueAsString(categories));
            return mapper.writeValueAsString(categories);
        } catch (IOException e) {
            return null;
        }
    }

    public String getAllVariantsInJson(List<CategoryDataSet> categories) {
        Cursor cursor = db.query(ctx.getString(R.string.table_variant_name), null, null, null, null, null, null);

        List<VariantDataSet> variants = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                VariantDataSet variant = new VariantDataSet(cursor);
                boolean isCategoryExist = false;
                for (CategoryDataSet category : categories) {
                    if (category.getOldId() == null) continue;
                    if (category.getOldId() == variant.getCategory()) {
                        variant.setCategory(category.getServerId());
                        isCategoryExist = true;
                        break;
                    }
                }
                if (isCategoryExist) {
                    variants.add(variant);
                }
            } while (cursor.moveToNext());
        }

        try {
            System.out.println("ПОЛУЧИЛИ ВАРИАНТЫ ИЗ ДБ" + mapper.writeValueAsString(variants));
            return mapper.writeValueAsString(variants);
        } catch (IOException e) {
            return null;
        }
    }

    public String getAllTasksVariantsInJson(List<TaskDataSet> tasks,
                                            List<VariantDataSet> variants) {
        Cursor cursor = db.query(ctx.getString(R.string.table_task_variant_name), null, null, null, null, null, null);

        List<TaskVariantDataSet> tvs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                TaskVariantDataSet tv = new TaskVariantDataSet(cursor);
                boolean isTaskExist = false;
                boolean isVariantExist = false;
                for (TaskDataSet task : tasks) {
                    if (task.getOldId() == null) continue;
                    if (task.getOldId() == tv.getTask()) {
                        tv.setTask(task.getServerId());
                        isTaskExist = true;
                        break;
                    }
                }
                for (VariantDataSet variant : variants) {
                    if (variant.getOldId() == null) continue;
                    if (variant.getOldId() == tv.getVariant()) {
                        tv.setVariant(variant.getServerId());
                        isVariantExist = true;
                        break;
                    }
                }
                if (isTaskExist && isVariantExist) {
                    tvs.add(tv);
                }
            } while (cursor.moveToNext());
        }

        try {
            System.out.println("ПОЛУЧИЛИ ТАСКОВАРИАНТЫ ИЗ ДБ" + mapper.writeValueAsString(tvs));
            return mapper.writeValueAsString(tvs);
        } catch (IOException e) {
            return null;
        }
    }

    public List<TaskDataSet> insertTasksFromJson(String json) {
        System.out.println("ПОЛУЧИЛИ ТАСКИ С СЕРВЕРА" + json);
        db.delete(ctx.getString(R.string.table_task_name), null, null);

        final JsonNode mainNode;
        final JsonNode[] nodes;
        try {
            mainNode = mapper.readValue(json, JsonNode.class);
            nodes = mapper.readValue(mainNode.get("response"), JsonNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<TaskDataSet> tasks = new ArrayList<>();
        for (JsonNode node : nodes) {
            TaskDataSet task = new TaskDataSet(node);
            tasks.add(task);

            ContentValues cv = new ContentValues();
            cv.put("serverId", task.getId());
            cv.put("name", task.getName());
            cv.put("comment", task.getComment());
            cv.put("date", task.getDate());
            cv.put("time", task.getTime());
            cv.put("lastUpdate", task.getLastUpdate());

            insert(ctx.getString(R.string.table_task_name), cv);
        }
        return tasks;
    }

    public List<CategoryDataSet> insertCategoriesFromJson(String json) {
        System.out.println("ПОЛУЧИЛИ КАТЕГОРИИ С СЕРВЕРА" + json);
        db.delete(ctx.getString(R.string.table_category_name), null, null);

        final JsonNode mainNode;
        final JsonNode[] nodes;
        try {
            mainNode = mapper.readValue(json, JsonNode.class);
            nodes = mapper.readValue(mainNode.get("response"), JsonNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<CategoryDataSet> categories = new ArrayList<>();
        for (JsonNode node : nodes) {
            CategoryDataSet category = new CategoryDataSet(node);
            categories.add(category);
        }

        List<CategoryDataSet> inserted = new ArrayList<>();
        for (CategoryDataSet category : categories) {
            if (inserted.contains(category)) continue;
            if (category.getParent() > 0) {
                for (CategoryDataSet c : categories) {
                    if (c.getServerId() == category.getParent()) {
                        if (inserted.contains(c)) {
                            category.setParent(c.getId());
                        } else {
                            category.setParent((int) insertCategory(c));
                            inserted.add(c);
                        }
                        break;
                    }
                }
            }
            insertCategory(category);
            inserted.add(category);
        }
        return categories;
    }

    private long insertCategory(CategoryDataSet category) {
        ContentValues cv = new ContentValues();
        cv.put("serverId", category.getId());
        cv.put("name", category.getName());
        cv.put("parent", category.getParent());
        cv.put("lastUpdate", category.getLastUpdate());

        long id = insert(ctx.getString(R.string.table_category_name), cv);
        category.setId((int)id);
        return id;
    }

    public List<VariantDataSet> insertVariantsFromJson(String json) {
        System.out.println("ПОЛУЧИЛИ ВАРИАНТЫ С СЕРВЕРА" + json);
        db.delete(ctx.getString(R.string.table_variant_name), null, null);

        final JsonNode mainNode;
        final JsonNode[] nodes;
        try {
            mainNode = mapper.readValue(json, JsonNode.class);
            nodes = mapper.readValue(mainNode.get("response"), JsonNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<VariantDataSet> variants = new ArrayList<>();
        for (JsonNode node : nodes) {
            VariantDataSet variant = new VariantDataSet(node);
            variants.add(variant);

            Cursor categoryCursor = selectByServerId(ctx.getString(R.string.table_category_name), variant.getCategory());
            if (categoryCursor.moveToFirst()) {
                int id = categoryCursor.getInt(categoryCursor.getColumnIndex("id"));
                variant.setCategory(id);
            } else {
                continue;
            }

            ContentValues cv = new ContentValues();
            cv.put("serverId", variant.getId());
            cv.put("name", variant.getName());
            cv.put("category", variant.getCategory());

            insert(ctx.getString(R.string.table_variant_name), cv);
        }

        return variants;
    }

    public void insertTasksVariantsFromJson(String json) {
        System.out.println("ПОЛУЧИЛИ ТАСКОВАРИАНТЫ С СЕРВЕРА" + json);
        db.delete(ctx.getString(R.string.table_task_variant_name), null, null);

        final JsonNode mainNode;
        final JsonNode[] nodes;
        try {
            mainNode = mapper.readValue(json, JsonNode.class);
            nodes = mapper.readValue(mainNode.get("response"), JsonNode[].class);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (JsonNode node : nodes) {
            TaskVariantDataSet tv = new TaskVariantDataSet(node);

            Cursor taskCursor = selectByServerId(ctx.getString(R.string.table_task_name), tv.getTask());
            Cursor variantCursor = selectByServerId(ctx.getString(R.string.table_variant_name), tv.getVariant());
            if (taskCursor.moveToFirst() && variantCursor.moveToFirst()) {
                int task = taskCursor.getInt(taskCursor.getColumnIndex("id"));
                tv.setTask(task);
                int variant = variantCursor.getInt(variantCursor.getColumnIndex("id"));
                tv.setVariant(variant);
            } else {
                return;
            }

            ContentValues cv = new ContentValues();
            cv.put("task", tv.getTask());
            cv.put("variant", tv.getVariant());

            insert(ctx.getString(R.string.table_task_variant_name), cv);
        }
    }
}
