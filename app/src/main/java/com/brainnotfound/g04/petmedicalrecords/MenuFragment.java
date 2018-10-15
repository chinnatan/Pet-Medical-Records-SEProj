package com.brainnotfound.g04.petmedicalrecords;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.module.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class MenuFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private Profile _getProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        _getProfile = Profile.getProfileInstance();

        getProfile(mAuth.getCurrentUser());
        getCountPet(mAuth.getCurrentUser());
        initSignoutBtn();
    }

    void initSignoutBtn() {
        TextView _signoutBtn = getView().findViewById(R.id.menu_signoutBtn);
        _signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new LoginFragment()).commit();
            }
        });
    }

    void getProfile(FirebaseUser _user) {
        if(_getProfile.getFirstname() == null) {
            mStore.collection("account").document(_user.getUid())
                    .collection("profile").document(_user.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> checkData = documentSnapshot.getData();
                        if (checkData.size() != 0) {
                            _getProfile.setFirstname(documentSnapshot.getString("firstname"));
                            _getProfile.setLastname(documentSnapshot.getString("lastname"));
                            initProfile(_getProfile);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            initProfile(_getProfile);
        }
    }

    void initProfile(Profile _profile) {
        TextView _profileNameTxt = getView().findViewById(R.id.menu_profilename);
        _profileNameTxt.setText(_profile.getFirstname() + " " + _profile.getLastname());
    }

    void getCountPet(FirebaseUser _user) {
        mStore.collection("account").document(_user.getUid())
                .collection("pet").orderBy("name", Query.Direction.DESCENDING)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()) {
                    initCountPet();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void initCountPet() {
        TextView _countPet = getView().findViewById(R.id.menu_show_count_pet);
        _countPet.setText("จำนวนสัตว์เลี้ยงทั้งหมดของคุณคือ 0 ตัว");
    }
}
