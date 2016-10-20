package com.example.sasha.singletask;

import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UsersManager {

    private static final String TAG = "UsersManager";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String SIGN_UP_URL = "http://188.120.235.252/singletask/api/user/signup";
    private static final String SIGN_IN_URL = "http://188.120.235.252/singletask/api/user/signin";

    private static final UsersManager USERS_MANAGER = new UsersManager();

    public static UsersManager getInstance() {
        return USERS_MANAGER;
    }

    public interface SignUpCallback {
        void onSignUpFinished(String json);
    }

    public interface SignInCallback {
        void onSignInFinished(String json);
    }

    private final Executor executor = Executors.newCachedThreadPool();

    private SignUpCallback signUpCallback;
    private SignUpCallback signInCallback;

    public void setSignUpCallback(SignUpCallback signUpCallback) {
        this.signUpCallback = signUpCallback;
    }

    public void signUp(final String email, final String password) {
        Log.d(TAG, "signUp()");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String json;
                try {
                    json = signUpInternal(email, password);
                } catch (IOException e) {
                    e.printStackTrace();
                    json = null;
                }
                notifySignUpFinished(json);
            }
        });
    }

    private void notifySignUpFinished(final String json) {
        Log.d(TAG, "notifySignUpFinished(), json: " + json);
        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (signUpCallback != null) {
                    signUpCallback.onSignUpFinished(json);
                }
            }
        });
    }

    private String signUpInternal(String email, String password) throws IOException {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        Log.d(TAG, "signUpInternal()");

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(SIGN_UP_URL)
                .post(body)
                .build();
        Response response = Http.getClient().newCall(request).execute();

        String responseBody = response.body().string();
        return responseBody;
    }
}
