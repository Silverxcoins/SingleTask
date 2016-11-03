package com.example.sasha.singletask.choice;

import android.content.Context;

import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.db.dataSets.CategoryDataSet;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;
import com.example.sasha.singletask.db.dataSets.VariantDataSet;
import com.example.sasha.singletask.helpers.Http;
import com.example.sasha.singletask.helpers.Ui;
import com.example.sasha.singletask.helpers.Utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SyncManager {

    private static final Logger logger = LoggerFactory.getLogger(SyncManager.class);

    private static final String CODE_KEY = "code";

    private static final String SYNC_TASKS_URL =
            "http://188.120.235.252/singletask/api/task/sync";
    private static final String SYNC_CATEGORIES_URL =
            "http://188.120.235.252/singletask/api/category/sync";
    private static final String SYNC_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/variant/sync";
    private static final String SYNC_TASKS_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/task-variant/sync";
    private static final String LIST_TASKS_URL =
            "http://188.120.235.252/singletask/api/task/list?user=";
    private static final String LIST_CATEGORIES_URL =
            "http://188.120.235.252/singletask/api/category/list?user=";
    private static final String LIST_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/variant/list?user=";
    private static final String LIST_TASKS_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/task-variant/list?user=";

    private DB db;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final SyncManager SYNC_MANAGER = new SyncManager();

    public static SyncManager getInstance() {
        return SYNC_MANAGER;
    }

    public interface Callback {
        void onSyncFinished(boolean wasSuccessful);
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    private SyncManager.Callback callback;

    public void setCallback(SyncManager.Callback callback) {

        logger.debug("setCallback()");

        this.callback = callback;
    }

    public void sync(Context ctx) {

        logger.debug("sync()");

        this.db = DB.getInstance(ctx);
        db.open();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean wasSuccessful = syncInternal();
                notifySyncFinished(wasSuccessful);
            }
        });
    }

    public void getDataFromServer(Context ctx) {

        logger.debug("getDataFromServer()");

        this.db = DB.getInstance(ctx);
        db.open();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean wasSuccessful = getDataFromServerInternal();
                notifySyncFinished(wasSuccessful);
            }
        });
    }

    private void notifySyncFinished(final boolean wasSuccessfull) {

        logger.debug("notifySyncFinished()");

        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSyncFinished(wasSuccessfull);
                }
            }
        });
    }

    private boolean getDataFromServerInternal() {

        logger.debug("getDataFromServerInternal()");

        String response = Http.sendGetRequest(LIST_TASKS_URL + Utils.getUserId());
        if (!isServerOperationSuccessful(response)) return false;
        db.insertTasksFromJson(response);

        response = Http.sendGetRequest(LIST_CATEGORIES_URL + Utils.getUserId());
        if (!isServerOperationSuccessful(response)) return false;
        db.insertCategoriesFromJson(response);

        response = Http.sendGetRequest(LIST_VARIANTS_URL + Utils.getUserId());
        if (!isServerOperationSuccessful(response)) return false;
        db.insertVariantsFromJson(response);

        response = Http.sendGetRequest(LIST_TASKS_VARIANTS_URL + Utils.getUserId());
        if (!isServerOperationSuccessful(response)) return false;
        db.insertTasksVariantsFromJson(response);

        return true;
    }

    private boolean syncInternal() {

        logger.debug("syncInternal()");

        String tasksResponse = Http.sendPostRequest(SYNC_TASKS_URL,
                db.getAllTasksInJson());
        if (!isServerOperationSuccessful(tasksResponse)) {
            return false;
        }
        List<TaskDataSet> insertedTasks =
                db.insertTasksFromJson(Http.sendGetRequest(LIST_TASKS_URL + Utils.getUserId()));

        String categoriesResponse = Http.sendPostRequest(SYNC_CATEGORIES_URL,
                db.getAllCategoriesInJson());
        if (!isServerOperationSuccessful(categoriesResponse)) {
            return false;
        }
        List<CategoryDataSet> insertedCategories =
                db.insertCategoriesFromJson(Http.sendGetRequest(LIST_CATEGORIES_URL
                        + Utils.getUserId()));

        String variantsResponse = Http.sendPostRequest(SYNC_VARIANTS_URL,
                db.getAllVariantsInJson(insertedCategories));
        if (!isServerOperationSuccessful(variantsResponse)) {
            return false;
        }
        List<VariantDataSet> insertedVariants =
                db.insertVariantsFromJson(Http.sendGetRequest(LIST_VARIANTS_URL
                        + Utils.getUserId()));

        String tasksVariantsResponse = Http.sendPostRequest(SYNC_TASKS_VARIANTS_URL,
                db.getAllTasksVariantsInJson(insertedTasks, insertedVariants));
        if (!isServerOperationSuccessful(tasksVariantsResponse)) {
            return false;
        }
        db.insertTasksVariantsFromJson(Http.sendGetRequest(LIST_TASKS_VARIANTS_URL
                + Utils.getUserId()));
        return true;
    }

    private boolean isServerOperationSuccessful(String response) {

        logger.debug("isServerOperationSuccess()");

        try {
            if (response != null) {
                JsonNode node = mapper.readValue(response, JsonNode.class);
                if (node.has(CODE_KEY) && node.get(CODE_KEY).getIntValue() == Http.OK) {
                    return true;
                }
            }
            return false;

        } catch (IOException e) {
            return false;
        }
    }
}
