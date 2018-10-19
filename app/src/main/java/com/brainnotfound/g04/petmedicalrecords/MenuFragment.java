package com.brainnotfound.g04.petmedicalrecords;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.module.Pets.PetsFragment;
import com.brainnotfound.g04.petmedicalrecords.module.Profile;
import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;
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
    private SaveFragment saveFragment;
    private ProgressBar loadingMenu;

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
        saveFragment = SaveFragment.getSaveFragmentInstance();
        loadingMenu = getView().findViewById(R.id.menu_loading);
        String _userUid = mAuth.getCurrentUser().getUid();

        invisibleMenu();
        getProfileAndCountPet(_userUid);
        getCountPet(_userUid);
        initSignoutBtn();
        initProfileBtn();
        initPetsBtn();
    }

    void initSignoutBtn() {
        TextView _signoutBtn = getView().findViewById(R.id.menu_signoutBtn);
        _signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFragment.setName("LoginFragment");
                _getProfile.setFirstname(null);
                mAuth.signOut();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.main_view, new LoginFragment()).commit();
            }
        });
    }

    void initProfileBtn() {
        LinearLayout _profileLiBtn = getView().findViewById(R.id.layout_fragment_menu_profile);
        _profileLiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFragment.setName("ProfileFragment");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.main_view, new ProfileFragment()).commit();
            }
        });
    }

    void initPetsBtn() {
        LinearLayout _petsBtn = getView().findViewById(R.id.layout_fragment_menu_pet);
        _petsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFragment.setName("PetsFragment");
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.main_view, new PetsFragment()).commit();
            }
        });
    }

    private void getProfileAndCountPet(String userUid) {
        if(_getProfile.getFirstname() == null) {
            mStore.collection("account").document(userUid)
                    .collection("profile").document(userUid)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    loadingMenu.setVisibility(View.GONE);
                    visibleMenu();

                    if (documentSnapshot.exists()) {
                        Map<String, Object> checkData = documentSnapshot.getData();
                        if (checkData.size() != 0) {
                            _getProfile.setFirstname(documentSnapshot.getString("firstname"));
                            _getProfile.setLastname(documentSnapshot.getString("lastname"));
                            _getProfile.setPhonenumber(documentSnapshot.getString("phonenumber"));
                            _getProfile.setAccount_type(documentSnapshot.getString("account_type"));
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
            loadingMenu.setVisibility(View.GONE);
            visibleMenu();
            initProfile(_getProfile);
        }
    }

    private void getCountPet(String userUid) {
        mStore.collection("account").document(userUid)
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

    void initProfile(Profile _profile) {
        TextView _profileNameTxt = getView().findViewById(R.id.menu_profilename);
        _profileNameTxt.setText(_profile.getFirstname() + " " + _profile.getLastname());
    }

    void initCountPet() {
        TextView _countPet = getView().findViewById(R.id.menu_show_count_pet);
        _countPet.setText("จำนวนสัตว์เลี้ยงทั้งหมดของคุณคือ 0 ตัว");
    }

    private void invisibleMenu() {
        ImageView _profileImage = getView().findViewById(R.id.menu_profileImages);
        TextView _signoutBtn = getView().findViewById(R.id.menu_signoutBtn);
        TextView _name = getView().findViewById(R.id.menu_profilename);
        TextView _countpet = getView().findViewById(R.id.menu_show_count_pet);
        LinearLayout _profile = getView().findViewById(R.id.layout_fragment_menu_profile);
        LinearLayout _pets = getView().findViewById(R.id.layout_fragment_menu_pet);
        LinearLayout _veterinary = getView().findViewById(R.id.layout_fragment_menu_veterinary);
        LinearLayout _history = getView().findViewById(R.id.layout_fragment_menu_history);
        LinearLayout _medicine = getView().findViewById(R.id.layout_fragment_menu_medicine);
        LinearLayout _request = getView().findViewById(R.id.layout_fragment_menu_request);
        LinearLayout _setting = getView().findViewById(R.id.layout_fragment_menu_setting);

        _profileImage.setVisibility(View.INVISIBLE);
        _signoutBtn.setVisibility(View.INVISIBLE);
        _name.setVisibility(View.INVISIBLE);
        _countpet.setVisibility(View.INVISIBLE);
        _profile.setVisibility(View.INVISIBLE);
        _pets.setVisibility(View.INVISIBLE);
        _veterinary.setVisibility(View.INVISIBLE);
        _history.setVisibility(View.INVISIBLE);
        _medicine.setVisibility(View.INVISIBLE);
        _request.setVisibility(View.INVISIBLE);
        _setting.setVisibility(View.INVISIBLE);
    }

    private void visibleMenu() {
        ImageView _profileImage = getView().findViewById(R.id.menu_profileImages);
        TextView _signoutBtn = getView().findViewById(R.id.menu_signoutBtn);
        TextView _name = getView().findViewById(R.id.menu_profilename);
        TextView _countpet = getView().findViewById(R.id.menu_show_count_pet);
        LinearLayout _profile = getView().findViewById(R.id.layout_fragment_menu_profile);
        LinearLayout _pets = getView().findViewById(R.id.layout_fragment_menu_pet);
        LinearLayout _veterinary = getView().findViewById(R.id.layout_fragment_menu_veterinary);
        LinearLayout _history = getView().findViewById(R.id.layout_fragment_menu_history);
        LinearLayout _medicine = getView().findViewById(R.id.layout_fragment_menu_medicine);
        LinearLayout _request = getView().findViewById(R.id.layout_fragment_menu_request);
        LinearLayout _setting = getView().findViewById(R.id.layout_fragment_menu_setting);

        _profileImage.setVisibility(View.VISIBLE);
        _signoutBtn.setVisibility(View.VISIBLE);
        _name.setVisibility(View.VISIBLE);
        _countpet.setVisibility(View.VISIBLE);
        _profile.setVisibility(View.VISIBLE);
        _pets.setVisibility(View.VISIBLE);
        _veterinary.setVisibility(View.VISIBLE);
        _history.setVisibility(View.VISIBLE);
        _medicine.setVisibility(View.VISIBLE);
        _request.setVisibility(View.VISIBLE);
        _setting.setVisibility(View.VISIBLE);
    }
}
