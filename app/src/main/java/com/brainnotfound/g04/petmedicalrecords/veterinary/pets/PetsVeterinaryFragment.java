package com.brainnotfound.g04.petmedicalrecords.veterinary.pets;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.brainnotfound.g04.petmedicalrecords.module.Request;
import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;
import com.brainnotfound.g04.petmedicalrecords.pets.Pets;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PetsVeterinaryFragment extends Fragment {

    private SaveFragment _saveFragment = SaveFragment.getSaveFragmentInstance();
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private String userUid;
    private ProgressBar loadingVeterinaryPets;
    private TextView veterinaryPetsNotFound;
    private ArrayList<Pets> pets = new ArrayList<>();
    private ListView veterinaryPetsList;
    private PetsVeterinaryAdapter petsVeterinaryAdapter;
    private Request request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_veterinary_pets, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _saveFragment.setName("PetsFragment");
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        userUid = mAuth.getCurrentUser().getUid();
        request = Request.getRequestInstance();

        loadingVeterinaryPets = getView().findViewById(R.id.frg_menu_veterinary_pets_loading);
        veterinaryPetsNotFound = getView().findViewById(R.id.frg_menu_veterinary_pets_notfound);

        veterinaryPetsList = getView().findViewById(R.id.frg_menu_veterinary_pets_list);
        petsVeterinaryAdapter = new PetsVeterinaryAdapter(getActivity(), R.layout.fragment_menu_pets_item, pets, getActivity());

        initAllBtn();
        loadPets();
    }

    private void loadPets() {
        mStore.collection("pets").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        mStore.collection("pets").document(queryDocumentSnapshot.getId())
                                .collection("request").document(userUid)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                request.setStatus(documentSnapshot.getString("status"));
                                request.setKey(documentSnapshot.getString("key"));

                                if(documentSnapshot.exists()) {

                                    mStore.collection("pets").document(queryDocumentSnapshot.getId())
                                            .collection("detail")
                                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            petsVeterinaryAdapter.clear();
                                            loadingVeterinaryPets.setVisibility(View.GONE);

                                            if (queryDocumentSnapshots.isEmpty()) {
                                                veterinaryPetsNotFound.setVisibility(View.VISIBLE);
                                            } else {
                                                List<DocumentSnapshot> listPetsData = queryDocumentSnapshots.getDocuments();

                                                for (DocumentSnapshot _doc : listPetsData) {
                                                    Pets _petData = _doc.toObject(Pets.class);
                                                    if (_petData.getKey().equals(request.getKey())) {
                                                        pets.add(_petData);
                                                    }
                                                }
                                            }

                                            petsVeterinaryAdapter.notifyDataSetChanged();
                                            veterinaryPetsList.setAdapter(petsVeterinaryAdapter);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            Log.d("LOADPETS_PETSVETERINARY", e.getMessage());
                                        }
                                    });
                                } else {
                                    loadingVeterinaryPets.setVisibility(View.GONE);
                                    veterinaryPetsNotFound.setVisibility(View.VISIBLE);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.d("LOADPETS_PETSVETERINARY", e.getMessage());
                            }
                        });
                    }
                } else {
                    Log.d("LOADPETS_PETSVETERINARY", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void initAllBtn() {
        initAddBtn();
        initBackBtn();
    }

    private void initBackBtn() {
        ImageView _backBtn = getView().findViewById(R.id.frg_menu_veterinary_pets_backBtn);
        _backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new MenuFragment()).commit();
            }
        });
    }

    private void initAddBtn() {
        LinearLayout _addBtn = getView().findViewById(R.id.frg_menu_veterinary_pets_addBtn);
        _addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.main_view, new AddPetsVeterinaryFragment()).commit();
            }
        });
    }
}
