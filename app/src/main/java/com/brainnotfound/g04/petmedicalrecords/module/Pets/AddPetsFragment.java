package com.brainnotfound.g04.petmedicalrecords.module.Pets;

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

import com.brainnotfound.g04.petmedicalrecords.MenuFragment;
import com.brainnotfound.g04.petmedicalrecords.R;

import java.util.ArrayList;

public class AddPetsFragment extends Fragment {

    private ArrayList<String> typeData = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_pets_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Spinner spinnerType = getView().findViewById(R.id.frg_menu_pets_add_type);

        createTypeData();

        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, typeData);
        spinnerType.setAdapter(adapterType);

        initBackBtn();
    }

    private void createTypeData() {
        typeData.add("สุนัข");
        typeData.add("แมว");
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
