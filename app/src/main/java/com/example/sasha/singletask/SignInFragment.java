package com.example.sasha.singletask;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SignInFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, null);

        Button signUpButton = (Button) view.findViewById(R.id.buttonGoToSignUp);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left,
                        R.animator.slide_out_right, R.animator.slide_in_right);
                ft.replace(R.id.authFragmantContainer, new SignUpFragment());
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        return view;
    }
}