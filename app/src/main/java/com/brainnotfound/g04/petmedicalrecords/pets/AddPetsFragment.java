package com.brainnotfound.g04.petmedicalrecords.pets;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.R;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddPetsFragment extends Fragment {

    private ArrayList<String> typeData = new ArrayList<String>();
    private ArrayList<String> sexData = new ArrayList<String>();
    private ArrayList<String> ageDayData = new ArrayList<String>();
    private ArrayList<String> ageMonthData = new ArrayList<String>();
    private ArrayList<String> ageYearData = new ArrayList<String>();
    private ImageView imageView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private Profile profile;
    private Uri uri;
    private SaveFragment saveFragment;
    private Pets intoDataPetStore = Pets.getGetPetsInstance();
    private ProgressDialog csprogress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_pets_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        saveFragment = SaveFragment.getSaveFragmentInstance();
        saveFragment.setName("AddPetsFragment");
        mAuth = FirebaseAuth.getInstance();
        String userUid = mAuth.getCurrentUser().getUid();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        profile = Profile.getProfileInstance();
        csprogress = new ProgressDialog(getActivity());

        imageController();
        setDataInSpinner();
        initAddPet(userUid);
        initBackBtn();
    }

    private void imageController() {
        imageView = getView().findViewById(R.id.frg_menu_pets_add_images);
        imageView.setOnClickListener(new View.OnClickListener() {
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
                Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(imageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void createData() {
        typeData.add("สุนัข");
        typeData.add("แมว");

        sexData.add("ผู้");
        sexData.add("เมีย");

        for(int day = 0;day <= 31; day++) {
            ageDayData.add(String.valueOf(day));
        }

        for(int month = 0;month <= 12; month++) {
            ageMonthData.add(String.valueOf(month));
        }

        for(int year = 0;year <= 15; year++) {
            ageYearData.add(String.valueOf(year));
        }
    }

    private void setDataInSpinner() {
        Spinner spinnerType = getView().findViewById(R.id.frg_menu_pets_add_type);
        Spinner spinnerSex = getView().findViewById(R.id.frg_menu_pets_add_sex);
        Spinner spinnerAgeDay = getView().findViewById(R.id.frg_menu_pets_add_age_day);
        Spinner spinnerAgeMonth = getView().findViewById(R.id.frg_menu_pets_add_age_month);
        Spinner spinnerAgeYear = getView().findViewById(R.id.frg_menu_pets_add_age_year);

        createData();

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, typeData);
        spinnerType.setAdapter(adapterType);

        ArrayAdapter<String> adapterSex = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, sexData);
        spinnerSex.setAdapter(adapterSex);

        ArrayAdapter<String> adapterAgeDay = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ageDayData);
        spinnerAgeDay.setAdapter(adapterAgeDay);

        ArrayAdapter<String> adapterAgeMonth = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ageMonthData);
        spinnerAgeMonth.setAdapter(adapterAgeMonth);

        ArrayAdapter<String> adapterAgeYear = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ageYearData);
        spinnerAgeYear.setAdapter(adapterAgeYear);
    }

    private void initBackBtn() {
        ImageView _backBtn = getView().findViewById(R.id.frg_menu_pets_add_backBtn);
        _backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new PetsFragment()).commit();
            }
        });
    }

    private void initAddPet(final String userUid) {
        Button addPetBtn = getView().findViewById(R.id.frg_menu_pets_add_addBtn);
        addPetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csprogress.setMessage("Adding...");
                getPetsData(userUid);
            }
        });
    }

    private void getPetsData(String userUid) {
        final String _userUid = userUid;
        Spinner typeTxt = getView().findViewById(R.id.frg_menu_pets_add_type);
        Spinner sexTxt = getView().findViewById(R.id.frg_menu_pets_add_sex);
        Spinner dayTxt = getView().findViewById(R.id.frg_menu_pets_add_age_day);
        Spinner monthTxt = getView().findViewById(R.id.frg_menu_pets_add_age_month);
        Spinner yearTxt = getView().findViewById(R.id.frg_menu_pets_add_age_year);
        EditText petnameTxt = getView().findViewById(R.id.frg_menu_pets_add_petname);

        String typeStr = typeTxt.getSelectedItem().toString();
        String sexStr = sexTxt.getSelectedItem().toString();
        String dayStr = dayTxt.getSelectedItem().toString();
        String monthStr = monthTxt.getSelectedItem().toString();
        String yearStr = yearTxt.getSelectedItem().toString();
        final String petnameStr = petnameTxt.getText().toString();

        if(typeStr.isEmpty() || sexStr.isEmpty() || dayStr.isEmpty() || monthStr.isEmpty() || yearStr.isEmpty() || petnameStr.isEmpty()) {
            Toast.makeText(getActivity(), "กรุณาใส่ข้อมูลให้ครบถ้วน", Toast.LENGTH_LONG).show();
        } else if(imageView.getDrawable() == null) {
            Toast.makeText(getActivity(), "กรุณาเลือกรูปภาพของสัตว์เลี้ยง", Toast.LENGTH_SHORT).show();
        } else {
            csprogress.show();
            DateFormat dateFormat = new SimpleDateFormat("ddMMYYYYHHmmss");
            Date date = new Date();

            storageReference = mStorage.getReference().child("images/pets/" + _userUid + "/" + petnameStr);

            intoDataPetStore.setPet_name(petnameStr);
            intoDataPetStore.setPet_type(typeStr);
            intoDataPetStore.setPet_sex(sexStr);
            intoDataPetStore.setPet_ageDay(dayStr);
            intoDataPetStore.setPet_ageMonth(monthStr);
            intoDataPetStore.setPet_ageYear(yearStr);
            intoDataPetStore.setUrlImage(storageReference.getPath());
            intoDataPetStore.setKey(dateFormat.format(date));


            mStore.collection("pets").document(_userUid)
                    .collection("detail").document(dateFormat.format(date))
                    .set(intoDataPetStore).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getActivity(), "เพิ่มข้อมูลสัตว์เลี้ยงเรียบร้อย", Toast.LENGTH_LONG).show();
                            csprogress.dismiss();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                    .replace(R.id.main_view, new PetsFragment()).commit();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
