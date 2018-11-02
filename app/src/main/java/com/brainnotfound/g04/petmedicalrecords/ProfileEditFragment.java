package com.brainnotfound.g04.petmedicalrecords;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.module.Profile;
import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileEditFragment extends Fragment {

    private Profile profile;
    private SaveFragment saveFragment;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private ImageView imageViewProfile;
    private Uri uri;
    private ProgressDialog csprogress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_profile_edit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        csprogress = new ProgressDialog(getActivity());
        profile = Profile.getProfileInstance();
        saveFragment = SaveFragment.getSaveFragmentInstance();
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        imageViewProfile = getView().findViewById(R.id.frg_menu_profile_edit_image);

        String userUid = mAuth.getCurrentUser().getUid();

        saveFragment.setName("ProfileEditFragment");
        putProfileInEditText(profile);
        imageController();
        initSubmitBtn(userUid);
        initBackBtn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK) {
            try {
                uri = data.getData();
                Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(imageViewProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void putProfileInEditText(Profile profile) {
        EditText _firstnameTxt = getView().findViewById(R.id.frg_menu_profile_edit_firstname);
        EditText _lastnameTxt = getView().findViewById(R.id.frg_menu_profile_edit_lastname);
        EditText _phonenumberTxt = getView().findViewById(R.id.frg_menu_profile_edit_phonenumber);

        _firstnameTxt.setText(profile.getFirstname());
        _lastnameTxt.setText(profile.getLastname());
        _phonenumberTxt.setText(profile.getPhonenumber());
        storageReference.child(profile.getUrlImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(imageViewProfile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("LOADIMAGEPROFILE", e.getMessage());
            }
        });
    }

    private void imageController() {
       imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "กรุณาเลือกรูปภาพสัตว์เลี้ยงของคุณ"), 1);
            }
        });
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
            storageReference = mStorage.getReference().child("images/avatars/" + mAuth.getCurrentUser().getEmail() + "/" + _firstnameStr);
            profile.setFirstname(_firstnameStr);
            profile.setLastname(_lastnameStr);
            profile.setPhonenumber(_phonenumberStr);

            if(uri == null) {
                csprogress.dismiss();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new ProfileFragment()).commit();
            } else {
                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        csprogress.dismiss();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                .replace(R.id.main_view, new ProfileFragment()).commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        csprogress.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("UPDATEPROFILEIMAGE", e.getMessage());
                    }
                });
            }
        }
    }

    void initSubmitBtn(final String userUid) {
        Button _submitBtn = getView().findViewById(R.id.frg_menu_profile_edit_submitBtn);
        _submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csprogress.setMessage("Updating...");
                csprogress.show();
                updateProfile(userUid);
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
