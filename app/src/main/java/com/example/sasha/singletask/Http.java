package com.example.sasha.singletask;

import android.content.Context;
import android.net.ConnectivityManager;

import okhttp3.OkHttpClient;

public class Http {

    public static final int OK = 0;
    public static final int NOT_FOUND = 1;
    public static final int INVALID_REQUEST = 2;
    public static final int INCORRECT_REQUEST = 3;
    public static final int UNKNOWN_ERROR = 4;
    public static final int ALREADY_EXIST = 5;

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();

    public static OkHttpClient getClient() {
        return OK_HTTP_CLIENT;
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
