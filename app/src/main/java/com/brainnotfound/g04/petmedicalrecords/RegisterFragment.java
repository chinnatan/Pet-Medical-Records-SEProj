package com.brainnotfound.g04.petmedicalrecords;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterFragment extends Fragment {

    SaveFragment _saveFragment = SaveFragment.getSaveFragmentInstance();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private ProgressDialog csprogress;
    private Uri uri;
    private ImageView _profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        csprogress = new ProgressDialog(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        _saveFragment.setName("registerFragment");

        imageController();
        initRegisterBtn();
        initBackBtn();
    }

    private void imageController() {
        _profileImage = getView().findViewById(R.id.register_profileImage);
        _profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "กรุณาเลือกรูปภาพสัตว์เลี้ยงของคุณ"), 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK) {
            try {
                uri = data.getData();
                Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(_profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getInformationNewAccount() {
        EditText _emailTxt = getView().findViewById(R.id.register_emailTxt);
        EditText _passwordTxt = getView().findViewById(R.id.register_passwordTxt);
        EditText _password2Txt = getView().findViewById(R.id.register_password2Txt);
        EditText _firstnameTxt = getView().findViewById(R.id.register_firstnameTxt);
        EditText _lastnameTxt = getView().findViewById(R.id.register_lastnameTxt);
        EditText _phonenumberTxt = getView().findViewById(R.id.register_phonenumberTxt);

        final String _emailStr = _emailTxt.getText().toString();
        String _passwordStr = _passwordTxt.getText().toString();
        String _password2Str = _password2Txt.getText().toString();
        final String _firstnameStr = _firstnameTxt.getText().toString();
        final String _lastnameStr = _lastnameTxt.getText().toString();
        final String _phonenumberStr = _phonenumberTxt.getText().toString();

        if(_emailStr.isEmpty() || _passwordStr.isEmpty() || _password2Str.isEmpty() ||
                _firstnameStr.isEmpty() || _lastnameStr.isEmpty() || _phonenumberStr.isEmpty()) {
            csprogress.dismiss();
            Toast.makeText(getActivity(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        } else if(!_passwordStr.equals(_password2Str) || !_password2Str.equals(_passwordStr)) {
            csprogress.dismiss();
            Toast.makeText(getActivity(), "กรุณากรอกรหัสผ่านทั้ง 2 ช่องให้ตรงกัน", Toast.LENGTH_LONG).show();
        } else if(_passwordStr.length() < 6 || _password2Str.length() < 6) {
            csprogress.dismiss();
            Toast.makeText(getActivity(), "กรุณากรอกรหัสผ่านมากกว่า 6 ตัวอักษร", Toast.LENGTH_LONG).show();
        } else if(_phonenumberStr.length() <= 9) {
            csprogress.dismiss();
            Toast.makeText(getActivity(), "กรุณากรอกเบอร์โทรศัพท์ให้ครบถ้วน", Toast.LENGTH_LONG).show();
        } else if(_profileImage.getDrawable() == null) {
            csprogress.dismiss();
            Toast.makeText(getActivity(), "กรุณาเลือกภาพโปรไฟล์ของคุณ", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(_emailStr, _passwordStr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    sendVerifiedEmail(authResult.getUser());
                    storageReference = mStorage.getReference().child("images/avatars/" + _emailStr + "/" + _firstnameStr);
                    Profile profileToDatabase = new Profile(_firstnameStr, _lastnameStr, _phonenumberStr, "customer", storageReference.getPath());
                    mStore.collection("account").document(authResult.getUser().getUid())
                            .collection("profile").document(authResult.getUser().getUid())
                            .set(profileToDatabase).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    csprogress.dismiss();
                                    mAuth.signOut();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new IntroFragment()).commit();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    csprogress.dismiss();
                                    Toast.makeText(getActivity(), "ERROR - " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            csprogress.dismiss();
                            Toast.makeText(getActivity(), "ERROR - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    csprogress.dismiss();
                    Toast.makeText(getActivity(), "ERROR - " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void sendVerifiedEmail(FirebaseUser _user) {
        _user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "กรุณาตรวจสอบ email ที่ทำการสมัครใช้งาน", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "ERROR - " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRegisterBtn() {
        Button _registerBtn = getView().findViewById(R.id.register_registerBtn);
        _registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csprogress.setMessage("Registering");
                csprogress.show();
                getInformationNewAccount();
            }
        });
    }

    private void initBackBtn() {
        Button _backBtn = getView().findViewById(R.id.register_backBtn);
        _backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new IntroFragment()).commit();
            }
        });
    }
}
