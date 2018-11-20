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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

public class MyEditFragment extends Fragment {

    private static final String TAG = "MYEDIT";
    private final String title = "แก้ไขข้อมูล";

    private Toolbar zToolBar;
    private ImageView zImageViewMe;
    private ImageView zImageViewClick;
    private TextInputLayout zFullnameLayout;
    private TextInputLayout zPhonenumberLayout;
    private TextInputEditText zFullname;
    private TextInputEditText zPhonenumber;
    private Button zSaveBtn;
    private ProgressBar zLoading;
    private ProgressDialog zLoadingDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private User user;
    private Uri uriImage;
    private Bitmap selectedImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        return inflater.inflate(R.layout.fragment_my_edit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);

        zLoadingDialog = new ProgressDialog(getActivity());

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        user = User.getUserInstance();

        myeditFragmentElements();
        imageController();
        createMenu();
        loadMy();
        initSaveBtn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            try {
                uriImage = data.getData();
                InputStream imageStream = getActivity().getContentResolver().openInputStream(uriImage);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 400);// 400 is for example, replace with desired size
                Glide.with(getActivity()).load(selectedImage).apply(RequestOptions.circleCropTransform()).into(zImageViewMe);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void imageController() {
        zImageViewClick.setOnClickListener(new View.OnClickListener() {
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
        builder.setTitle(title)
                .setMessage("บันทึกข้อมูลเรียบร้อย")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_view, new MyFragment())
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

    private void myeditFragmentElements() {
        zToolBar = getView().findViewById(R.id.toolbar);
        zImageViewMe = getView().findViewById(R.id.frg_myedit_image);
        zImageViewClick = getView().findViewById(R.id.frg_myedit_image_click);
        zFullname = getView().findViewById(R.id.frg_myedit_fullname);
        zPhonenumber = getView().findViewById(R.id.frg_myedit_phonenumber);
        zSaveBtn = getView().findViewById(R.id.frg_myedit_savebtn);
        zLoading = getView().findViewById(R.id.myedit_loading);

        zFullnameLayout = getView().findViewById(R.id.frg_myedit_fullname_layout);
        zPhonenumberLayout = getView().findViewById(R.id.frg_myedit_phonenumber_layout);
    }

    private void createMenu() {
        Log.d(TAG, "create menu");
        Objects.requireNonNull(getActivity()).setActionBar(zToolBar);
        zToolBar.setTitle(title);
        zToolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        zToolBar.setNavigationContentDescription("Back");
        zToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadMy() {
        Log.d(TAG, "load my");
        storageReference.child(user.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "load image and my : success");
                if (isAdded()) {
                    Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(zImageViewMe);
                    zFullname.setText(user.getFullname());
                    zPhonenumber.setText(user.getPhonenumber());

                    zFullnameLayout.setVisibility(View.VISIBLE);
                    zPhonenumberLayout.setVisibility(View.VISIBLE);

                    zImageViewMe.setVisibility(View.VISIBLE);
                    zImageViewClick.setVisibility(View.VISIBLE);
                    zFullname.setVisibility(View.VISIBLE);
                    zPhonenumber.setVisibility(View.VISIBLE);
                    zSaveBtn.setVisibility(View.VISIBLE);
                }
                zLoading.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "load image failed : " + e.getMessage());
            }
        });
    }

    private void initSaveBtn() {
        zSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zLoadingDialog.setMessage("กำลังบันทึกข้อมูล...");
                zLoadingDialog.setCancelable(false);
                zLoadingDialog.setCanceledOnTouchOutside(false);
                zLoadingDialog.show();

                final String fullname = zFullname.getText().toString();
                final String phonenumber = zPhonenumber.getText().toString();

                if (uriImage == null) {
                    firebaseFirestore.collection("account").document(user.getUid())
                            .update("fullname", fullname,
                                    "phonenumber", phonenumber)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    user.setFullname(fullname);
                                    user.setPhonenumber(phonenumber);
                                    displaySuccessDialog();
                                    zLoadingDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            displayFailureDialog(e.getMessage());
                            zLoadingDialog.dismiss();
                        }
                    });
                } else {
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("image/jpeg")
                            .build();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] dataImage = baos.toByteArray();

                    final StorageReference uploadTask;
                    uploadTask = storageReference.child("images/avatars/" + user.getUid() + "/" + user.getUid());

                    uploadTask.putBytes(dataImage, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            firebaseFirestore.collection("account").document(user.getUid())
                                    .update("fullname", fullname,
                                            "phonenumber", phonenumber)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            user.setFullname(fullname);
                                            user.setPhonenumber(phonenumber);
                                            user.setImage(uploadTask.getPath());
                                            displaySuccessDialog();
                                            zLoadingDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    displayFailureDialog(e.getMessage());
                                    zLoadingDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            displayFailureDialog(e.getMessage());
                            zLoadingDialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}
