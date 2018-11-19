package com.brainnotfound.g04.petmedicalrecords.control;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;


public class RegisterFragment extends Fragment {

    private static final String TAG = "REGISTER";
    private final String title = "ลงทะเบียน";

    private Uri uriImage;
    private Uri imageUpload;
    private Toolbar mToolBar;
    private ImageView mImageViewMe;
    private ImageView mImageChoose;
    private TextInputEditText mEmail;
    private TextInputEditText mPassword;
    private TextInputEditText mRepassword;
    private TextInputEditText mFullname;
    private TextInputEditText mPhonenumber;
    private Button mSubmit;
    private ProgressDialog progressDialog;
    private Bitmap selectedImage;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_register, container, false);
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
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        registerFragmentElements();
        createMenu();
        initSubmitBtn();
        imageController();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK) {
            try {
                uriImage = data.getData();
                InputStream imageStream = getActivity().getContentResolver().openInputStream(uriImage);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 400);// 400 is for example, replace with desired size
                Glide.with(getActivity()).load(selectedImage).apply(RequestOptions.circleCropTransform()).into(mImageViewMe);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void createMenu() {
        Log.d(TAG, "create menu");
        Objects.requireNonNull(getActivity()).setActionBar(mToolBar);
        mToolBar.setTitle(title);
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolBar.setNavigationContentDescription("Back");
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void registerFragmentElements() {
        mToolBar = getView().findViewById(R.id.register_action_bar);
        mImageViewMe = getView().findViewById(R.id.frg_reg_imageme);
        mImageChoose = getView().findViewById(R.id.frg_reg_imageme_click);
        mEmail = getView().findViewById(R.id.frg_reg_email);
        mPassword = getView().findViewById(R.id.frg_reg_password);
        mRepassword = getView().findViewById(R.id.frg_reg_repassword);
        mFullname = getView().findViewById(R.id.frg_reg_fullname);
        mPhonenumber = getView().findViewById(R.id.frg_reg_phonenumber);
        mSubmit = getView().findViewById(R.id.frg_reg_submitbutton);
    }

    private void initSubmitBtn() {
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick : clicked");

                progressDialog.show();

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String repassword = mRepassword.getText().toString();
                final String fullname = mFullname.getText().toString();
                final String phonenumber = mPhonenumber.getText().toString();
                if (email.isEmpty() || password.isEmpty() || repassword.isEmpty() || fullname.isEmpty() || phonenumber.isEmpty()) {
                    Toast.makeText(getActivity(),
                            "กรุณากรอกข้อมูลให้ครบถ้วน",
                            Toast.LENGTH_SHORT)
                            .show();
                    progressDialog.dismiss();
                } else if (password.length() < 6) {
                    Toast.makeText(getActivity(),
                            "รหัสผ่านต้องมีความยาว 6 ตัวอักษรขึ้นไป",
                            Toast.LENGTH_SHORT)
                            .show();
                    progressDialog.dismiss();
                } else if (!password.equals(repassword)) {
                    Toast.makeText(getActivity(),
                            "รหัสผ่านทั้ง 2 ช่องต้องเหมือนกัน",
                            Toast.LENGTH_LONG)
                            .show();
                    progressDialog.dismiss();
                } else if(mImageViewMe.getDrawable() == null) {
                    Toast.makeText(getActivity(),
                            "กรุณาเลือกภาพโปรไฟล์",
                            Toast.LENGTH_LONG)
                            .show();
                    progressDialog.dismiss();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "createUserWithEmail : success");
                                        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                        Log.d(TAG, "sending verification email");
                                        firebaseUser.sendEmailVerification();

                                        Log.d(TAG, "set path for upload image");

                                        Log.d(TAG, "uploading image to firebase");

                                        StorageMetadata metadata = new StorageMetadata.Builder()
                                                .setContentType("image/jpeg")
                                                .build();

                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] dataImage = baos.toByteArray();

                                        final StorageReference uploadTask;
                                        uploadTask = storageReference.child("images/avatars/" + firebaseUser.getUid() + "/" + firebaseUser.getUid());

                                        uploadTask.putBytes(dataImage, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                Log.d(TAG, "upload image : success");

                                                User user = User.getUserInstance();
                                                user.setUid(firebaseUser.getUid());
                                                user.setFullname(fullname);
                                                user.setPhonenumber(phonenumber);
                                                user.setType("เจ้าของสัตว์เลี้ยง");
                                                user.setImage(uploadTask.getPath());

                                                Log.d(TAG, "submitting insert profile to firebase");
                                                firebaseFirestore.collection("account").document(firebaseUser.getUid())
                                                        .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "submit : success");
                                                        firebaseAuth.signOut();
                                                        progressDialog.dismiss();
                                                        displaySuccessDialog();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "submit : failed (" + e.getMessage() + ")");
                                                        firebaseAuth.signOut();
                                                        progressDialog.dismiss();
                                                        displayFailureDialog(e.getMessage());
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "upload image : failed");
                                                firebaseAuth.signOut();
                                                progressDialog.dismiss();
                                                displayFailureDialog("ขออภัย, อัพโหลดรูปภาพไม่สำเร็จ");
                                            }
                                        });
                                    } else {
                                        Log.d(TAG, "createUserWithEmail : failed", task.getException());
                                        firebaseAuth.signOut();
                                        progressDialog.dismiss();
                                        displayFailureDialog(task.getException().getLocalizedMessage());
                                    }
                                }
                            });
                }
            }
        });
    }

    private void imageController() {
        mImageChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "กรุณาเลือกรูปภาพของคุณ"), 1);
            }
        });
    }

    private void displaySuccessDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("ตรวจสอบอีเมล์ที่ทำการลงทะเบียน")
                .setMessage("ลงทะเบียนสำเร็จ. โปรดคลิกลิงก์ที่พวกเราส่งให้ในอีเมล์ของคุณเพื่อยืนยันการลงทะเบียน.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_view, new LoginFragment())
                                .commit();
                    }
                })
                .show();
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
