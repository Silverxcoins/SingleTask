package com.example.sasha.singletask;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SignInFragment extends Fragment {
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

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setSignUpFragment();
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(EMAIL_KEY)) {
            emailEditText.setText(savedInstanceState.getString(EMAIL_KEY));
            passwordEditText.setText(savedInstanceState.getString(PASSWORD_KEY));
        }

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
}