package com.brainnotfound.g04.petmedicalrecords.control.petowner;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.HomeFragment;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

public class EditPetFragment extends Fragment {

    private static final String TAG = "EDITPET";
    private final String title = "แก้ไขข้อมูลสัตว์เลี้ยง";

    private Toolbar zToolBar;
    private ImageView zImageViewPet;
    private ImageView zImageViewClick;
    private RadioGroup zPettype;
    private RadioGroup zPetsex;
    private TextInputLayout zPetnameLayout;
    private TextInputEditText zPetname;
    private Spinner zYear;
    private Spinner zMonth;
    private Spinner zDay;
    private Button zSaveBtn;
    private ProgressBar zLoading;
    private ProgressDialog zLoadingDialog;

    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private Pet pet;
    private User user;
    private Uri uriImage;
    private Bitmap selectedImage;

    private ArrayList<Integer> zYearData = new ArrayList<Integer>();
    private ArrayList<Integer> zMonthData = new ArrayList<Integer>();
    private ArrayList<Integer> zDayData = new ArrayList<Integer>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_editpet, container, false);
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
        pet = Pet.getPetInstance();

        editpetFragmentElements();
        createMenu();
        createSpinner();
        createSpinnerAdapter();
        loadPet();
        initSaveBtn();
        imageController();
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
                Glide.with(getActivity()).load(selectedImage).apply(RequestOptions.circleCropTransform()).into(zImageViewPet);
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
                startActivityForResult(Intent.createChooser(intent, "กรุณาเลือกรูปภาพสัตว์เลี้ยงของคุณ"), 1);
            }
        });
    }

    private void displaySuccessDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("แก้ไขข้อมูลสัตว์เลี้ยง")
                .setMessage("แก้ไขข้อมูลสำเร็จ.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity()
                                .getSupportFragmentManager()
                                .popBackStack();
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

    private void createSpinner() {
        zYearData.add(Integer.parseInt(pet.getPetyear()));
        for (int year = 0; year <= 15; year++) {
            if(!pet.getPetyear().equals(String.valueOf(year))) {
                zYearData.add(year);
            }
        }
        zMonthData.add(Integer.parseInt(pet.getPetmonth()));
        for (int month = 0; month <= 12; month++) {
            if(!pet.getPetmonth().equals(String.valueOf(month))) {
                zMonthData.add(month);
            }
        }
        zDayData.add(Integer.parseInt(pet.getPetday()));
        for (int day = 0; day <= 15; day++) {
            if(!pet.getPetday().equals(String.valueOf(day))) {
                zDayData.add(day);
            }
        }
    }

    private void createSpinnerAdapter() {
        ArrayAdapter<Integer> zAdapterYear = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_dropdown_item_1line, zYearData);
        ArrayAdapter<Integer> zAdapterMonth = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_dropdown_item_1line, zMonthData);
        ArrayAdapter<Integer> zAdapterDay = new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_dropdown_item_1line, zDayData);
        zYear.setAdapter(zAdapterYear);
        zMonth.setAdapter(zAdapterMonth);
        zDay.setAdapter(zAdapterDay);
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

    private void editpetFragmentElements() {
        zToolBar = getView().findViewById(R.id.toolbar);
        zImageViewPet = getView().findViewById(R.id.frg_editpet_image);
        zImageViewClick = getView().findViewById(R.id.frg_editpet_image_click);
        zPettype = getView().findViewById(R.id.frg_editpet_typegroup);
        zPetsex = getView().findViewById(R.id.frg_editpet_sexgroup);
        zPetnameLayout = getView().findViewById(R.id.frg_editpet_petname_layout);
        zPetname = getView().findViewById(R.id.frg_editpet_petname);
        zYear = getView().findViewById(R.id.frg_editpet_year);
        zMonth = getView().findViewById(R.id.frg_editpet_month);
        zDay = getView().findViewById(R.id.frg_editpet_day);
        zLoading = getView().findViewById(R.id.editpet_loading);
        zSaveBtn = getView().findViewById(R.id.frg_editpet_savebtn);
    }

    private void loadPet() {
        storageReference.child(pet.getPetimage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(isAdded()) {
                    Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(zImageViewPet);
                    zPetname.setText(pet.getPetname());

                    if(pet.getPettype().equals("สุนัข")) {
                        zPettype.check(R.id.frg_editpet_dog);
                    } else {
                        zPettype.check(R.id.frg_editpet_cat);
                    }

                    if(pet.getPetsex().equals("ผู้")) {
                        zPetsex.check(R.id.frg_editpet_sex_male);
                    } else {
                        zPetsex.check(R.id.frg_editpet_sex_female);
                    }

                    zYear.setSelection(0);
                    zMonth.setSelection(0);
                    zDay.setSelection(0);

                    zLoading.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "LOAD IMAGE ERROR : " + e.getMessage());
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

                int typeId = zPettype.getCheckedRadioButtonId();
                int sexId = zPetsex.getCheckedRadioButtonId();
                RadioButton zTypeRadio = getView().findViewById(typeId);
                RadioButton zSexRadio = getView().findViewById(sexId);
                final String petname = zPetname.getText().toString();
                final String pettype = zTypeRadio.getText().toString();
                final String petsex = zSexRadio.getText().toString();
                final String year = zYear.getSelectedItem().toString();
                final String month = zMonth.getSelectedItem().toString();
                final String day = zDay.getSelectedItem().toString();

                if(petname.isEmpty() || pettype.isEmpty() || petsex.isEmpty() || year.isEmpty() || month.isEmpty() || day.isEmpty()) {
                    Toast.makeText(getActivity(),
                            "กรุณาใส่ข้อมูลให้ครบถ้วน",
                            Toast.LENGTH_LONG).show();
                    zLoadingDialog.dismiss();
                } else if (year.equals("0") && month.equals("0") && day.equals("0")) {
                    Toast.makeText(getActivity(),
                            "กรุณาเลือกอายุสัตว์ลี้ยงของคุณ",
                            Toast.LENGTH_LONG).show();
                    zLoadingDialog.dismiss();
                } else {
                    if(uriImage == null) {
                        firebaseFirestore.collection("pet").document(pet.getPetkey())
                                .update("petname", petname,
                                        "petsex", petsex,
                                        "pettype", pettype,
                                        "petyear", year,
                                        "petmonth", month,
                                        "petday", day)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pet.setPetname(petname);
                                        pet.setPettype(pettype);
                                        pet.setPetsex(petsex);
                                        pet.setPetyear(year);
                                        pet.setPetmonth(month);
                                        pet.setPetday(day);
                                        zLoadingDialog.dismiss();
                                        displaySuccessDialog();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "UPDATE ERROR : " + e.getMessage());
                                zLoadingDialog.dismiss();
                                displayFailureDialog(e.getMessage());
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
                        uploadTask = storageReference.child("images/pets/" + user.getUid() + "/" + pet.getPetkey());

                        pet.setPetimage(uploadTask.getPath());

                        uploadTask.putBytes(dataImage, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                firebaseFirestore.collection("pet").document(pet.getPetkey())
                                        .update("petname", petname,
                                                "petsex", petsex,
                                                "pettype", pettype,
                                                "petyear", year,
                                                "petmonth", month,
                                                "petday", day,
                                                "petimage", pet.getPetimage()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        pet.setPetname(petname);
                                        pet.setPettype(pettype);
                                        pet.setPetsex(petsex);
                                        pet.setPetyear(year);
                                        pet.setPetmonth(month);
                                        pet.setPetday(day);
                                        zLoadingDialog.dismiss();
                                        displaySuccessDialog();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "UPDATE IMAGE ERROR : " + e.getMessage());
                                        zLoadingDialog.dismiss();
                                        displayFailureDialog(e.getMessage());
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "UPLOAD IMAGE ERROR : " + e.getMessage());
                                zLoadingDialog.dismiss();
                                displayFailureDialog(e.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }
}
