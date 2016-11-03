package com.example.sasha.singletask.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.choice.ChoiceActivity;
import com.example.sasha.singletask.helpers.Http;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignInFragment extends Fragment implements UsersManager.SignInCallback {

    private static final Logger logger = LoggerFactory.getLogger(SignInFragment.class);

    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String CODE_KEY = "code";
    private static final String RESPONSE_KEY = "response";
    private static final String ID_KEY = "id";
    private static final String AFTER_SIGN_IN_KEY = "afterSignIn";
    private static final String IS_SIGNED_IN_KEY = "isSignedIn";

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, null);

        logger.debug("onCreateView()");

        initViews(view);

        if (savedInstanceState != null
                && savedInstanceState.containsKey(EMAIL_KEY)
                && savedInstanceState.containsKey(PASSWORD_KEY)) {
            emailEditText.setText(savedInstanceState.getString(EMAIL_KEY));
            passwordEditText.setText(savedInstanceState.getString(PASSWORD_KEY));
        }

        UsersManager.getInstance().setSignInCallback(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        logger.debug("onSaveInstanceState()");

        if (emailEditText != null)
            outState.putString(EMAIL_KEY, emailEditText.getText().toString());
        if (passwordEditText != null)
            outState.putString(PASSWORD_KEY, passwordEditText.getText().toString());

        super.onSaveInstanceState(outState);
    }

    private void initViews(View view) {

        logger.debug("initUiItems()");

        emailEditText = (EditText) view.findViewById(R.id.editTextEmailSigIn);
        passwordEditText = (EditText) view.findViewById(R.id.editTextPasswordSignIn);
        Button signInButton = (Button) view.findViewById(R.id.buttonSignIn);
        Button signUpButton = (Button) view.findViewById(R.id.buttonGoToSignUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setSignUpFragment();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {

        logger.debug("signIn()");

        if (!Http.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT)
                    .show();
            logger.warn("No internet connection");
            return;
        }
        if (areThereEmptyFields()) {
            Toast.makeText(getActivity(), R.string.THERE_ARE_EMPTY_FIELDS, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        UsersManager.getInstance().signIn(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString()
        );
    }

    @Override
    public void onSignInFinished(JSONObject json) {

        logger.debug("onSignInFinished()");

        if (json == null) {
            logger.warn("Something went wrong");
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int code = json.getInt(CODE_KEY);
            if (code == Http.OK) {
                setUserSettings(json.getString(EMAIL_KEY), json.getLong(RESPONSE_KEY));
                Intent intent = new Intent(getActivity(), ChoiceActivity.class);
                intent.putExtra(AFTER_SIGN_IN_KEY, true);

                logger.info("Sign in success");
                startActivity(intent);
                getActivity().finish();
            } else if (code == Http.NOT_FOUND) {
                Toast.makeText(getActivity(), R.string.WRONG_EMAIL_OR_PASSWORD, Toast.LENGTH_SHORT)
                        .show();
            } else {
                logger.warn("Something went wrong");
                Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            logger.warn("Something went wrong");
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areThereEmptyFields() {

        logger.debug("areThereEmptyFields()");

        return emailEditText.getText().toString().isEmpty()
                || passwordEditText.getText().toString().isEmpty();
    }

    private void setUserSettings(String email, long id) {

        logger.debug("setUserSettings()");

        SharedPreferences settings = getActivity()
                .getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_SIGNED_IN_KEY, true);
        editor.putString(EMAIL_KEY, email);
        editor.putLong(ID_KEY, id);
        editor.apply();
    }
}