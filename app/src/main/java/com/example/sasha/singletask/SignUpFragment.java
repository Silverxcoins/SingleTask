package com.example.sasha.singletask;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpFragment extends Fragment implements UsersManager.SignUpCallback {
    private static final String TAG = "SignUpFragment";

    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String PASSWORD_REPEAT_KEY = "passwordRepeat";

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordRepeatEditText;
    private Button signUpButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, null);

        emailEditText = (EditText) view.findViewById(R.id.editTextEmailSignUp);
        passwordEditText = (EditText) view.findViewById(R.id.editTextPasswordSignUp);
        passwordRepeatEditText = (EditText) view.findViewById(R.id.editTextRepeatPasswordSignUp);
        signUpButton = (Button) view.findViewById(R.id.buttonSignUp);

        if (savedInstanceState != null) {
            emailEditText.setText(savedInstanceState.getString(EMAIL_KEY));
            passwordEditText.setText(savedInstanceState.getString(PASSWORD_KEY));
            passwordRepeatEditText.setText(savedInstanceState.getString(PASSWORD_REPEAT_KEY));
        }

        UsersManager.getInstance().setSignUpCallback(this);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "signUpButton clicked");
                signUp();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        if (emailEditText != null)
            outState.putString(EMAIL_KEY, emailEditText.getText().toString());
        if (passwordEditText != null)
            outState.putString(PASSWORD_KEY, passwordEditText.getText().toString());
        if (passwordRepeatEditText != null)
            outState.putString(PASSWORD_REPEAT_KEY, passwordRepeatEditText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private void signUp() {
        Log.d(TAG, "SignUp()");
        if (!Http.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), R.string.NO_INTERNET_CONNECTION,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (areThereEmptyFields()) {
            Toast.makeText(getActivity(), R.string.THERE_ARE_EMPTY_FIELDS,
                    Toast.LENGTH_SHORT).show();
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
    public void onSignUpFinished(String jsonString) {
        Log.d(TAG, "onSignUpFinished()");
        if (jsonString == null) {
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json;
        try {
            json = new JSONObject(jsonString);
            int code = json.getInt("code");
            if (code == Http.OK) {
                Toast.makeText(getActivity(), R.string.SIGN_UP_SUCCESSFULL,
                        Toast.LENGTH_SHORT).show();
                clearFields();
                getActivity().onBackPressed();
            } else if (code == Http.ALREADY_EXIST) {
                Toast.makeText(getActivity(), R.string.EMAIL_ALREADY_IN_USE,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        emailEditText.getText().clear();
        passwordEditText.getText().clear();
        passwordRepeatEditText.getText().clear();
    }

    private boolean areThereEmptyFields() {
        return emailEditText.getText().toString().isEmpty()
                || passwordEditText.getText().toString().isEmpty()
                || passwordRepeatEditText.getText().toString().isEmpty();
    }

    private boolean isEmailValid() {
        CharSequence email = emailEditText.getText().toString();
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean arePasswordsMatch() {
        return passwordEditText.getText().toString()
                .equals(passwordRepeatEditText.getText().toString());
    }
}
