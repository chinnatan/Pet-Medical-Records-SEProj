package com.brainnotfound.g04.petmedicalrecords.control;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.AddPetFragment;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.PetAdapter;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.PetFragment;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private static final String TAG = "HOME";
    private final String title = "หน้าหลัก";

    private Toolbar zToolBar;
    private ImageButton zAddBtn;
    private ListView zListPet;
    private ProgressBar zLoading;
    private ArrayList<Pet> zPetArrayList = new ArrayList<>();
    private PetAdapter zPetAdapter;

    private User user;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);

        zPetAdapter = new PetAdapter(getActivity(), R.layout.fragment_pet_item, zPetArrayList);
        user = User.getUserInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        homeFragmentElements();
        createMenu();
        initAddBtn();
        loadPet();
        initSelectedItem();
    }

    private void loadPet() {
        firebaseFirestore.collection("pet").whereEqualTo("petownerUid", user.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                zPetAdapter.clear();
                if(!queryDocumentSnapshots.isEmpty()) {

                    List<DocumentSnapshot> listPetData = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot document : listPetData) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Pet petData = document.toObject(Pet.class);
                        zPetArrayList.add(petData);
                    }

                    zPetAdapter.notifyDataSetChanged();
                    zListPet.setAdapter(zPetAdapter);
                    zLoading.setVisibility(View.GONE);
                } else {
                    Log.d(TAG, "document is empty");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "LOAD DOCUMENT ERROR : " + e.getMessage());
            }
        });
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
        zLoading = getView().findViewById(R.id.home_loading);
    }

    private void initAddBtn() {
        zAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new AddPetFragment()).addToBackStack(null).commit();
            }
        });
    }

    private void initSelectedItem() {
        zListPet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pet petAdapter = (Pet) parent.getAdapter().getItem(position);
                Pet pet = Pet.getPetInstance();
                pet.setPetimage(petAdapter.getPetimage());
                pet.setPetname(petAdapter.getPetname());
                pet.setPettype(petAdapter.getPettype());
                pet.setPetsex(petAdapter.getPetsex());
                pet.setPetownerUid(petAdapter.getPetownerUid());
                pet.setPetkey(petAdapter.getPetkey());
                pet.setPetday(petAdapter.getPetday());
                pet.setPetmonth(petAdapter.getPetmonth());
                pet.setPetyear(petAdapter.getPetyear());

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new PetFragment()).addToBackStack(null).commit();
            }
        });
    }
}
