package com.example.sasha.singletask.helpers;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Http {

    public static final int OK = 0;
    public static final int NOT_FOUND = 1;
    public static final int ALREADY_EXIST = 5;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public static String sendPostRequest(String url, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = OK_HTTP_CLIENT.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }

    public static String sendGetRequest(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = OK_HTTP_CLIENT.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }
}
