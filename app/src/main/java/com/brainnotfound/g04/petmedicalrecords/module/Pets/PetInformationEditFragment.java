package com.brainnotfound.g04.petmedicalrecords.module.Pets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.ImageConverter;
import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class PetInformationEditFragment extends Fragment {

    private SaveFragment saveFragment;
    private Pets pets;
    private ArrayList<String> typeData = new ArrayList<String>();
    private ArrayList<String> sexData = new ArrayList<String>();
    private ArrayList<String> ageDayData = new ArrayList<String>();
    private ArrayList<String> ageMonthData = new ArrayList<String>();
    private ArrayList<String> ageYearData = new ArrayList<String>();
    private Uri uriImages;
    private ImageView _imagePet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pet_information_edit, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        saveFragment = SaveFragment.getSaveFragmentInstance();
        saveFragment.setName("PetInformationEditFragment");
        pets = Pets.getGetPetsInstance();

        initBtn();
        getInformation(pets);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK) {
            try {
                uriImages = data.getData();
                Glide.with(getActivity()).load(uriImages).apply(RequestOptions.circleCropTransform()).into(_imagePet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void imageController(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "กรุณาเลือกรูปภาพสัตว์เลี้ยงของคุณ"), 1);
            }
        });
    }

    private void getInformation(Pets pets) {
        _imagePet = getView().findViewById(R.id.frg_pet_inf_edit_image);
        Spinner _typeTxt = getView().findViewById(R.id.frg_pet_inf_edit_type);
        EditText _petnameTxt = getView().findViewById(R.id.frg_pet_inf_edit_petname);
        Spinner _sexTxt = getView().findViewById(R.id.frg_pet_inf_edit_sex);
        Spinner _dayAgeTxt = getView().findViewById(R.id.frg_pet_inf_edit_day);
        Spinner _monthAgeTxt = getView().findViewById(R.id.frg_pet_inf_edit_month);
        Spinner _yearAgeTxt = getView().findViewById(R.id.frg_pet_inf_edit_year);

        imageController(_imagePet);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child(pets.getUrlImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(_imagePet);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        getData(pets);
        setDataSpinner(_typeTxt, _sexTxt, _dayAgeTxt, _monthAgeTxt, _yearAgeTxt);
        _petnameTxt.setText(pets.getPet_name());

    }

    private void getData(Pets pets) {
        if(pets.getPet_type().equals("สุนัข")) {
            typeData.add(pets.getPet_type());
            typeData.add("แมว");
        } else {
            typeData.add(pets.getPet_type());
            typeData.add("สุนัข");
        }

        if(pets.getPet_sex().equals("ผู้")) {
            sexData.add(pets.getPet_sex());
            sexData.add("เมีย");
        } else {
            sexData.add(pets.getPet_sex());
            sexData.add("ผู้");
        }

        ageDayData.add(pets.getPet_ageDay());
        for(int day = 1;day <= 31;day++) {
            if(day != Integer.parseInt(pets.getPet_ageDay())) {
                ageDayData.add(String.valueOf(day));
            }
        }

        ageMonthData.add(pets.getPet_ageMonth());
        for(int month = 1;month <= 12;month++) {
            if(month != Integer.parseInt(pets.getPet_ageMonth())) {
                ageMonthData.add(String.valueOf(month));
            }
        }

        ageYearData.add(pets.getPet_ageYear());
        for(int year = 1;year <= 15;year++) {
            if(year != Integer.parseInt(pets.getPet_ageYear())) {
                ageYearData.add(String.valueOf(year));
            }
        }
    }

    private void setDataSpinner(Spinner type, Spinner sex, Spinner dayAge, Spinner monthAge, Spinner yearAge) {
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, typeData);
        type.setAdapter(adapterType);

        ArrayAdapter<String> adapterSex = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, sexData);
        sex.setAdapter(adapterSex);

        ArrayAdapter<String> adapterAgeDay = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ageDayData);
        dayAge.setAdapter(adapterAgeDay);

        ArrayAdapter<String> adapterAgeMonth = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ageMonthData);
        monthAge.setAdapter(adapterAgeMonth);

        ArrayAdapter<String> adapterAgeYear = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ageYearData);
        yearAge.setAdapter(adapterAgeYear);
    }

    private void initBtn() {
        initBackBtn();
    }

    private void initBackBtn() {
        ImageView _backBtn = getView().findViewById(R.id.frg_pet_inf_edit_backBtn);
        _backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new PetInformationFragment()).commit();
            }
        });
    }

}
