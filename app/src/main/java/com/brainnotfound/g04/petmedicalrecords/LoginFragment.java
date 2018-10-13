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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        checkCurrentUser();
        initLoginBtn();
    }


    void getEmailAndPasswordToLogin() {
        EditText _emailTxt = getView().findViewById(R.id.login_emailTxt);
        EditText _passwordTxt = getView().findViewById(R.id.login_passwordTxt);

        String _emailStr = _emailTxt.getText().toString();
        String _passwordStr = _passwordTxt.getText().toString();

        if(_emailStr.isEmpty() || _passwordStr.isEmpty()) {
            Toast.makeText(getActivity(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
        } else if(_passwordStr.length() < 6) {
            Toast.makeText(getActivity(), "กรุณากรอกรหัสผ่านมากกว่า 6 ตัวอักษร", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(_emailStr, _passwordStr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if(authResult.getUser().isEmailVerified()) {
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new MenuFragment()).commit();
                    } else {
                        Toast.makeText(getActivity(), "กรุณายืนยัน email", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "ERROR - " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    void checkCurrentUser() {
        if(mAuth.getCurrentUser() != null) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new MenuFragment()).commit();
        }
    }

    void initLoginBtn() {
        Button _loginBtn = getView().findViewById(R.id.login_loginBtn);
        _loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEmailAndPasswordToLogin();
            }
        });
    }
}
