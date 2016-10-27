package com.example.sasha.singletask.choice;

import android.content.Context;
import android.util.Log;

import com.example.sasha.singletask.db.DB;
import com.example.sasha.singletask.db.dataSets.CategoryDataSet;
import com.example.sasha.singletask.db.dataSets.TaskDataSet;
import com.example.sasha.singletask.db.dataSets.VariantDataSet;
import com.example.sasha.singletask.helpers.Http;
import com.example.sasha.singletask.helpers.Ui;
import com.example.sasha.singletask.helpers.Utils;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SyncManager {

    private static final String TAG = "SyncManager";

    private static final String SYNC_TASKS_URL =
            "http://188.120.235.252/singletask/api/task/sync";
    private static final String SYNC_CATEGORIES_URL =
            "http://188.120.235.252/singletask/api/category/sync";
    private static final String SYNC_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/variant/sync";
    private static final String SYNC_TASKS_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/task-variant/sync";
    private static final String LIST_TASKS_URL =
            "http://188.120.235.252/singletask/api/task/list?user=" + Utils.getUserId();
    private static final String LIST_CATEGORIES_URL =
            "http://188.120.235.252/singletask/api/category/list?user=" + Utils.getUserId();
    private static final String LIST_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/variant/list?user=" + Utils.getUserId();
    private static final String LIST_TASKS_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/task-variant/list?user=" + Utils.getUserId();

    private Context ctx;
    private DB db;

    private ObjectMapper mapper = new ObjectMapper();

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
        this.callback = callback;
    }

    public void sync(Context ctx) {
        this.ctx = ctx;
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

    private void notifySyncFinished(final boolean wasSuccessfull) {
        Log.d(TAG, "notifySignUpFinished(), success: " + wasSuccessfull);
        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSyncFinished(wasSuccessfull);
                }
            }
        });
    }

    private boolean syncInternal() {
        String tasksResponse = Http.sendPostRequest(SYNC_TASKS_URL,
                db.getAllTasksInJson());
        if (!isServerOperationSuccessful(tasksResponse)) {
            return false;
        }
        List<TaskDataSet> insertedTasks =
                db.insertTasksFromJson(Http.sendGetRequest(LIST_TASKS_URL));

        String categoriesResponse = Http.sendPostRequest(SYNC_CATEGORIES_URL,
                db.getAllCategoriesInJson());
        if (!isServerOperationSuccessful(categoriesResponse)) {
            return false;
        }
        List<CategoryDataSet> insertedCategories =
                db.insertCategoriesFromJson(Http.sendGetRequest(LIST_CATEGORIES_URL));

        String variantsResponse = Http.sendPostRequest(SYNC_VARIANTS_URL,
                db.getAllVariantsInJson(insertedCategories));
        if (!isServerOperationSuccessful(variantsResponse)) {
            return false;
        }
        List<VariantDataSet> insertedVariants =
                db.insertVariantsFromJson(Http.sendGetRequest(LIST_VARIANTS_URL));

        String tasksVariantsResponse = Http.sendPostRequest(SYNC_TASKS_VARIANTS_URL,
                db.getAllTasksVariantsInJson(insertedTasks, insertedVariants));
        if (!isServerOperationSuccessful(tasksVariantsResponse)) {
            return false;
        }
        db.insertTasksVariantsFromJson(Http.sendGetRequest(LIST_TASKS_VARIANTS_URL));
        db.close();
        return true;
    }

    private boolean isServerOperationSuccessful(String response) {
        System.out.println("ответ от сервера: " + response);
        try {
            JsonNode node = mapper.readValue(response, JsonNode.class);
            if (node.has("code") && node.get("code").getIntValue() == Http.OK) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
    }
}
