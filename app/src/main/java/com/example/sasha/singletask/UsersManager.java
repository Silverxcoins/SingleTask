package com.example.sasha.singletask;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UsersManager {

    private static final String TAG = "UsersManager";

    private static final String SIGN_UP_URL = "http://188.120.235.252/singletask/api/user/signup";
    private static final String SIGN_IN_URL = "http://188.120.235.252/singletask/api/user/signin";

    private static final UsersManager USERS_MANAGER = new UsersManager();

    public static UsersManager getInstance() {
        return USERS_MANAGER;
    }

    public interface SignUpCallback {
        void onSignUpFinished(JSONObject json);
    }

    public interface SignInCallback {
        void onSignInFinished(JSONObject json);
    }

    private final Executor executor = Executors.newCachedThreadPool();

    private SignUpCallback signUpCallback;
    private SignInCallback signInCallback;

    public void setSignUpCallback(SignUpCallback signUpCallback) {
        this.signUpCallback = signUpCallback;
    }

    public void setSignInCallback(SignInCallback signInCallback) {
        this.signInCallback = signInCallback;
    }

    public void signUp(final String email, final String password) {
        Log.d(TAG, "signUp()");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = signUpInternal(email, password);
                notifySignUpFinished(json);
            }
        });
    }

    public void signIn(final String email, final String password) {
        Log.d(TAG, "signIn()");
        executor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = signInInternal(email, password);
                notifySignInFinished(json);
            }
        });
    }

    private void notifySignUpFinished(final JSONObject json) {
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

    private void notifySignInFinished(final JSONObject json) {
        Log.d(TAG, "notifySignUpFinished(), json: " + json);
        Ui.run(new Runnable() {
            @Override
            public void run() {
                if (signInCallback != null) {
                    signInCallback.onSignInFinished(json);
                }
            }
        });
    }

    private JSONObject signUpInternal(String email, String password) {
        Log.d(TAG, "signUpInternal()");
        String json = formJson(email, password);
        try {
            return new JSONObject(Http.sendPostRequest(SIGN_UP_URL, json));
        } catch (JSONException e) {
            return null;
        }
    }

    private JSONObject signInInternal(String email, String password) {
        Log.d(TAG, "signInInternal()");
        String requestJson = formJson(email, password);
        try {
            String response = Http.sendPostRequest(SIGN_IN_URL, requestJson);
            JSONObject responseJson = new JSONObject(response);
            responseJson.put("email", email);
            return responseJson;
        } catch (JSONException e) {
            return null;
        }
    }

    private String formJson(String email, String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            return json.toString();
        } catch (JSONException e) {
            return null;
        }
    }
}
