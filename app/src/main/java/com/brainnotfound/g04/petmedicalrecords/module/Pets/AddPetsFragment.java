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
import android.widget.ImageView;
import android.widget.Spinner;

import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.ImageConverter;

import java.util.ArrayList;

public class AddPetsFragment extends Fragment {

    private ArrayList<String> typeData = new ArrayList<String>();
    private ArrayList<String> sexData = new ArrayList<String>();
    private ArrayList<String> ageData = new ArrayList<String>();
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_pets_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageController();
        setDataInSpinner();
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
                Uri uri = data.getData();
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
                Bitmap circleBitmap = ImageConverter.getRoundedConnerBitmap(bitmap, 150);
                imageView.setImageBitmap(circleBitmap);
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

        ageData.add("1 เดือน");
        ageData.add("2 เดือน");
        ageData.add("3 เดือน");
        ageData.add("4 เดือน");
        ageData.add("5 เดือน");
        ageData.add("6 เดือน");
        ageData.add("7 เดือน");
        ageData.add("8 เดือน");
        ageData.add("9 เดือน");
        ageData.add("10 เดือน");
        ageData.add("11 เดือน");
        ageData.add("1 ปี");
        ageData.add("2 ปี");
        ageData.add("3 ปี");
        ageData.add("4 ปี");
        ageData.add("5 ปี");
        ageData.add("6 ปี");
        ageData.add("7 ปี");
        ageData.add("8 ปี");
        ageData.add("9 ปี");
        ageData.add("10 ปี");
        ageData.add("11 ปี");
        ageData.add("12 ปี");
        ageData.add("13 ปี");
        ageData.add("14 ปี");
        ageData.add("15 ปี");
    }

    private void setDataInSpinner() {
        Spinner spinnerType = getView().findViewById(R.id.frg_menu_pets_add_type);
        Spinner spinnerSex = getView().findViewById(R.id.frg_menu_pets_add_sex);
        Spinner spinnerAge = getView().findViewById(R.id.frg_menu_pets_add_age);

        createData();

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, typeData);
        spinnerType.setAdapter(adapterType);

        ArrayAdapter<String> adapterSex = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, sexData);
        spinnerSex.setAdapter(adapterSex);

        ArrayAdapter<String> adapterAge = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, ageData);
        spinnerAge.setAdapter(adapterAge);
    }

    void initBackBtn() {
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
}
