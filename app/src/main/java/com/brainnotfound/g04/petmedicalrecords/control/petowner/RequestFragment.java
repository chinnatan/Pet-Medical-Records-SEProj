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
import com.brainnotfound.g04.petmedicalrecords.control.petowner.request.RequestAdapter;
import com.brainnotfound.g04.petmedicalrecords.control.veterinary.PetVeterinaryAdapter;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.brainnotfound.g04.petmedicalrecords.module.Request;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequestFragment extends Fragment {

    private static final String TAG = "REQUEST";
    private final String title = "คำขอ";

    private TextView zNotfound;
    private Toolbar zToolbar;
    private ProgressBar zLoading;
    private ListView zRequestlist;

    private User user;
    private FirebaseFirestore firebaseFirestore;

    private ArrayList<Request> zRequestArrayList = new ArrayList<>();
    private RequestAdapter zRequestAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_petowner_request, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);
        user = User.getUserInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        requestFragmentElements();
        createMenu();

        zRequestAdapter = new RequestAdapter(getActivity(), R.layout.fragment_petowner_request_item, zRequestArrayList, getActivity());
        loadRequest();
    }

    private void loadRequest() {
        firebaseFirestore.collection("request")
                .whereEqualTo("customeruid", user.getUid())
                .whereEqualTo("status", "รออนุมัติ")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        zRequestAdapter.clear();
                        if(!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if(document.getString("customeruid").equals(user.getUid()) && document.getString("status").equals("รออนุมัติ")) {
                                    Request requestData = document.toObject(Request.class);
                                    zRequestArrayList.add(requestData);
                                }
                            }

                            zRequestAdapter.notifyDataSetChanged();
                            zRequestlist.setAdapter(zRequestAdapter);
                            zLoading.setVisibility(View.GONE);
                        } else {
                            zNotfound.setText("ไม่พบคำขอ");
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

    private void requestFragmentElements() {
        Log.d(TAG, "registed elements");
        zNotfound = getView().findViewById(R.id.request_notfound);
        zToolbar = getView().findViewById(R.id.toolbar);
        zLoading = getView().findViewById(R.id.request_loading);
        zRequestlist = getView().findViewById(R.id.request_list);
    }

    private void createMenu() {
        Log.d(TAG, "create menu");
        Objects.requireNonNull(getActivity()).setActionBar(zToolbar);
        zToolbar.setTitle(title);
    }
}
