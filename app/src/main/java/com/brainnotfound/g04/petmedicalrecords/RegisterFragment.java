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
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.module.Profile;
import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment {

    SaveFragment _saveFragment = SaveFragment.getSaveFragmentInstance();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        _saveFragment.setName("registerFragment");

        initRegisterBtn();
    }

    void getInformationNewAccount() {
        EditText _emailTxt = getView().findViewById(R.id.register_emailTxt);
        EditText _passwordTxt = getView().findViewById(R.id.register_passwordTxt);
        EditText _password2Txt = getView().findViewById(R.id.register_password2Txt);
        EditText _firstnameTxt = getView().findViewById(R.id.register_firstnameTxt);
        EditText _lastnameTxt = getView().findViewById(R.id.register_lastnameTxt);
        EditText _phonenumberTxt = getView().findViewById(R.id.register_phonenumberTxt);

        String _emailStr = _emailTxt.getText().toString();
        String _passwordStr = _passwordTxt.getText().toString();
        String _password2Str = _password2Txt.getText().toString();
        final String _firstnameStr = _firstnameTxt.getText().toString();
        final String _lastnameStr = _lastnameTxt.getText().toString();
        final String _phonenumberStr = _phonenumberTxt.getText().toString();

        if(_emailStr.isEmpty() || _passwordStr.isEmpty() || _password2Str.isEmpty() ||
                _firstnameStr.isEmpty() || _lastnameStr.isEmpty() || _phonenumberStr.isEmpty()) {
            Toast.makeText(getActivity(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        } else if(!_passwordStr.equals(_password2Str) || !_password2Str.equals(_passwordStr)) {
            Toast.makeText(getActivity(), "กรุณากรอกรหัสผ่านทั้ง 2 ช่องให้ตรงกัน", Toast.LENGTH_LONG).show();
        } else if(_passwordStr.length() < 6 || _password2Str.length() < 6) {
            Toast.makeText(getActivity(), "กรุณากรอกรหัสผ่านมากกว่า 6 ตัวอักษร", Toast.LENGTH_LONG).show();
        } else if(_phonenumberStr.length() <= 9) {
            Toast.makeText(getActivity(), "กรุณากรอกเบอร์โทรศัพท์ให้ครบถ้วน", Toast.LENGTH_LONG).show();
        } else {
            mAuth.createUserWithEmailAndPassword(_emailStr, _passwordStr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    sendVerifiedEmail(authResult.getUser());
                    Profile profileToDatabase = new Profile(_firstnameStr, _lastnameStr, _phonenumberStr, "customer");
                    mStore.collection("account").document(authResult.getUser().getUid())
                            .collection("profile").document(authResult.getUser().getUid())
                            .set(profileToDatabase).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mAuth.signOut();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new IntroFragment()).commit();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "ERROR - " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
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

    void initRegisterBtn() {
        Button _registerBtn = getView().findViewById(R.id.register_registerBtn);
        _registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInformationNewAccount();
            }
        });
    }
}
