package com.brainnotfound.g04.petmedicalrecords;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainnotfound.g04.petmedicalrecords.module.Profile;

public class ProfileFragment extends Fragment {

    private Profile _profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _profile = Profile.getProfileInstance();

        getProfile(_profile);

        initBackBtn();
    }

    void getProfile(Profile profile) {
        TextView _nameView = getView().findViewById(R.id.frg_menu_profile_name);
        TextView _phonenumberView = getView().findViewById(R.id.frg_menu_profile_phonenumber);
        TextView _ruleView = getView().findViewById(R.id.frg_menu_profile_rule);

        _nameView.setText(" " + profile.getFirstname() + " " + profile.getLastname());
        _phonenumberView.setText(" " + profile.getPhonenumber());
        _ruleView.setText(" " + profile.getAccount_type());
    }

    void initBackBtn() {
        ImageView _backBtn = getView().findViewById(R.id.frg_menu_profile_backBtn);
        _backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new MenuFragment()).commit();
            }
        });
    }
}
