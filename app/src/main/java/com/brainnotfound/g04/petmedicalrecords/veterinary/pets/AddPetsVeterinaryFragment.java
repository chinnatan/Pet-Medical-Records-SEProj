package com.brainnotfound.g04.petmedicalrecords.veterinary.pets;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.Request;
import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;
import com.brainnotfound.g04.petmedicalrecords.pets.Pets;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AddPetsVeterinaryFragment extends Fragment {

    private SaveFragment saveFragment;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private Pets pets;
    private String checkLoop = "";
    private ProgressDialog csprogress;
    private String userUidRequest = "";
    private String userUidCurrent = "";

    private LinearLayout _bgContent;
    private ImageView _imagePet;
    private TextView _petNameTxt;
    private TextView _petTypeTxt;
    private TextView _petSexTxt;
    private TextView _petAgeTxt;
    private Button _requestBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_veterinary_pets_add, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        saveFragment = SaveFragment.getSaveFragmentInstance();
        saveFragment.setName("AddPetsVeterinaryFragment");

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        csprogress = new ProgressDialog(getActivity());
        pets = Pets.getGetPetsInstance();
        userUidCurrent = mAuth.getCurrentUser().getUid();

        initAllBtn();
        getViewID();
        invisibleInformation();
    }

    private void initAllBtn() {
        initSearchBtn();
        initRequestBtn();
        initBackBtn();
    }

    private void initBackBtn() {
        ImageView _backBtn = getView().findViewById(R.id.frg_menu_veterinary_pets_add_backBtn);
        _backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new PetsVeterinaryFragment()).commit();
            }
        });
    }

    private void initSearchBtn() {
        Button _searchBtn = getView().findViewById(R.id.frg_menu_veterinary_pets_add_searchBtn);
        _searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csprogress.setMessage("Searching...");
                csprogress.show();
                searchInformationPet();
            }
        });
    }

    private void initRequestBtn() {
        Button _requestBtn = getView().findViewById(R.id.frg_menu_veterinary_pets_add_requestBtn);
        _requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csprogress.setMessage("Requesting...");
                csprogress.show();
                sendRequest();
            }
        });
    }

    private void sendRequest() {
        Request request = Request.getRequestInstance();
        request.setUid(userUidCurrent);
        request.setStatus("รอการอนุมัติ");
        request.setKey(pets.getKey());
        mStore.collection("pets").document(userUidRequest)
                .collection("request").document(userUidCurrent)
                .set(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                csprogress.dismiss();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new PetsVeterinaryFragment()).commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("REQUEST", e.getMessage());
            }
        });
    }

    private void searchInformationPet() {
        EditText _petCodeTxt = getView().findViewById(R.id.frg_menu_veterinary_pets_add_petcode);

        final String _petCodeStr = _petCodeTxt.getText().toString();

        if (_petCodeStr.isEmpty()) {
            Toast.makeText(getActivity(), "กรุณาใส่รหัสประจำตัวสัตว์เลี้ยง", Toast.LENGTH_LONG).show();
        } else {
            mStore.collection("pets")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d("CHECKLOOP", "On Complete : " + String.valueOf(checkLoop));
                    if (task.isSuccessful()) {
                        checkLoop = "";
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("CHECKLOOP", String.valueOf(checkLoop));
                            if (checkLoop.equals("found")) {
                                break;
                            }
                            Log.d("PULL", document.getId() + " => " + document.getData());
                            mStore.collection("pets").document(document.getId())
                                    .collection("detail").document(_petCodeStr)
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        if (documentSnapshot.getString("key").equals(_petCodeStr)) {
                                            csprogress.dismiss();
                                            checkLoop = "found";

                                            pets.setPet_name(documentSnapshot.getString("pet_name"));
                                            pets.setPet_type(documentSnapshot.getString("pet_type"));
                                            pets.setPet_sex(documentSnapshot.getString("pet_sex"));
                                            pets.setPet_ageDay(documentSnapshot.getString("pet_ageDay"));
                                            pets.setPet_ageMonth(documentSnapshot.getString("pet_ageMonth"));
                                            pets.setPet_ageYear(documentSnapshot.getString("pet_ageYear"));
                                            pets.setUrlImage(documentSnapshot.getString("urlImage"));
                                            pets.setKey(documentSnapshot.getString("key"));

                                            userUidRequest = document.getId();

                                            Log.d("CHECKLOOP", String.valueOf(checkLoop));
                                            showInformationPet();
                                        }
                                    } else {
                                        Log.d("CHECKLOOP", "CHECK ON SUCCESS ELSE : " + String.valueOf(checkLoop));
                                        checkLoop = "notfound";
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.d("PETDATA", e.getMessage());
                                }
                            });
                        }
                    } else {
                        Log.d("PULL", "Error getting documents: ", task.getException());
                    }
                    // Delay Check
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (checkLoop.equals("notfound")) {
                                csprogress.dismiss();
                                invisibleInformation();
                                Toast.makeText(getActivity(), "ไม่พบสัตว์เลี้ยงที่คุณค้นหา", Toast.LENGTH_LONG).show();
                            }
                        }
                    }, 2000);
                }
            });
        }
    }

    private void showInformationPet() {
        storageReference.child(pets.getUrlImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(_imagePet);
                _petNameTxt.setText("ชื่อ : " + pets.getPet_name());
                _petTypeTxt.setText("ประเภท : " + pets.getPet_type());
                _petSexTxt.setText("เพศ : " + pets.getPet_sex());
                _petAgeTxt.setText("อายุ : " + pets.getPet_ageYear() + " ปี " + pets.getPet_ageMonth() + " เดือน " + pets.getPet_ageDay() + " วัน");

                visiblePetInformation();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("SHOWINFORMATIONPET", e.getMessage());
            }
        });
    }

    private void getViewID() {
        _bgContent = getView().findViewById(R.id.frg_menu_veterinary_pets_add_bgdetail);
        _imagePet = getView().findViewById(R.id.frg_menu_veterinary_pets_add_image);
        _petNameTxt = getView().findViewById(R.id.frg_menu_veterinary_pets_add_name);
        _petTypeTxt = getView().findViewById(R.id.frg_menu_veterinary_pets_add_type);
        _petSexTxt = getView().findViewById(R.id.frg_menu_veterinary_pets_add_sex);
        _petAgeTxt = getView().findViewById(R.id.frg_menu_veterinary_pets_add_age);
        _requestBtn = getView().findViewById(R.id.frg_menu_veterinary_pets_add_requestBtn);
    }

    private void visiblePetInformation() {
        _bgContent.setVisibility(View.VISIBLE);
        _imagePet.setVisibility(View.VISIBLE);
        _petNameTxt.setVisibility(View.VISIBLE);
        _petTypeTxt.setVisibility(View.VISIBLE);
        _petSexTxt.setVisibility(View.VISIBLE);
        _petAgeTxt.setVisibility(View.VISIBLE);
        _requestBtn.setVisibility(View.VISIBLE);
    }

    private void invisibleInformation() {
        _bgContent.setVisibility(View.INVISIBLE);
        _imagePet.setVisibility(View.INVISIBLE);
        _petNameTxt.setVisibility(View.INVISIBLE);
        _petTypeTxt.setVisibility(View.INVISIBLE);
        _petSexTxt.setVisibility(View.INVISIBLE);
        _petAgeTxt.setVisibility(View.INVISIBLE);
        _requestBtn.setVisibility(View.INVISIBLE);
    }
}
