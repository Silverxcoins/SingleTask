package com.example.sasha.singletask.user;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sasha.singletask.helpers.Http;
import com.example.sasha.singletask.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignUpFragment extends Fragment implements UsersManager.SignUpCallback {

    private static final Logger logger = LoggerFactory.getLogger(SignUpFragment.class);

    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String PASSWORD_REPEAT_KEY = "passwordRepeat";
    private static final String CODE_KEY = "code";

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordRepeatEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, null);

        logger.debug("onCreateView()");

        initViews(view);

        if (savedInstanceState != null) {
            emailEditText.setText(savedInstanceState.getString(EMAIL_KEY));
            passwordEditText.setText(savedInstanceState.getString(PASSWORD_KEY));
            passwordRepeatEditText.setText(savedInstanceState.getString(PASSWORD_REPEAT_KEY));
        }

        UsersManager.getInstance().setSignUpCallback(this);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){

        logger.debug("onSaveInstanceState()");

        if (emailEditText != null)
            outState.putString(EMAIL_KEY, emailEditText.getText().toString());
        if (passwordEditText != null)
            outState.putString(PASSWORD_KEY, passwordEditText.getText().toString());
        if (passwordRepeatEditText != null)
            outState.putString(PASSWORD_REPEAT_KEY, passwordRepeatEditText.getText().toString());

        super.onSaveInstanceState(outState);
    }

    private void initViews(View view) {

        logger.debug("initViews()");

        emailEditText = (EditText) view.findViewById(R.id.editTextEmailSignUp);
        passwordEditText = (EditText) view.findViewById(R.id.editTextPasswordSignUp);
        passwordRepeatEditText = (EditText) view.findViewById(R.id.editTextRepeatPasswordSignUp);
        Button signUpButton = (Button) view.findViewById(R.id.buttonSignUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {

        logger.debug("signUp()");

        if (!Http.isNetworkAvailable(getActivity())) {
            logger.warn("No internet connection");
            Toast.makeText(getActivity(), R.string.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (areThereEmptyFields()) {
            Toast.makeText(getActivity(), R.string.THERE_ARE_EMPTY_FIELDS, Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if(!isEmailValid()) {
            Toast.makeText(getActivity(), R.string.INVALID_EMAIL, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!arePasswordsMatch()) {
            Toast.makeText(getActivity(), R.string.PASSWORDS_DONT_MATCH, Toast.LENGTH_SHORT).show();
            return;
        }

        UsersManager.getInstance().signUp(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString()
        );
    }

    @Override
    public void onSignUpFinished(JSONObject json) {

        logger.debug("onSignUpFinished()");

        if (json == null) {
            logger.warn("Something went wrong");
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int code = json.getInt(CODE_KEY);
            if (code == Http.OK) {
                logger.info("Sign up successful");
                Toast.makeText(getActivity(), R.string.SIGN_UP_SUCCESSFULL, Toast.LENGTH_SHORT)
                        .show();
                clearFields();
                getActivity().onBackPressed();
            } else if (code == Http.ALREADY_EXIST) {
                Toast.makeText(getActivity(), R.string.EMAIL_ALREADY_IN_USE,
                        Toast.LENGTH_SHORT).show();
            } else {
                logger.warn("Something went wrong");
                Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            logger.warn("Something went wrong");
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {

        logger.debug("clearFields()");

        emailEditText.getText().clear();
        passwordEditText.getText().clear();
        passwordRepeatEditText.getText().clear();
    }

    private boolean areThereEmptyFields() {

        logger.debug("areThereEmptyFields()");

        return emailEditText.getText().toString().isEmpty()
                || passwordEditText.getText().toString().isEmpty()
                || passwordRepeatEditText.getText().toString().isEmpty();
    }

    private boolean isEmailValid() {

        logger.debug("isEmailValid()");

        CharSequence email = emailEditText.getText().toString();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean arePasswordsMatch() {

        logger.debug("arePasswordsMatch()");

        return passwordEditText.getText().toString()
                .equals(passwordRepeatEditText.getText().toString());
    }
}
