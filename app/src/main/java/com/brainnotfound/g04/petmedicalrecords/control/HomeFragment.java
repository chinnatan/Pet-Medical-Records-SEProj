package com.brainnotfound.g04.petmedicalrecords.control;

import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.AddPetFragment;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.PetAdapter;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.PetFragment;
import com.brainnotfound.g04.petmedicalrecords.control.veterinary.AddRequestFragment;
import com.brainnotfound.g04.petmedicalrecords.control.veterinary.PetVeterinaryAdapter;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.brainnotfound.g04.petmedicalrecords.module.Request;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private TextView zNotfound;
    private ArrayList<Pet> zPetArrayList = new ArrayList<>();
    private ArrayList<Request> zRequestArrayList = new ArrayList<>();
    private PetAdapter zPetAdapter;
    private PetVeterinaryAdapter zPetVeterinaryAdapter;

    private User user;
    private Request requestData;
    private FirebaseFirestore firebaseFirestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        user = User.getUserInstance();
        MainActivity.onFragmentChanged(TAG);
        MainActivity.onBottomNavigationChanged(user.getType());

        if (user.getType().equals("เจ้าของสัตว์เลี้ยง")) {
            zPetAdapter = new PetAdapter(getActivity(), R.layout.fragment_pet_item, zPetArrayList);
        } else {
            zPetVeterinaryAdapter = new PetVeterinaryAdapter(getActivity(), R.layout.fragment_pet_veterinary_item, zPetArrayList, zRequestArrayList);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();

        homeFragmentElements();
        createMenu();
        initAddBtn();
        loadPet();
        initSelectedItem();
    }

    private void loadPet() {
        if (user.getType().equals("เจ้าของสัตว์เลี้ยง")) {
            firebaseFirestore.collection("pet").whereEqualTo("petownerUid", user.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    zPetAdapter.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {

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
                        zLoading.setVisibility(View.GONE);
                        zNotfound.setVisibility(View.VISIBLE);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "LOAD DOCUMENT ERROR : " + e.getMessage());
                }
            });
        } else if (user.getType().equals("สัตวแพทย์")) {

            firebaseFirestore.collection("request").whereEqualTo("veterinaryuid", user.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            zPetVeterinaryAdapter.clear();
                            if(!queryDocumentSnapshots.isEmpty()) {

                                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                    requestData = documentSnapshot.toObject(Request.class);
                                    zRequestArrayList.add(requestData);

                                    firebaseFirestore.collection("pet").whereEqualTo("petkey", requestData.getPetkey())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    if(!queryDocumentSnapshots.isEmpty()) {
                                                        for(DocumentSnapshot petDoc : queryDocumentSnapshots.getDocuments()) {
                                                            Pet petData = petDoc.toObject(Pet.class);
                                                            zPetArrayList.add(petData);
                                                        }

                                                        zPetVeterinaryAdapter.notifyDataSetChanged();
                                                        zListPet.setAdapter(zPetVeterinaryAdapter);
                                                        zLoading.setVisibility(View.GONE);
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Load pet failed : " + e.getMessage());
                                        }
                                    });
                                }

                            } else {
                                Log.d(TAG, "document is empty");
                                zLoading.setVisibility(View.GONE);
                                zNotfound.setText("คุณยังไม่ได้ส่งคำขอ");
                                zNotfound.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "Load request failed : " + e.getMessage());
                }
            });
        }
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
        zNotfound = getView().findViewById(R.id.home_notfound);
    }

    private void initAddBtn() {
        zAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getType().equals("เจ้าของสัตว์เลี้ยง")) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new AddPetFragment()).addToBackStack(null).commit();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new AddRequestFragment()).addToBackStack(null).commit();
                }
            }
        });
    }

    private void initSelectedItem() {
        zListPet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pet petData = (Pet) parent.getAdapter().getItem(position);

                if(!zRequestArrayList.get(position).getStatus().equals("รออนุมัติ")) {
                    Pet pet = Pet.getPetInstance();
                    pet.setPetimage(petData.getPetimage());
                    pet.setPetname(petData.getPetname());
                    pet.setPettype(petData.getPettype());
                    pet.setPetsex(petData.getPetsex());
                    pet.setPetownerUid(petData.getPetownerUid());
                    pet.setPetkey(petData.getPetkey());
                    pet.setPetday(petData.getPetday());
                    pet.setPetmonth(petData.getPetmonth());
                    pet.setPetyear(petData.getPetyear());
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new PetFragment()).addToBackStack(null).commit();
                } else {
                    Toast.makeText(getActivity(), "กรุณารอการอนุมัติจากเจ้าของสัตว์เลี้ยง", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
