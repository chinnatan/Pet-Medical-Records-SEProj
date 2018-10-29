package com.brainnotfound.g04.petmedicalrecords.pets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.MenuFragment;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PetsFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userUid;

    SaveFragment _saveFragment = SaveFragment.getSaveFragmentInstance();
    TextView petsNotFound;
    ProgressBar loadingPets;
    ArrayList<Pets> pets = new ArrayList<>();
    ListView petsList;
    PetsAdapter petsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_pets, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        _saveFragment.setName("PetsFragment");

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userUid = mAuth.getCurrentUser().getUid();
        loadingPets = getView().findViewById(R.id.frg_menu_pets_loading);
        petsNotFound = getView().findViewById(R.id.frg_menu_pets_notfound);

        petsList = getView().findViewById(R.id.frg_menu_pets_list);
        petsAdapter = new PetsAdapter(getActivity(), R.layout.fragment_menu_pets_item, pets, getActivity());

        initAddPet();
        loadPets(userUid);
        initBackBtn();
    }

    private void loadPets(String userUid) {
        mStore.collection("account").document(userUid)
                .collection("pets").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                petsAdapter.clear();
                loadingPets.setVisibility(View.GONE);

                if(queryDocumentSnapshots.isEmpty()) {
                    petsNotFound.setVisibility(View.VISIBLE);
                } else {
                    List<DocumentSnapshot> listPetsData = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot _doc : listPetsData) {
                        Pets _petData = _doc.toObject(Pets.class);
                        pets.add(_petData);
                    }
                }

                petsAdapter.notifyDataSetChanged();
                petsList.setAdapter(petsAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initAddPet() {
        LinearLayout addPets = getView().findViewById(R.id.frg_menu_pets_addBtn);
        addPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _saveFragment.setName("AddPetsFragment");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.main_view, new AddPetsFragment()).commit();
            }
        });
    }

    private void initBackBtn() {
        ImageView _backBtn = getView().findViewById(R.id.frg_menu_pets_backBtn);
        _backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new MenuFragment()).commit();
            }
        });
    }
}
