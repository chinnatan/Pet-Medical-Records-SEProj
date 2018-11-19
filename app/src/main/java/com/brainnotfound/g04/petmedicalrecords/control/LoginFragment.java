package com.brainnotfound.g04.petmedicalrecords.control;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {

    private static final String TAG = "LOGIN";

    private ProgressDialog progressDialog;
    private TextView zRegister;
    private TextInputEditText zEmail;
    private TextInputEditText zPassword;
    private Button zLoginBtn;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("กำลังดำเนินการ...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseFirestore = FirebaseFirestore.getInstance();

        loginFragmentElements();
        initLoginBtn();
        initRegisterBtn();
    }

    private void initRegisterBtn() {
        zRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : register clicked");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new RegisterFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void initLoginBtn() {
        zLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : login clicked");

                progressDialog.show();

                String email = zEmail.getText().toString();
                String password = zPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(),
                            "กรุณากรอกข้อมูลให้ครบถ้วน",
                            Toast.LENGTH_SHORT)
                            .show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Log.d(TAG, "check verified email");
                                    if(authResult.getUser().isEmailVerified()) {
                                        Log.d(TAG, "Sign in success");
                                        firebaseFirestore.collection("account").document(firebaseAuth.getCurrentUser().getUid())
                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Log.d(TAG, "Load success");

                                                User user = User.getUserInstance();
                                                user.setUid(documentSnapshot.getString("uid"));
                                                user.setFullname(documentSnapshot.getString("fullname"));
                                                user.setPhonenumber(documentSnapshot.getString("phonenumber"));
                                                user.setImage(documentSnapshot.getString("image"));
                                                progressDialog.dismiss();
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new HomeFragment()).commit();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "Load information fails : " + e.getMessage());
                                                progressDialog.dismiss();
                                                Toast.makeText(getActivity(),
                                                        e.getMessage(),
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "check verified email is failed");
                                        firebaseAuth.signOut();
                                        displayFailureDialog("โปรดยืนยันการลงทะเบียน");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Sign in fails : " + e.getMessage());
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),
                                    e.getMessage(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            }
        });
    }

    private void loginFragmentElements() {
        zRegister = getView().findViewById(R.id.frg_login_registerbtn);
        zEmail = getView().findViewById(R.id.frg_login_email);
        zPassword = getView().findViewById(R.id.frg_login_password);
        zLoginBtn = getView().findViewById(R.id.frg_login_loginbtn);
    }

    private void displayFailureDialog(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("เกิดข้อผิดพลาด")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}
