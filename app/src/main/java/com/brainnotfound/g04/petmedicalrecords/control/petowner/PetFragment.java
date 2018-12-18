package com.brainnotfound.g04.petmedicalrecords.control.petowner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.HistoryFragment;
import com.brainnotfound.g04.petmedicalrecords.control.HomeFragment;
import com.brainnotfound.g04.petmedicalrecords.control.LoginFragment;
import com.brainnotfound.g04.petmedicalrecords.control.MyEditFragment;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.adapter.HistoryAdapter;
import com.brainnotfound.g04.petmedicalrecords.control.veterinary.AddHistoryFragment;
import com.brainnotfound.g04.petmedicalrecords.control.veterinary.EditHistoryFragment;
import com.brainnotfound.g04.petmedicalrecords.module.History;
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
    private ImageButton zHistoryAdd;
    private LinearLayout zHistoryAddBg;

    private ArrayList<History> zHistoryArrayList = new ArrayList<>();
    private HistoryAdapter zHistoryAdapter;

    private User user;
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

        user = User.getUserInstance();
        pet = Pet.getPetInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        createMenu();
        loadPet();

        zHistoryAdapter = new HistoryAdapter(getActivity(), R.layout.fragment_history_item, zHistoryArrayList);
        addHistoryPet();
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
        zHistoryAdd = getView().findViewById(R.id.pet_history_add);
        zHistoryAddBg = getView().findViewById(R.id.pet_history_add_bg);
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

        if(user.getType().equals("เจ้าของสัตว์เลี้ยง")) {
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
                            displayConfirmDeleteDialog();
                            return true;
                        default:
                            return true;
                    }
                }
            });
        }

        if (!user.getType().equals("สัตวแพทย์")) {
            zHistoryAdd.setVisibility(View.INVISIBLE);
            zHistoryAddBg.setVisibility(View.INVISIBLE);
        }
    }

    private void deletePet() {
        firebaseFirestore.collection("pet").document(pet.getPetkey())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                zLoadingDialog.dismiss();
                firebaseFirestore.collection("request").get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(!queryDocumentSnapshots.isEmpty()) {
                                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        if(documentSnapshot.getString("petkey").equals(pet.getPetkey())) {
                                            firebaseFirestore.collection("request").document(documentSnapshot.getString("requestkey"))
                                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getActivity(), "ลบข้อมูลเรียบร้อย", Toast.LENGTH_LONG).show();
                                                    getActivity().getSupportFragmentManager().popBackStack();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "DELETE ERROR : " + e.getMessage());
                                                    zLoadingDialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "DELETE ERROR : " + e.getMessage());
                        zLoadingDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "DELETE ERROR : " + e.getMessage());
                zLoadingDialog.dismiss();
            }
        });
    }

    private void displayConfirmDeleteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("ยืนยันการลบสัตว์เลี้ยง")
                .setMessage("คุณแน่ใจหรือไม่ที่จะลบสัตว์เลี้ยงของคุณ")
                .setPositiveButton("ยืนยีน", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        zLoadingDialog.setMessage("กำลังลบข้อมูล...");
                        zLoadingDialog.setCancelable(false);
                        zLoadingDialog.setCanceledOnTouchOutside(false);
                        zLoadingDialog.show();
                        deletePet();
                    }
                })
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
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
        Log.d(TAG, "PET ID : " + pet.getPetkey());
        firebaseFirestore.collection("pet").document(pet.getPetkey())
                .collection("history")
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            zHistoryLoading.setVisibility(View.GONE);
                            zHistoryNotfound.setVisibility(View.VISIBLE);
                        } else {
                            zHistoryAdapter.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                                History historyData = documentSnapshot.toObject(History.class);
                                zHistoryArrayList.add(historyData);
                            }

                            zHistoryAdapter.notifyDataSetChanged();
                            zHistoryList.setAdapter(zHistoryAdapter);
                            zHistoryLoading.setVisibility(View.GONE);
                        }
                    }
                });

        zHistoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                History historyData = (History) parent.getAdapter().getItem(position);

                Fragment historyFragment = new HistoryFragment();
                Bundle historyBundle = new Bundle();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);

                historyBundle.putString("petid", historyData.getPetid());
                historyBundle.putString("historyid", historyData.getHistoryid());
                historyBundle.putString("historytitle", historyData.getTitle());
                historyBundle.putString("historydetail", historyData.getDetail());
                historyBundle.putStringArrayList("historyvaccine", historyData.getVaccine());
                historyBundle.putString("historydate", historyData.getDate());
                historyBundle.putString("historydatetime", historyData.getDatetime());
                historyBundle.putString("historyaddby", historyData.getAddby());

                historyFragment.setArguments(historyBundle);
                fragmentTransaction.replace(R.id.main_view, historyFragment);
                fragmentTransaction.commit();
            }
        });
    }

    private void addHistoryPet() {
        zHistoryAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new AddHistoryFragment()).addToBackStack(null).commit();
            }
        });
    }
}
