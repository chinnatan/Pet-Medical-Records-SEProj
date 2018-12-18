package com.brainnotfound.g04.petmedicalrecords.control.petowner;

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
import android.widget.TextView;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.petowner.adapter.VeterinaryAdapter;
import com.brainnotfound.g04.petmedicalrecords.module.Request;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class VeterinaryFragment extends Fragment {

    private static final String TAG = "VETERINARY";

    private final String title = "สัตวแพทย์";

    private Toolbar zToolbar;
    private ProgressBar zLoading;
    private ListView zVetlist;
    private TextView zNotfound;

    private User user;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<Request> zVeterinaryArrayList = new ArrayList<>();
    private VeterinaryAdapter zVeterinaryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_petowner_veterinary, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);
        user = User.getUserInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        veterinaryFragmentElements();
        createMenu();

        zVeterinaryAdapter = new VeterinaryAdapter(getActivity(), R.layout.fragment_petowner_veterinary_item, zVeterinaryArrayList, getActivity(), this.isAdded());
        loadVeterinary();
    }

    private void loadVeterinary() {
        firebaseFirestore.collection("request")
                .whereEqualTo("customeruid", user.getUid())
                .whereEqualTo("status", "อนุมัติ")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        zVeterinaryAdapter.clear();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Request requestData = document.toObject(Request.class);
                                zVeterinaryArrayList.add(requestData);
                            }

                            zVeterinaryAdapter.notifyDataSetChanged();
                            zVetlist.setAdapter(zVeterinaryAdapter);
                            zLoading.setVisibility(View.GONE);
                        } else {
                            zNotfound.setText("ไม่พบสัตวแพทย์");
                            zNotfound.setVisibility(View.VISIBLE);
                            zLoading.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "load request is failed : " + e.getMessage());
                zLoading.setVisibility(View.GONE);
            }
        });
    }

    private void veterinaryFragmentElements() {
        zToolbar = getView().findViewById(R.id.toolbar);
        zLoading = getView().findViewById(R.id.petowner_veterinary_loading);
        zVetlist = getView().findViewById(R.id.petowner_veterinary_list);
        zNotfound = getView().findViewById(R.id.petowner_veterinary_notfound);
    }

    private void createMenu() {
        Log.d(TAG, "create menu");
        Objects.requireNonNull(getActivity()).setActionBar(zToolbar);
        zToolbar.setTitle(title);
    }
}
