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
import com.example.sasha.singletask.helpers.Ui;
import com.example.sasha.singletask.helpers.Utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DB {

    private static final Logger logger = LoggerFactory.getLogger(DB.class);

    private static DB instance;

    public enum Operation {
        GET_CATEGORIES,
        GET_VARIANT_BY_TASK_AND_CATEGORY,
        GET_VARIANTS_BY_CATEGORY,
        GET_TASK_BY_ID,
        INSERT_NEW_TASK,
        UPDATE_TASK,
        GET_TASKS,
        GET_CATEGORY_BY_ID,
        INSERT_NEW_CATEGORY,
        UPDATE_CATEGORY
    }

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    private final ObjectMapper mapper = new ObjectMapper();

    private static Context ctx;

    public static DB getInstance(Context ctx) {
        if (instance == null) {
            instance = new DB();
            DB.ctx = ctx;
        }
        return instance;
    }

    private DB() {}

    public interface Callback {
        void onOperationFinished(Operation operation, Cursor result, int position);
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    private DB.Callback callback;

    public void setCallback(DB.Callback callback) {

        logger.debug("setCallback()");

        this.callback = callback;
    }

    private void notifyOperationFinished(final Operation operation, final Cursor result,
                                         final int position) {

        logger.debug("notifyOperationFinished()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onOperationFinished(operation, result, position);
                }
            }
        });
    }


    public void open() {

        logger.debug("open()");

        dbHelper = new DbHelper(ctx, ctx.getString(R.string.db_name), null, 1);
        db = dbHelper.getWritableDatabase();
        logger.info("Db opened");
    }

    public void close() {

        logger.debug("close()");

        if (dbHelper!=null) dbHelper.close();
        logger.info("Db closed");
    }

    private Cursor selectByServerId(String tableName, int serverId) {

        logger.debug("selectByServerId()");

        String selection = "serverId = ?";
        String[] selectionArgs = { String.valueOf(serverId) };
        return db.query(tableName, null, selection, selectionArgs, null, null, null);
    }

    public String getAllTasksInJson() {

        logger.debug("getAllTasksInJson()");

        Cursor cursor = db.query(ctx.getString(R.string.table_task_name),
                null, null, null, null, null, null);

        List<TaskDataSet> tasks = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                tasks.add(new TaskDataSet(cursor));
            } while (cursor.moveToNext());
        }

        try {
            logger.info("Tasks received from db : {}", mapper.writeValueAsString(tasks));
            return mapper.writeValueAsString(tasks);
        } catch (IOException e) {
            return null;
        }
    }

    public String getAllCategoriesInJson() {

        logger.debug("getAllCategoriesInJson()");

        Cursor cursor = db.query(ctx.getString(R.string.table_category_name),
                null, null, null, null, null, null);

        List<CategoryDataSet> categories = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                categories.add(new CategoryDataSet(cursor));
            } while (cursor.moveToNext());
        }

        try {
            logger.info("Categories received from db: {}", mapper.writeValueAsString(categories));
            return mapper.writeValueAsString(categories);
        } catch (IOException e) {
            return null;
        }
    }

    public String getAllVariantsInJson(List<CategoryDataSet> categories) {

        logger.debug("getAllVariantsInJson()");

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
            logger.info("Variants received from db: {}", mapper.writeValueAsString(variants));
            return mapper.writeValueAsString(variants);
        } catch (IOException e) {
            return null;
        }
    }

    public String getAllTasksVariantsInJson(List<TaskDataSet> tasks,
                                            List<VariantDataSet> variants) {

        logger.debug("getAllTasksVariantsInJson()");

        Cursor cursor = db.query(ctx.getString(R.string.table_task_variant_name),
                null, null, null, null, null, null);

        List<TaskVariantDataSet> tvs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                TaskVariantDataSet tv = new TaskVariantDataSet(cursor);
                boolean isTaskExist = false;
                boolean isVariantExist = false;

                for (TaskDataSet task : tasks) {
                    if (task.getOldId() != null && task.getOldId() == tv.getTask()) {
                        tv.setTask(task.getServerId());
                        isTaskExist = true;
                        break;
                    }
                }
                for (VariantDataSet variant : variants) {
                    if (variant.getOldId() != null && variant.getOldId() == tv.getVariant()) {
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
            logger.info("TasksVariants received from db: {}", mapper.writeValueAsString(tvs));
            return mapper.writeValueAsString(tvs);
        } catch (IOException e) {
            return null;
        }
    }

    public List<TaskDataSet> insertTasksFromJson(String json) {

        logger.debug("insertTasksFromJson()");
        logger.info("Tasks received from server: {}", json);

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
            cv.put("serverId", task.getServerId());
            cv.put("name", task.getName());
            cv.put("comment", task.getComment());
            cv.put("date", task.getDate());
            cv.put("time", task.getTime());
            cv.put("lastUpdate", task.getLastUpdate());
            cv.put("isUpdated", 0);
            cv.put("isDeleted", 0);

            db.insert(ctx.getString(R.string.table_task_name), null, cv);
        }
        return tasks;
    }

    public List<CategoryDataSet> insertCategoriesFromJson(String json) {

        logger.debug("insertCategoriesFromJson()");
        logger.info("Categories received from server: {}", json);


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

        logger.debug("insertCategory()");

        ContentValues cv = new ContentValues();
        cv.put("serverId", category.getServerId());
        cv.put("name", category.getName());
        cv.put("parent", category.getParent());
        cv.put("lastUpdate", category.getLastUpdate());
        cv.put("isUpdated", 0);
        cv.put("isDeleted", 0);

        long id = db.insert(ctx.getString(R.string.table_category_name), null, cv);
        category.setId((int)id);
        return id;
    }

    public List<VariantDataSet> insertVariantsFromJson(String json) {

        logger.debug("insertVariantsFromJson()");
        logger.info("Variants received from server: {}", json);

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
            cv.put("serverId", variant.getServerId());
            cv.put("name", variant.getName());
            cv.put("category", variant.getCategory());
            cv.put("isDeleted", 0);


            db.insert(ctx.getString(R.string.table_variant_name), null, cv);
        }

        return variants;
    }

    public void insertTasksVariantsFromJson(String json) {

        logger.debug("insertTasksVariantsFromJson()");
        logger.info("TasksVariants received from server: {}", json);

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
            cv.put("isDeleted", 0);

            db.insert(ctx.getString(R.string.table_task_variant_name), null, cv);
        }
    }

    public void getTasks() {

        logger.debug("getTasks()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor tasks = getTasksFromDb();
                notifyOperationFinished(Operation.GET_TASKS, tasks, 0);
            }
        });
    }

    private Cursor getTasksFromDb() {

        logger.debug("getTasksFromDb()");

        String selection = "isDeleted IS NULL OR isDeleted=0";
        return db.query(ctx.getString(R.string.table_task_name), null, selection,
                null, null, null, null);
    }

    public void getCategories() {

        logger.debug("getCategories()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor categories = getCategoriesFromDb();
                notifyOperationFinished(Operation.GET_CATEGORIES, categories, 0);
            }
        });
    }

    private Cursor getCategoriesFromDb() {

        logger.debug("getCategoriesFromDb()");

        String selection = "isDeleted IS NULL OR isDeleted=0";
        return db.query(ctx.getString(R.string.table_category_name), null, selection,
                null, null, null, null);
    }

    public void getVariantsByCategory(final Long category, final int position) {

        logger.debug("getVariantsByCategory()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor variants = getVariantsByCategoryFromDb(category, false);
                notifyOperationFinished(Operation.GET_VARIANTS_BY_CATEGORY, variants, position);
            }
        });
    }

    private Cursor getVariantsByCategoryFromDb(Long category, boolean withDeleted) {

        logger.debug("getVariantsByCategoryFromDB()");

        String selection = "category=?";
        if (!withDeleted) {
            selection += " AND (isDeleted IS NULL OR isDeleted=0)";
        }
        String[] selectionArgs = { String.valueOf(category) };
        return db.query(ctx.getString(R.string.table_variant_name), null, selection, selectionArgs,
                null, null, null);
    }

    public void getVariantByTaskAndCategory(final long task, final Long category,
                                            final int position) {

        logger.debug("getVariantByTaskAndCategory()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor variant = getVariantByTaskAndCategoryFromDb(task, category);
                notifyOperationFinished(Operation.GET_VARIANT_BY_TASK_AND_CATEGORY, variant,
                        position);
            }
        });
    }

    private Cursor getVariantByTaskAndCategoryFromDb(long task, Long category) {

        logger.debug("getVariantsByTaskAndCategoryFromDb()");

        String[] selectionArgs = { String.valueOf(task), String.valueOf(category) };
        return db.rawQuery(SqlQueries.GET_VARIANT_BY_TASK_AND_CATEGORY, selectionArgs);
    }

    public void getTaskById(final long id) {

        logger.debug("getTaskById()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor task = getTaskByIdFromDb(id);
                notifyOperationFinished(Operation.GET_TASK_BY_ID, task, 0);
            }
        });

    }

    private Cursor getTaskByIdFromDb(long id) {

        logger.debug("getTaskByIdFromDb()");

        String selection = "id=?";
        String[] selectionArgs = { String.valueOf(id) };
        return db.query(ctx.getString(R.string.table_task_name), null, selection, selectionArgs, null, null, null);
    }

    public void insertNewTask(final String name, final int time, final String date,
                              final String comment, final ArrayList<Long> variants) {

        logger.debug("insertNewTask()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                insertNewTaskInDb(name, time, date, comment, variants);
                notifyOperationFinished(Operation.INSERT_NEW_TASK, null, 0);
            }
        });
    }

    private void insertNewTaskInDb(String name, int time, String date, String comment,
                                   ArrayList<Long> variants) {

        logger.debug("insertNewTaskInDb()");

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("time", time);
        cv.put("date", !date.isEmpty() ? date : null);
        cv.put("comment", !comment.isEmpty() ? comment : null);
        cv.put("isUpdated", 1);
        cv.put("isDeleted", 0);
        cv.put("lastUpdate", Utils.getCurrentTimeAsString());
        long taskId = db.insert(ctx.getString(R.string.table_task_name), null, cv);

        for (Long variantId : variants) {
            if (variantId != 0) {
                cv = new ContentValues();
                cv.put("task", taskId);
                cv.put("variant", variantId);
                db.insert(ctx.getString(R.string.table_task_variant_name), null, cv);
            }
        }
    }

    public void updateTask(final long id, final String name, final int time, final String date,
                           final String comment, final ArrayList<Long> variants,
                           final ArrayList<Long> categories) {

        logger.debug("updateTask()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                updateTaskInDb(id, name, time, date, comment, variants, categories);
                notifyOperationFinished(Operation.UPDATE_TASK, null, 0);
            }
        });
    }

    private void updateTaskInDb(long taskId, String name, int time, String date, String comment,
                                   ArrayList<Long> variants, ArrayList<Long> categories) {

        logger.debug("updateTaskInDb()");

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("time", time);
        cv.put("date", !date.isEmpty() ? date : null);
        cv.put("comment", !comment.isEmpty() ? comment : null);
        cv.put("isUpdated", 1);
        cv.put("lastUpdate", Utils.getCurrentTimeAsString());

        String selection = "id=?";
        String[] selectionArgs = { String.valueOf(taskId) };
        db.update(ctx.getString(R.string.table_task_name), cv, selection, selectionArgs);

        for (int i = 0; i < variants.size(); i++) {
            Long variantId = variants.get(i);
            Long categoryId = categories.get(i);
            Cursor c = selectTaskVariantByTaskAndCategoryFromDb(taskId, categoryId);
            boolean doesExist = false;
            if (c.moveToFirst()) {
                do {
                    long variantFromDb = c.getLong(c.getColumnIndex("variant"));
                    boolean isDeleted = (c.getInt(c.getColumnIndex("isDeleted")) == 1);
                    if (variantFromDb == variantId) {
                        doesExist = true;
                        if (isDeleted) {
                            updateTaskVariantInDb(taskId, variantId, false);
                        }
                    } else {
                        if (!isDeleted) {
                            updateTaskVariantInDb(taskId, variantFromDb, true);
                        }
                    }
                } while (c.moveToNext());
            }
            if (!doesExist && variantId != 0) {
                cv = new ContentValues();
                cv.put("task", taskId);
                cv.put("variant", variantId);
                cv.put("isDeleted", 0);
                db.insert(ctx.getString(R.string.table_task_variant_name), null, cv);
            }
        }
    }

    private Cursor selectTaskVariantByTaskAndCategoryFromDb(long task, long category) {

        logger.debug("selectTaskVariantByTaskAndCategoryFromDb()");

        String[] tVselectionArgs = { String.valueOf(task), String.valueOf(category) };
        return db.rawQuery(SqlQueries.GET_TASKS_VARIANTS_BY_TASK_AND_CATEGORY, tVselectionArgs);
    }

    private void updateTaskVariantInDb(long task, long variant, boolean isDeleted) {

        logger.debug("updateTaskVariantInDb()");

        ContentValues cv = new ContentValues();
        cv.put("isDeleted", (isDeleted) ? 1 : 0);
        String selection = "task=? AND variant=?";
        String[] selectionArgs = { String.valueOf(task), String.valueOf(variant) };
        db.update(ctx.getString(R.string.table_task_variant_name), cv, selection, selectionArgs);
    }

    public void insertNewCategory(final String categoryName, final List<String> variantsNames) {

        logger.debug("insertNewCategory()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                insertNewCategoryInDb(categoryName, variantsNames);
                notifyOperationFinished(Operation.INSERT_NEW_CATEGORY, null, 0);
            }
        });
    }

    private void insertNewCategoryInDb(String categoryName, List<String> variantsNames) {

        logger.debug("insertNewCategoryInDb()");

        ContentValues cv = new ContentValues();
        cv.put("name", categoryName);
        cv.put("isUpdated", 1);
        cv.put("isDeleted", 0);
        cv.put("lastUpdate", Utils.getCurrentTimeAsString());
        long categoryId = db.insert(ctx.getString(R.string.table_category_name), null, cv);

        for (String variantName : variantsNames) {
            cv = new ContentValues();
            cv.put("name", variantName);
            cv.put("category", categoryId);
            cv.put("isDeleted", 0);
            db.insert(ctx.getString(R.string.table_variant_name), null, cv);
        }
    }

    public void updateCategory(final long categoryId, final String categoryName,
                               final List<String> variantsNames) {

        logger.debug("updateCategory()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                updateCategoryInDb(categoryId, categoryName, variantsNames);
                notifyOperationFinished(Operation.UPDATE_CATEGORY, null, 0);
            }
        });
    }

    private void updateCategoryInDb(long categoryId, String categoryName,
                                    List<String> variantsNames) {

        logger.debug("updateCategoryInDb()");

        ContentValues cv = new ContentValues();
        cv.put("name", categoryName);
        cv.put("isUpdated", 1);
        cv.put("isDeleted", 0);
        cv.put("lastUpdate", Utils.getCurrentTimeAsString());

        String selection = "id=?";
        String[] selectionArgs = { String.valueOf(categoryId) };
        db.update(ctx.getString(R.string.table_category_name), cv, selection, selectionArgs);

        Cursor cursor = getVariantsByCategoryFromDb(categoryId, true);
        for (String variantName : variantsNames) {
            boolean isInDb = false;
            if (cursor.moveToFirst()) {
                do {
                    long variantFromDbId = cursor.getLong(cursor.getColumnIndex("id"));
                    String variantFromDbName = cursor.getString(cursor.getColumnIndex("name"));
                    boolean isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);
                    if (variantName != null && variantFromDbName != null
                            && variantFromDbName.equals(variantName)) {
                        isInDb = true;
                        if (isDeleted) {
                            updateVariantInDb(variantFromDbId, false);
                        }
                    }
                } while (cursor.moveToNext());
            }
            if (!isInDb) {
                cv = new ContentValues();
                cv.put("name", variantName);
                cv.put("category", categoryId);
                cv.put("isDeleted", 0);
                db.insert(ctx.getString(R.string.table_variant_name), null, cv);
            }
        }

        if (cursor.moveToFirst()) {
            do {
                String variantFromDbName = cursor.getString(cursor.getColumnIndex("name"));
                long variantFromDbId = cursor.getLong(cursor.getColumnIndex("id"));
                boolean isDeleted = (cursor.getInt(cursor.getColumnIndex("isDeleted")) == 1);

                boolean isInNewVariants = false;
                for (String variantName : variantsNames) {
                    if (variantName.equals(variantFromDbName)) {
                        isInNewVariants = true;
                        break;
                    }
                }
                if (!isInNewVariants && !isDeleted) {
                    updateVariantInDb(variantFromDbId, true);

                    cv = new ContentValues();
                    cv.put("isDeleted", 1);
                    String tvSelection = "variant=?";
                    String[] tvSelectionArgs = { String.valueOf(variantFromDbId) };
                    db.update(ctx.getString(R.string.table_task_variant_name), cv, tvSelection,
                            tvSelectionArgs);
                }
            } while (cursor.moveToNext());
        }
    }

    private void updateVariantInDb(long id, boolean isDeleted) {

        logger.debug("updateVariantInDb()");

        ContentValues cv = new ContentValues();
        cv.put("isDeleted", isDeleted ? 1 : 0);

        String selection = "id=?";
        String[] selectionArgs = { String.valueOf(id) };
        db.update(ctx.getString(R.string.table_variant_name), cv, selection, selectionArgs);
    }

    public void getCategoryById(final long id) {

        logger.debug("getCategoryById()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor result = getCategoryByIdFromDb(id);
                notifyOperationFinished(Operation.GET_CATEGORY_BY_ID, result, 0);
            }
        });
    }

    private Cursor getCategoryByIdFromDb(long id) {

        logger.debug("getCateforyByIdFromDb()");

        String selection = "id=?";
        String[] selectionArgs = { String.valueOf(id) };
        return db.query(ctx.getString(R.string.table_category_name), null, selection, selectionArgs,
                null, null, null);
    }
}
