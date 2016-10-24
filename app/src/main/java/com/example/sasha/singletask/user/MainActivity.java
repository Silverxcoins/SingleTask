package com.example.sasha.singletask.user;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.sasha.singletask.R;
import com.example.sasha.singletask.helpers.Utils;

public class MainActivity extends AppCompatActivity {
    private static final String FRAGMENT_SIGN_IN_KEY = "fragmentSignIn";
    private static final String FRAGMENT_SIGN_UP_KEY = "fragmentSignUp";

    private Fragment signInFragment;
    private Fragment signUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SharedPreferences settings = getSharedPreferences(getString(R.string.PREFS_NAME),0);
//        if (settings.getBoolean("isSignedIn", false)) {
//            Intent intent = new Intent(this, ChoiceActivity.class);
//            startActivity(intent);
//            finish();
//        }

        if (savedInstanceState != null && savedInstanceState.containsKey(FRAGMENT_SIGN_UP_KEY)) {
            signUpFragment = getSupportFragmentManager()
                    .getFragment(savedInstanceState, FRAGMENT_SIGN_UP_KEY);
        } else {
            signUpFragment = new SignUpFragment();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(FRAGMENT_SIGN_IN_KEY)) {
            signInFragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_SIGN_IN_KEY);
        } else {
            signInFragment = new SignInFragment();
        }

        setSignInFragment();
        if (savedInstanceState != null && savedInstanceState.containsKey(FRAGMENT_SIGN_UP_KEY)) {
            setSignUpFragment();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Utils.clearBackStack(this);
        if (signInFragment != null)
            getSupportFragmentManager().putFragment(outState,FRAGMENT_SIGN_IN_KEY, signInFragment);
        if (signUpFragment != null && signUpFragment.isVisible())
            getSupportFragmentManager().putFragment(outState,FRAGMENT_SIGN_UP_KEY, signUpFragment);
        super.onSaveInstanceState(outState);
    }

    private void setSignInFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
        ft.replace(R.id.authFragmantContainer, signInFragment);
        ft.commit();
    }

    public void setSignUpFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left,
                R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.authFragmantContainer, signUpFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
