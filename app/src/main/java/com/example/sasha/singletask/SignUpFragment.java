package com.example.sasha.singletask;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SignUpFragment extends Fragment {
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
}
