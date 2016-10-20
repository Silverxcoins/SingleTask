package com.example.sasha.singletask;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
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

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SignUpFragment extends Fragment implements UsersManager.SignUpCallback {
    private static String TAG = "SignUpFragment";

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
                Log.d(TAG, "Sign Up Button Clicked");
                if (Http.isNetworkAvailable(getActivity())) {
                    UsersManager.getInstance().signUp(
                            emailEditText.getText().toString(),
                            passwordEditText.getText().toString()
                    );
                } else {
                    Toast.makeText(getActivity(), R.string.NO_INTERNET_CONNECTION,
                            Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void onSignUpFinished(String jsonString) {
        Log.d(TAG, "onSignUpFinished()");
        if (jsonString == null) {
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject json;
        try {
            json = new JSONObject(jsonString);
            int code = json.getInt("code");
            if (code == Http.OK) {
                Toast.makeText(getActivity(), R.string.SIGN_UP_SUCCESSFULL,
                        Toast.LENGTH_SHORT).show();
            } else if (code == Http.ALREADY_EXIST) {
                Toast.makeText(getActivity(), R.string.EMAIL_ALREADY_IN_USE,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(), R.string.SOMETHING_WRONG, Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
