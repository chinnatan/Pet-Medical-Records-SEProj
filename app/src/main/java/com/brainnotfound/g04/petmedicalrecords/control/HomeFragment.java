package com.brainnotfound.g04.petmedicalrecords.control;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.AddPetFragment;

import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOME";

    private final String title = "หน้าหลัก";

    private Toolbar zToolBar;
    private ImageButton zAddBtn;
    private ListView zListPet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);
        homeFragmentElements();
        createMenu();
        initAddBtn();
    }

    private void createMenu() {
        Log.d(TAG, "create menu");
        Objects.requireNonNull(getActivity()).setActionBar(zToolBar);
        zToolBar.setTitle(title);
    }

    private void homeFragmentElements() {
        zToolBar = getView().findViewById(R.id.toolbar);
        zAddBtn = getView().findViewById(R.id.frg_home_addbtn);
        zListPet = getView().findViewById(R.id.home_list);
    }

    private void initAddBtn() {
        zAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new AddPetFragment()).addToBackStack(null).commit();
            }
        });
    }
}
