package com.brainnotfound.g04.petmedicalrecords;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;
import com.google.firebase.auth.FirebaseAuth;

public class IntroFragment extends Fragment {

    private FirebaseAuth mAuth;
    private SaveFragment saveFragment = SaveFragment.getSaveFragmentInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        checkCurrentUser();

        Button _registerBtn = getView().findViewById(R.id.intro_registerBtn);
        _registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveFragment.setName("LoginFragment");

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new RegisterFragment()).addToBackStack(null).commit();
            }
        });

        Button _loginBtn = getView().findViewById(R.id.intro_loginBtn);
        _loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveFragment.setName("LoginFragment");

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new LoginFragment()).addToBackStack(null).commit();
            }
        });
    }

    void checkCurrentUser() {
        if(mAuth.getCurrentUser() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new MenuFragment()).commit();
        }
    }
}
