package com.example.sasha.singletask.user;

import com.example.sasha.singletask.helpers.Ui;
import com.example.sasha.singletask.helpers.Http;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UsersManager {

    private static final Logger logger = LoggerFactory.getLogger(UsersManager.class);

    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";

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
        logger.debug("setSignUpCallback()");
        this.signUpCallback = signUpCallback;
    }

    public void setSignInCallback(SignInCallback signInCallback) {
        logger.debug("setSignInCallback()");
        this.signInCallback = signInCallback;
    }

    public void signUp(final String email, final String password) {

        logger.debug("signUp()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = signUpInternal(email, password);
                logger.debug("server response: {}", json.toString());
                notifySignUpFinished(json);
            }
        });
    }

    public void signIn(final String email, final String password) {

        logger.debug("signIn()");

        executor.execute(new Runnable() {
            @Override
            public void run() {
                JSONObject json = signInInternal(email, password);
                logger.debug("server response: {}", json.toString());
                notifySignInFinished(json);
            }
        });
    }

    private void notifySignUpFinished(final JSONObject json) {

        logger.debug("notifySignUpFinished()");

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

        logger.debug("notifySignInFinished()");

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

        logger.debug("signUpInternal()");

        String json = formJson(email, password);
        try {
            return new JSONObject(Http.sendPostRequest(SIGN_UP_URL, json));
        } catch (JSONException e) {
            return null;
        }
    }

    private JSONObject signInInternal(String email, String password) {

        logger.debug("signInInternal()");

        String requestJson = formJson(email, password);
        try {
            String response = Http.sendPostRequest(SIGN_IN_URL, requestJson);
            JSONObject responseJson = new JSONObject(response);
            responseJson.put(EMAIL_KEY, email);
            return responseJson;
        } catch (JSONException | NullPointerException e) {
            return null;
        }
    }

    private String formJson(String email, String password) {

        logger.debug("formJson()");

        try {
            JSONObject json = new JSONObject();
            json.put(EMAIL_KEY, email);
            json.put(PASSWORD_KEY, password);
            return json.toString();
        } catch (JSONException e) {
            return null;
        }
    }
}
