package com.example.sasha.singletask;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInFragment extends Fragment implements UsersManager.SignInCallback {
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private Button signUpButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, null);

        emailEditText = (EditText) view.findViewById(R.id.editTextEmailSigIn);
        passwordEditText = (EditText) view.findViewById(R.id.editTextPasswordSignIn);
        signInButton = (Button) view.findViewById(R.id.buttonSignIn);
        signUpButton = (Button) view.findViewById(R.id.buttonGoToSignUp);

        if (savedInstanceState != null && savedInstanceState.containsKey(EMAIL_KEY)) {
            emailEditText.setText(savedInstanceState.getString(EMAIL_KEY));
            passwordEditText.setText(savedInstanceState.getString(PASSWORD_KEY));
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setSignUpFragment();
            }
        });

        UsersManager.getInstance().setSignInCallback(this);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (emailEditText != null)
            outState.putString(EMAIL_KEY, emailEditText.getText().toString());
        if (passwordEditText != null)
            outState.putString(PASSWORD_KEY, passwordEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private void signIn() {
        if (!Http.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.NO_INTERNET_CONNECTION, Toast.LENGTH_SHORT)
                    .show();
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
        if (json == null) {
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int code = json.getInt("code");
            if (code == Http.OK) {
                setUserSettings(json.getString("email"), json.getString("response"));
                Intent intent = new Intent(getActivity(), ChoiceActivity.class);
                startActivity(intent);
                getActivity().finish();
            } else if (code == Http.NOT_FOUND) {
                Toast.makeText(getActivity(), R.string.WRONG_EMAIL_OR_PASSWORD, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean areThereEmptyFields() {
        return emailEditText.getText().toString().isEmpty()
                || passwordEditText.getText().toString().isEmpty();
    }

    private void setUserSettings(String email, String token) {
        SharedPreferences settings = getActivity()
                .getSharedPreferences(getString(R.string.PREFS_NAME), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isSignedIn", true);
        editor.putString("email", email);
        editor.putString("token", token);
        editor.apply();
    }
}