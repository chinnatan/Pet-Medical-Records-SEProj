package com.brainnotfound.g04.petmedicalrecords;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.module.Profile;
import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileEditFragment extends Fragment {

    private Profile profile;
    private SaveFragment saveFragment;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_profile_edit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        profile = Profile.getProfileInstance();
        saveFragment = SaveFragment.getSaveFragmentInstance();
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        String userUid = mAuth.getCurrentUser().getUid();

        saveFragment.setName("ProfileEditFragment");
        putProfileInEditText(profile);
        initSubmitBtn(userUid);
        initBackBtn();
    }

    void putProfileInEditText(Profile profile) {
        EditText _firstnameTxt = getView().findViewById(R.id.frg_menu_profile_edit_firstname);
        EditText _lastnameTxt = getView().findViewById(R.id.frg_menu_profile_edit_lastname);
        EditText _phonenumberTxt = getView().findViewById(R.id.frg_menu_profile_edit_phonenumber);

        _firstnameTxt.setText(profile.getFirstname());
        _lastnameTxt.setText(profile.getLastname());
        _phonenumberTxt.setText(profile.getPhonenumber());
    }

    void updateProfile(String userUid) {
        EditText _firstnameTxt = getView().findViewById(R.id.frg_menu_profile_edit_firstname);
        EditText _lastnameTxt = getView().findViewById(R.id.frg_menu_profile_edit_lastname);
        EditText _phonenumberTxt = getView().findViewById(R.id.frg_menu_profile_edit_phonenumber);

        String _firstnameStr = _firstnameTxt.getText().toString();
        String _lastnameStr = _lastnameTxt.getText().toString();
        String _phonenumberStr = _phonenumberTxt.getText().toString();

        if(_firstnameStr.isEmpty() || _lastnameStr.isEmpty() || _phonenumberStr.isEmpty()) {
            Toast.makeText(getActivity(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_LONG).show();
        } else {
            mStore.collection("account").document(userUid)
                    .collection("profile").document(userUid)
                    .update("firstname", _firstnameStr,
                            "lastname", _lastnameStr,
                            "phonenumber", _phonenumberStr);
            profile.setFirstname(_firstnameStr);
            profile.setLastname(_lastnameStr);
            profile.setPhonenumber(_phonenumberStr);
        }
    }

    void initSubmitBtn(final String userUid) {
        Button _submitBtn = getView().findViewById(R.id.frg_menu_profile_edit_submitBtn);
        _submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(userUid);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new ProfileFragment()).commit();
            }
        });
    }

    void initBackBtn() {
        ImageView _backBtn = getView().findViewById(R.id.frg_menu_profile_edit_backBtn);
        _backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new ProfileFragment()).commit();
            }
        });
    }
}
