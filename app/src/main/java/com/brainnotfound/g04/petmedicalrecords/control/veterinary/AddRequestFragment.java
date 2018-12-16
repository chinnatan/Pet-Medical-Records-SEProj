package com.brainnotfound.g04.petmedicalrecords.control.veterinary;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.veterinary.adapter.PetRequestAdapter;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddRequestFragment extends Fragment {

    private static final String TAG = "ADDREQUEST";

    private SearchView zSearchView;
    private ListView zListSearch;
    private ProgressBar zLoading;

    private ArrayList<Pet> zPetArrayList = new ArrayList<>();
    private PetRequestAdapter zPetRequestAdapter;

    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addrequest, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);

        firebaseFirestore = FirebaseFirestore.getInstance();
        zPetRequestAdapter = new PetRequestAdapter(getActivity(), R.layout.fragment_addrequest_item, zPetArrayList, getActivity());

        addrequestFragmentElements();
        searchbar();
    }

    private void addrequestFragmentElements() {
        zSearchView = getView().findViewById(R.id.frg_addrequest_search);
        zListSearch = getView().findViewById(R.id.frg_addrequest_list);
        zLoading = getView().findViewById(R.id.addrequest_loading);
    }

    private void searchPet(final String petkey) {
        firebaseFirestore.collection("pet").whereEqualTo("petkey", petkey).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        zPetRequestAdapter.clear();

                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Pet petData = document.toObject(Pet.class);
                                if (petData.getPetkey().equals(petkey)) {
                                    zPetArrayList.add(petData);
                                }
                            }

                            zPetRequestAdapter.notifyDataSetChanged();
                            zListSearch.setAdapter(zPetRequestAdapter);

                            zLoading.setVisibility(View.GONE);
                        } else if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(getActivity(), "ไม่พบสัตว์เลี้ยงที่คุณค้นหา", Toast.LENGTH_SHORT).show();
                            zLoading.setVisibility(View.INVISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "LOAD DOCUMENT ERROR : " + e.getMessage());
                    }
                });
    }

    private void searchbar() {
        zSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() == 14) {
                    zLoading.setVisibility(View.VISIBLE);
                    searchPet(newText);
                } else {
                    zLoading.setVisibility(View.INVISIBLE);
                    zPetRequestAdapter.clear();
                }
                return true;
            }
        });
    }
}
