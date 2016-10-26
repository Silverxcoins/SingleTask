package com.example.sasha.singletask.choice;

import android.util.Log;

import com.example.sasha.singletask.helpers.Http;
import com.example.sasha.singletask.helpers.Ui;
import com.example.sasha.singletask.user.UsersManager;

import org.json.JSONException;
import org.json.JSONObject;

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
            "http://188.120.235.252/singletask/api/task/list";
    private static final String LIST_CATEGORIES_URL =
            "http://188.120.235.252/singletask/api/category/list";
    private static final String LIST_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/variant/list";
    private static final String LIST_TASKS_VARIANTS_URL =
            "http://188.120.235.252/singletask/api/task-variant/list";

//    private static final SyncManager SYNC_MANAGER = new SyncManager();
//
//    public static UsersManager getInstance() {
//        return SYNC_MANAGER;
//    }
//
//    public interface Callback {
//        void onSyncFinished(JSONObject json);
//    }
//
//    private final Executor executor = Executors.newCachedThreadPool();
//
//    private SyncManager.Callback callback;
//
//    public void setcallback(SyncManager.Callback callback) {
//        this.callback = callback;
//    }
//
//    public void sync() {
//        Log.d(TAG, "signIn()");
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                JSONObject json = signInInternal(email, password);
//                notifySignInFinished(json);
//            }
//        });
//    }
//
//    private void notifySignUpFinished(final JSONObject json) {
//        Log.d(TAG, "notifySignUpFinished(), json: " + json);
//        Ui.run(new Runnable() {
//            @Override
//            public void run() {
//                if (signUpCallback != null) {
//                    signUpCallback.onSignUpFinished(json);
//                }
//            }
//        });
//    }
//
//    private void notifySignInFinished(final JSONObject json) {
//        Log.d(TAG, "notifySignUpFinished(), json: " + json);
//        Ui.run(new Runnable() {
//            @Override
//            public void run() {
//                if (signInCallback != null) {
//                    signInCallback.onSignInFinished(json);
//                }
//            }
//        });
//    }
//
//    private JSONObject signUpInternal(String email, String password) {
//        Log.d(TAG, "signUpInternal()");
//        String json = formJson(email, password);
//        try {
//            return new JSONObject(Http.sendPostRequest(SIGN_UP_URL, json));
//        } catch (JSONException e) {
//            return null;
//        }
//    }
//
//    private JSONObject signInInternal(String email, String password) {
//        Log.d(TAG, "signInInternal()");
//        String requestJson = formJson(email, password);
//        try {
//            String response = Http.sendPostRequest(SIGN_IN_URL, requestJson);
//            JSONObject responseJson = new JSONObject(response);
//            responseJson.put("email", email);
//            return responseJson;
//        } catch (JSONException | NullPointerException e) {
//            return null;
//        }
//    }
//
//    private String formJson(String email, String password) {
//        try {
//            JSONObject json = new JSONObject();
//            json.put("email", email);
//            json.put("password", password);
//            return json.toString();
//        } catch (JSONException e) {
//            return null;
//        }
//    }
}
