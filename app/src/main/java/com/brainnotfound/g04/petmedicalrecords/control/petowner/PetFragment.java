package com.brainnotfound.g04.petmedicalrecords.control.petowner;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.HomeFragment;
import com.brainnotfound.g04.petmedicalrecords.control.LoginFragment;
import com.brainnotfound.g04.petmedicalrecords.control.MyEditFragment;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.adapter.HistoryAdapter;
import com.brainnotfound.g04.petmedicalrecords.module.History;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class PetFragment extends Fragment {

    private static final String TAG = "PET";

    private Toolbar zToolBar;
    private ProgressBar zLoading;
    private ImageView zImageViewPet;
    private TextView zPetname;
    private TextView zPettype;
    private TextView zPetsex;
    private TextView zPetage;
    private TextView zPetid;
    private ProgressDialog zLoadingDialog;

    // History Pet
    private TextView zHistoryNotfound;
    private ProgressBar zHistoryLoading;
    private ListView zHistoryList;

    private ArrayList<History> zHistoryArrayList = new ArrayList<>();
    private HistoryAdapter zHistoryAdapter;

    private Pet pet;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_pet, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);
        petFragmentElements();

        zLoadingDialog = new ProgressDialog(getActivity());

        pet = Pet.getPetInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        createMenu();
        loadPet();

        zHistoryAdapter = new HistoryAdapter(getActivity(), R.layout.fragment_history_item, zHistoryArrayList);
    }

    private void petFragmentElements() {
        zToolBar = getView().findViewById(R.id.toolbar);
        zLoading = getView().findViewById(R.id.pet_loading);
        zImageViewPet = getView().findViewById(R.id.frg_pet_image);
        zPetname = getView().findViewById(R.id.frg_pet_name);
        zPettype = getView().findViewById(R.id.frg_pet_type);
        zPetsex = getView().findViewById(R.id.frg_pet_sex);
        zPetage = getView().findViewById(R.id.frg_pet_age);
        zPetid = getView().findViewById(R.id.frg_pet_id);

        zHistoryNotfound = getView().findViewById(R.id.pet_history_notfound);
        zHistoryLoading = getView().findViewById(R.id.pet_history_loading);
        zHistoryList = getView().findViewById(R.id.pet_history_list);
    }

    private void createMenu() {
        Log.d(TAG, "create menu");
        Objects.requireNonNull(getActivity()).setActionBar(zToolBar);
        zToolBar.setTitle(pet.getPetname());
        zToolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        zToolBar.setNavigationContentDescription("Back");
        zToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        zToolBar.setOverflowIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_more_event));
        zToolBar.inflateMenu(R.menu.navigation_pet);
        zToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_edit:
                        Log.d(TAG, "menu item click : edit");
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new EditPetFragment()).addToBackStack(null).commit();
                        return true;
                    case R.id.navigation_delete:
                        Log.d(TAG, "menu item click : delete");
                        zLoadingDialog.setMessage("กำลังลบข้อมูล...");
                        zLoadingDialog.setCancelable(false);
                        zLoadingDialog.setCanceledOnTouchOutside(false);
                        zLoadingDialog.show();
                        deletePet();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void deletePet() {
        firebaseFirestore.collection("pet").document(pet.getPetkey())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                zLoadingDialog.dismiss();
                Toast.makeText(getActivity(), "ลบข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new HomeFragment()).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "DELETE ERROR : " + e.getMessage());
                zLoadingDialog.dismiss();
            }
        });
    }

    private void loadPet() {
        storageReference.child(pet.getPetimage()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    if (isAdded()) {
                        Glide.with(getActivity()).load(task.getResult()).apply(RequestOptions.circleCropTransform()).into(zImageViewPet);
                        zPetname.setText(pet.getPetname());
                        zPetid.setText("PET ID : " + pet.getPetkey());
                        zPettype.setText(pet.getPettype());
                        zPetsex.setText("เพศ : " + pet.getPetsex());
                        zPetage.setText("อายุ : " + pet.getPetyear() + " ปี " + pet.getPetmonth() + " เดือน " + pet.getPetday() + " วัน");
                        zImageViewPet.setVisibility(View.VISIBLE);
                        zPetname.setVisibility(View.VISIBLE);
                        zPetid.setVisibility(View.VISIBLE);
                        zPettype.setVisibility(View.VISIBLE);
                        zPetsex.setVisibility(View.VISIBLE);
                        zPetage.setVisibility(View.VISIBLE);
                        zLoading.setVisibility(View.GONE);
                        loadHistoryPet();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "LOAD IMAGE : " + e.getMessage());
            }
        });
    }

    private void loadHistoryPet() {
        firebaseFirestore.collection("pet").document(pet.getPetkey())
                .collection("history")
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("datetime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                       Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                                       History historyData = documentSnapshot.toObject(History.class);
                                                       zHistoryArrayList.add(historyData);
                                                   }

                                                   zHistoryAdapter.notifyDataSetChanged();
                                                   zHistoryList.setAdapter(zHistoryAdapter);
                                                   zHistoryLoading.setVisibility(View.GONE);
                                               } else {
                                                   zHistoryLoading.setVisibility(View.GONE);
                                                   zHistoryNotfound.setVisibility(View.VISIBLE);
                                               }
                                           }
                                       }
                );
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        zHistoryAdapter.clear();
//                        if(!queryDocumentSnapshots.isEmpty()) {
//                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
//                                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
//                                History historyData = documentSnapshot.toObject(History.class);
//                                zHistoryArrayList.add(historyData);
//                            }
//
//                            zHistoryAdapter.notifyDataSetChanged();
//                            zHistoryList.setAdapter(zHistoryAdapter);
//                            zHistoryLoading.setVisibility(View.GONE);
//                        } else {
//                            zHistoryLoading.setVisibility(View.GONE);
//                            zHistoryNotfound.setVisibility(View.VISIBLE);
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "Load history is failed : " + e.getMessage());
//            }
//        });
    }
}
