package com.example.sasha.singletask.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.choice.CurrentChoice;
import com.example.sasha.singletask.choice.categoriesRecyclerView.CategoriesItem;
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
        UPDATE_CATEGORY,
        MARK_TASK_DELETED,
        MARK_CATEGORY_DELETED
    }

    private static DB instance;
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

    private final Executor executor = Executors.newSingleThreadExecutor();

    public interface Callback {
        void onOperationFinished(Operation operation, Cursor result, int position);
    }

    public interface GetCategoryByIdCallback {
        void onReceiveCategoryById(String categoryName);
    }

    public interface GetVariantsByCategoryCallback {
        void onReceiveVariantsByCategory(List<VariantDataSet> variants, int position);
    }

    public interface UpdateOrInsertCategoryCallback {
        void onCategoryUpdateOrInsertFinished(final long categoryId, String categoryName);
    }

    public interface GetTaskByIdCallback {
        void onReceiveTaskById(TaskDataSet task);
    }

    public interface GetCategoriesCallback {
        void onReceiveCategories(List<CategoryDataSet> categories);
    }

    public interface GetTasksCallback {
        void onReceiveTasks(List<TaskDataSet> tasks);
    }

    public interface GetVariantByTaskAndCategoryCallback {
        void onReceiveVariantByTaskAndCategory(VariantDataSet variant, int position);
    }

    public interface UpdateOrInsertTaskCallback {
        void onUpdateOrInsertTaskFinished(final long taskId, String taskName);
    }

    public interface SelectTasksCallback {
        void onTasksSelectionFinished();
    }

    public interface MarkCategoryDeletedCallback {
        void onMarkCategoryDeleted(final long categoryId);
    }

    public interface MarkTaskDeletedCallback {
        void onMarkTaskDeleted(final long taskId);
    }


    private DB.Callback callback;
    private DB.MarkCategoryDeletedCallback markCategoryDeletedCallback;
    private DB.MarkTaskDeletedCallback markTaskDeletedCallback;

    private DB.GetCategoryByIdCallback getCategoryNameByIdCallback;
    private DB.GetVariantsByCategoryCallback getVariantsByCategoryCallback;
    private DB.UpdateOrInsertCategoryCallback updateOrInsertCategoryCallback;
    private DB.GetTaskByIdCallback getTaskByIdCallback;
    private DB.GetCategoriesCallback getCategoriesCallback;
    private DB.GetTasksCallback getTasksCallback;
    private DB.GetVariantByTaskAndCategoryCallback getVariantByTaskAndCategoryCallback;
    private DB.UpdateOrInsertTaskCallback updateOrInsertTaskCallback;
    private DB.SelectTasksCallback selectTasksCallback;

    public void setCallback(DB.Callback callback) {

        logger.debug("setCallback()");

        this.callback = callback;
    }

    public void setGetCategoryNameByIdCallback(DB.GetCategoryByIdCallback callback) {
        this.getCategoryNameByIdCallback = callback;
    }

    public void setMarkCategoryDeletedCallback(DB.MarkCategoryDeletedCallback callback) {
        this.markCategoryDeletedCallback = callback;
    }

    public void setMarkTaskDeletedCallback(DB.MarkTaskDeletedCallback callback) {
        this.markTaskDeletedCallback = callback;
    }

    public void setGetVariantsByCategoryCallback(DB.GetVariantsByCategoryCallback callback) {
        this.getVariantsByCategoryCallback = callback;
    }

    public void setUpdateOrInsertCategoryCallback(DB.UpdateOrInsertCategoryCallback callback) {
        this.updateOrInsertCategoryCallback = callback;
    }

    public void setGetTaskByIdCallback(DB.GetTaskByIdCallback callback) {
        this.getTaskByIdCallback = callback;
    }

    public void setGetCategoriesCallback(DB.GetCategoriesCallback callback) {
        this.getCategoriesCallback = callback;
    }

    public void setGetVariantByTaskAndCategoryCallback(
            DB.GetVariantByTaskAndCategoryCallback callback) {
        this.getVariantByTaskAndCategoryCallback = callback;
    }

    public void setUpdateOrInsertTaskCallback(DB.UpdateOrInsertTaskCallback callback) {
        this.updateOrInsertTaskCallback = callback;
    }

    public void setSelectTasksCallback(DB.SelectTasksCallback callback) {
        this.selectTasksCallback = callback;
    }

    public void setGetTasksCallback(DB.GetTasksCallback callback) {
        this.getTasksCallback = callback;
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

    private void notifyReceiveCategoryNameById(final String categoryName) {

        logger.debug("notifyReceiveCategoryNameById()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (getCategoryNameByIdCallback != null) {
                    getCategoryNameByIdCallback.onReceiveCategoryById(categoryName);
                }
            }
        });
    }

    private void notifyMarkCategoryDeleted(final long categoryId) {

        logger.debug("notifyMarkCategoryDeleted()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (markCategoryDeletedCallback != null) {
                    markCategoryDeletedCallback.onMarkCategoryDeleted(categoryId);
                }
            }
        });
    }

    private void notifyMarkTaskDeleted(final long taskId) {

        logger.debug("notifyMarkTaskDeleted()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (markTaskDeletedCallback != null) {
                    markTaskDeletedCallback.onMarkTaskDeleted(taskId);
                }
            }
        });
    }

    private void notifyReceiveVariantsByCategory(final List<VariantDataSet> variants, final int position) {

        logger.debug("notifyReceiveVariantsByCategory()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (getVariantsByCategoryCallback != null) {
                    getVariantsByCategoryCallback.onReceiveVariantsByCategory(variants, position);
                }
            }
        });
    }

    private void notifyUpdateOrInsertCategoryFinished(final long categoryId, final String categoryName) {

        logger.debug("notifyReceiveCategoryNameById()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (updateOrInsertCategoryCallback != null) {
                    updateOrInsertCategoryCallback.onCategoryUpdateOrInsertFinished(categoryId, categoryName);
                }
            }
        });
    }

    private void notifyReceiveTaskById(final TaskDataSet task) {

        logger.debug("notifyReceiveTaskById()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (getTaskByIdCallback != null) {
                    getTaskByIdCallback.onReceiveTaskById(task);
                }
            }
        });
    }

    private void notifyReceiveCategories(final List<CategoryDataSet> categories) {

        logger.debug("notifyReceiveCategories()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (getCategoriesCallback != null) {
                    getCategoriesCallback.onReceiveCategories(categories);
                }
            }
        });
    }

    private void notifyReceiveTasks(final List<TaskDataSet> tasks) {

        logger.debug("notifyReceiveTasks()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (getTasksCallback != null) {
                    getTasksCallback.onReceiveTasks(tasks);
                }
            }
        });
    }

    private void notifyReceiveVariantByTaskAndCategory(final VariantDataSet variant,
                                                       final int position) {

        logger.debug("notifyReceiveVariantByTaskAndCategory()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (getVariantByTaskAndCategoryCallback != null) {
                    getVariantByTaskAndCategoryCallback.onReceiveVariantByTaskAndCategory(
                            variant,
                            position);
                }
            }
        });
    }

    private void notifyUpdateOrInsertTaskFinished(final long taskId, final String taskName) {

        logger.debug("notifyUpdateOrInsertTaskFinished()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (updateOrInsertTaskCallback != null) {
                    updateOrInsertTaskCallback.onUpdateOrInsertTaskFinished(taskId, taskName);
                }
            }
        });
    }

    private void notifyTasksSelectionFinished() {

        logger.debug("notifyTasksSelectionFinished()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (selectTasksCallback != null) {
                    selectTasksCallback.onTasksSelectionFinished();
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

        //if (dbHelper!=null) dbHelper.close();
        logger.info("Db closed");
    }

    private Cursor selectByServerId(String tableName, long serverId) {

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
        cursor.close();

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
        cursor.close();

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
        cursor.close();

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
        cursor.close();

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
                            category.setParent(insertCategory(c));
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
        category.setId(id);
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

            Cursor categoryCursor = selectByServerId(ctx.getString(R.string.table_category_name),
                    variant.getCategory());
            if (categoryCursor.moveToFirst()) {
                long id = categoryCursor.getLong(categoryCursor.getColumnIndex("id"));
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
                long task = taskCursor.getLong(taskCursor.getColumnIndex("id"));
                tv.setTask(task);
                long variant = variantCursor.getLong(variantCursor.getColumnIndex("id"));
                tv.setVariant(variant);
            } else {
                return;
            }
            taskCursor.close();
            variantCursor.close();

            ContentValues cv = new ContentValues();
            cv.put("task", tv.getTask());
            cv.put("variant", tv.getVariant());
            cv.put("isDeleted", 0);

            db.insert(ctx.getString(R.string.table_task_variant_name), null, cv);
        }
    }

    public void markTaskDeleted(final Long task) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                markTaskDeletedInDB(task);
                notifyMarkTaskDeleted(task);
            }
        });
    }

    private void markTaskDeletedInDB(Long category) {
        ContentValues cv = new ContentValues();
        cv.put("isDeleted", 1);
        cv.put("lastUpdate", Utils.getCurrentTimeAsString());
        String selection = "id=?";
        String[] selectionArgs = { String.valueOf(category)  };
        db.update(ctx.getString(R.string.table_task_name), cv, selection, selectionArgs);
    }

    public void markCategoryDeleted(final Long category) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                markCategoryDeletedInDB(category);
                notifyMarkCategoryDeleted(category);
            }
        });
    }

    private void markCategoryDeletedInDB(Long category) {

        ContentValues cv = new ContentValues();
        cv.put("isDeleted", 1);
        cv.put("lastUpdate", Utils.getCurrentTimeAsString());
        String selection = "id=?";
        String[] selectionArgs = { String.valueOf(category)  };
        db.update(ctx.getString(R.string.table_category_name), cv, selection, selectionArgs);
    }

    public void getTasks() {

        logger.debug("getTasks()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getTasksFromDb();
                List<TaskDataSet> tasks = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        tasks.add(new TaskDataSet(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                notifyReceiveTasks(tasks);
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
                Cursor cursor = getCategoriesFromDb();
                List<CategoryDataSet> categories = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        categories.add(new CategoryDataSet(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                notifyReceiveCategories(categories);
            }
        });
    }

    private Cursor getCategoriesFromDb() {

        logger.debug("getCategoriesFromDb()");

        String selection = "isDeleted IS NULL OR isDeleted=0";
        return db.query(ctx.getString(R.string.table_category_name), null, selection,
                null, null, null, null);
    }

    public void getCategoryNameById(final long id) {

        logger.debug("getCategoryNameById()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getCategoryByIdFromDb(id);
                String categoryName = "";
                if (cursor.moveToFirst()) {
                    categoryName = cursor.getString(cursor.getColumnIndex("name"));
                }
                cursor.close();
                notifyReceiveCategoryNameById(categoryName);
            }
        });
    }

    private Cursor getCategoryByIdFromDb(long id) {

        logger.debug("getCategoryNameByIdFromDb()");

        String selection = "id=?";
        String[] selectionArgs = { String.valueOf(id) };
        return db.query(ctx.getString(R.string.table_category_name), null, selection, selectionArgs,
                null, null, null);
    }

    public void getVariantsByCategory(final Long category, final int position) {

        logger.debug("getVariantsByCategory()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Cursor cursor = getVariantsByCategoryFromDb(category, false);
                List<VariantDataSet> variants = new ArrayList<>();
                if (cursor.moveToFirst()) {
                    do {
                        variants.add(new VariantDataSet(cursor));
                    } while (cursor.moveToNext());
                }
                cursor.close();
                notifyReceiveVariantsByCategory(variants, position);
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
                Cursor cursor = getVariantByTaskAndCategoryFromDb(task, category);
                VariantDataSet variant;
                if (cursor.moveToFirst()) {
                    variant = new VariantDataSet(cursor);
                    cursor.close();
                    notifyReceiveVariantByTaskAndCategory(variant, position);
                } else {
                    cursor.close();
                    notifyReceiveVariantByTaskAndCategory(null, position);
                }
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
                Cursor cursor = getTaskByIdFromDb(id);
                if (cursor.moveToFirst()) {
                    TaskDataSet task = new TaskDataSet(cursor);
                    cursor.close();
                    notifyReceiveTaskById(task);
                } else {
                    cursor.close();
                    notifyReceiveTaskById(null);
                }
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
                long taskId = insertNewTaskInDb(name, time, date, comment, variants);
                notifyUpdateOrInsertTaskFinished(taskId, name);
            }
        });
    }

    private long insertNewTaskInDb(String name, int time, String date, String comment,
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
        return taskId;
    }

    public void updateTask(final long id, final String name, final int time, final String date,
                           final String comment, final ArrayList<Long> variants,
                           final ArrayList<Long> categories) {

        logger.debug("updateTask()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                updateTaskInDb(id, name, time, date, comment, variants, categories);
                notifyUpdateOrInsertTaskFinished(id, name);
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
                long categoryId =  insertNewCategoryInDb(categoryName, variantsNames);
                notifyUpdateOrInsertCategoryFinished(categoryId, categoryName);
            }
        });
    }

    private long insertNewCategoryInDb(String categoryName, List<String> variantsNames) {

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
        return categoryId;
    }

    public void updateCategory(final long categoryId, final String categoryName,
                               final List<String> variantsNames) {

        logger.debug("updateCategory()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                updateCategoryInDb(categoryId, categoryName, variantsNames);
                notifyUpdateOrInsertCategoryFinished(categoryId, categoryName);
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
        cursor.close();
    }

    private void updateVariantInDb(long id, boolean isDeleted) {

        logger.debug("updateVariantInDb()");

        ContentValues cv = new ContentValues();
        cv.put("isDeleted", isDeleted ? 1 : 0);

        String selection = "id=?";
        String[] selectionArgs = { String.valueOf(id) };
        db.update(ctx.getString(R.string.table_variant_name), cv, selection, selectionArgs);
    }

    public void selectTasks() {

        logger.debug("selectTasks()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<TaskDataSet> tasks = selectTasksInDb();
                List<TaskDataSet> urgent = new ArrayList<>();
                List<TaskDataSet> simple = new ArrayList<>();
                for (TaskDataSet task : tasks) {
                    if (task.getDate() != null) {
                        int days = Utils.getDaysBetweenDateAndCurrentDate(task.getDate());
                        if (days < Utils.DAYS_IN_WEEK) {
                            urgent.add(task);
                        } else {
                            simple.add(task);
                        }
                    } else {
                        simple.add(task);
                    }
                }

                CurrentChoice.getInstance().setUrgentTasks(urgent);
                CurrentChoice.getInstance().setSimpleTasks(simple);
                notifyTasksSelectionFinished();
            }
        });
    }

    private List<TaskDataSet> selectTasksInDb() {

        logger.debug("selectTaskInDb()");

        String selection = "time<=?  AND (isDeleted IS NULL OR isDeleted=0)";
        String[] selectionArgs = { String.valueOf(CurrentChoice.getInstance().getTime()) };
        Cursor cursor = db.query(ctx.getString(R.string.table_task_name), null, selection,
                selectionArgs, null, null, null);

        List<TaskDataSet> tasks = new ArrayList<>();
        List<CategoriesItem> categories = CurrentChoice.getInstance().getCategories();
        if (cursor.moveToFirst()) {
            do {
                TaskDataSet task = new TaskDataSet(cursor);
                boolean isTaskApproach = true;
                for (CategoriesItem category : categories) {
                    long variantId = category.getVariantId();
                    Cursor variantCursor = getVariantByTaskAndCategoryFromDb(
                            task.getId(),
                            category.getCategoryId());
                    if (!variantCursor.moveToFirst()
                            || variantCursor.getInt(cursor.getColumnIndex("id")) != variantId) {
                        variantCursor.close();
                        isTaskApproach = false;
                        break;
                    }
                    variantCursor.close();
                }
                if (isTaskApproach) {
                    tasks.add(task);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return tasks;
    }

    public long getTaskIdByServerId(long serverId) {

        logger.debug("getTaskIdByServerId()");

        Cursor cursor = selectByServerId(ctx.getString(R.string.table_task_name), serverId);
        long id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndex("id"));
        }
        cursor.close();
        return id;
    }

    public void updateTaskTime(final long id, final int postponeTime) {

        logger.debug("updateTaskTime()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                updateTaskTimeInDb(id, postponeTime);
            }
        });
    }

    private void updateTaskTimeInDb(long id, int postponeTime) {

        logger.debug("updateTaskTimeInDb()");

        String[] updateArgs = { String.valueOf(postponeTime), Utils.getCurrentTimeAsString(),
                String.valueOf(id) };
        db.execSQL(SqlQueries.UPDATE_TASK_TIME_AND_LAST_UPDATE, updateArgs);
    }

    // TODO разобраться когда закрывать БД
}
