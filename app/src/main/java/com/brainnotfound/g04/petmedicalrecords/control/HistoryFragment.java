package com.brainnotfound.g04.petmedicalrecords.control;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.veterinary.EditHistoryFragment;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class HistoryFragment extends Fragment {

    private static final String TAG = "HISTORY";
    private Bundle historyBundle;
    private User user;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String title = "รายละเอียดประวัติการรักษา";
    private Toolbar zToolbar;
    private ProgressBar zLoading;
    private TextView zHistoryTitle;
    private TextView zHistoryDetail;
    private TextView zHistoryDate;
    private TextView zHistoryVaccine;
    private TextView zHistoryAddby;
    private ImageView zVetImage;

    // History Bundle
    private String petid;
    private String historyid;
    private String historytitle;
    private String historydetail;
    private String historydate;
    private String historydatetime;
    private String historyaddby;
    private ArrayList<String> vaccineList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);
        user = User.getUserInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        historyFragmentElements();
        loadHistoryBundle();
        loadHistory();
        createMenu();
    }

    private void loadHistoryBundle() {
        historyBundle = getArguments();
        petid = historyBundle.getString("petid");
        historyid = historyBundle.getString("historyid");
        historytitle = historyBundle.getString("historytitle");
        historydetail = historyBundle.getString("historydetail");
        historydate = historyBundle.getString("historydate");
        historydatetime = historyBundle.getString("historydatetime");
        historyaddby = historyBundle.getString("historyaddby");
        vaccineList = historyBundle.getStringArrayList("historyvaccine");
    }

    private void loadHistory() {
        firebaseFirestore.collection("pet").document(petid)
                .collection("history").document(historyid)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                zHistoryTitle.setText(documentSnapshot.getString("title"));
                zHistoryDetail.setText(documentSnapshot.getString("detail"));
                zHistoryDate.setText("วันที่ " + historydate + " | เวลา " + historydatetime + " น.");

                String vaccineLine = "";
                for (int vaccine = 0; vaccine < vaccineList.size(); vaccine++) {
                    if(vaccineList.get(vaccine).equals("-")) {
                        vaccineLine += "ไม่ได้ใช้";
                    } else {
                        vaccineLine += (vaccine + 1) + "." + vaccineList.get(vaccine) + "\n";
                    }
                }

                zHistoryVaccine.setText(vaccineLine);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "load pet history is failed : " + e.getMessage());
            }
        });

        firebaseFirestore.collection("account").document(historyaddby)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                zHistoryAddby.setText(documentSnapshot.getString("fullname"));

                storageReference.child(documentSnapshot.getString("image")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "load image and my : success");
                        if (isAdded()) {
                            Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(zVetImage);
                            zVetImage.setVisibility(View.VISIBLE);
                            zLoading.setVisibility(View.GONE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "load image failed : " + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "load account is failed : " + e.getMessage());
            }
        });
    }

    private void historyFragmentElements() {
        Log.d(TAG, "get elements");
        zToolbar = getView().findViewById(R.id.toolbar);
        zLoading = getView().findViewById(R.id.history_loading);
        zHistoryTitle = getView().findViewById(R.id.history_title);
        zHistoryDetail = getView().findViewById(R.id.history_detail);
        zHistoryDate = getView().findViewById(R.id.history_date);
        zHistoryVaccine = getView().findViewById(R.id.history_vaccine);
        zHistoryAddby = getView().findViewById(R.id.history_addby);
        zVetImage = getView().findViewById(R.id.history_image);
    }

    private void createMenu() {
        Log.d(TAG, "create menu");
        Objects.requireNonNull(getActivity()).setActionBar(zToolbar);
        zToolbar.setTitle(title);
        zToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        zToolbar.setNavigationContentDescription("Back");
        zToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

//        if (user.getType().equals("สัตวแพทย์")) {
//            zToolbar.setOverflowIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_more_event));
//            zToolbar.inflateMenu(R.menu.navigation_history);
//            zToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.navigation_edit:
//                            Log.d(TAG, "menu item click : edit");
//                            Fragment editHistoryFragment = new EditHistoryFragment();
//                            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
//                            editHistoryFragment.setArguments(historyBundle);
//                            fragmentTransaction.replace(R.id.main_view, editHistoryFragment);
//                            fragmentTransaction.commit();
//                            return true;
//                        default:
//                            return true;
//                    }
//                }
//            });
//        }

    }
}
