package com.example.sasha.singletask;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class MainActivity extends Activity {
    private static final String FRAGMENT_SIGN_IN_KEY = "fragmentSignIn";
    private static final String FRAGMENT_SIGN_UP_KEY = "fragmentSignUp";

    private Fragment signInFragment;
    private Fragment signUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null && savedInstanceState.containsKey(FRAGMENT_SIGN_UP_KEY)) {
            signUpFragment = getFragmentManager()
                    .getFragment(savedInstanceState, FRAGMENT_SIGN_UP_KEY);
        } else {
            signUpFragment = new SignUpFragment();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(FRAGMENT_SIGN_IN_KEY)) {
            signInFragment = getFragmentManager().getFragment(savedInstanceState, FRAGMENT_SIGN_IN_KEY);
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
        clearBackStack();
        if (signInFragment != null)
            getFragmentManager().putFragment(outState,FRAGMENT_SIGN_IN_KEY, signInFragment);
        if (signUpFragment != null && signUpFragment.isVisible())
            getFragmentManager().putFragment(outState,FRAGMENT_SIGN_UP_KEY, signUpFragment);
        super.onSaveInstanceState(outState);
    }

    private void setSignInFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left);
        ft.replace(R.id.authFragmantContainer, signInFragment);
        ft.commit();
    }

    public void setSignUpFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left,
                R.animator.slide_out_right, R.animator.slide_in_right);
        ft.replace(R.id.authFragmantContainer, signUpFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
