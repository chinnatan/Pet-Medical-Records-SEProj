package com.brainnotfound.g04.petmedicalrecords.control.veterinary;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.History;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AddHistoryFragment extends Fragment {

    private static final String TAG = "ADDHISTORY";
    private final String title = "เพิ่มประวัติการรักษา";

    private User user;
    private Pet pet;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<String> vaccineList = new ArrayList<>();

    private Toolbar zToolbar;
    private TextInputEditText zHistoryTitle;
    private TextInputEditText zHistoryDetail;
    private Button zSubmit;

    // Vaccine Cat
    private CheckBox zHistoryVaccineCat1;
    private CheckBox zHistoryVaccineCat2;
    private CheckBox zHistoryVaccineCat3;
    private CheckBox zHistoryVaccineCat4;
    private CheckBox zHistoryVaccineCat5;
    private CheckBox zHistoryVaccineCat6;
    private CheckBox zHistoryVaccineCat7;
    private CheckBox zHistoryVaccineCat8;

    // Vaccine Dog
    private CheckBox zHistoryVaccineDog1;
    private CheckBox zHistoryVaccineDog2;
    private CheckBox zHistoryVaccineDog3;
    private CheckBox zHistoryVaccineDog4;
    private CheckBox zHistoryVaccineDog5;
    private CheckBox zHistoryVaccineDog6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pet = Pet.getPetInstance();
        if (pet.getPettype().equals("แมว")) {
            return inflater.inflate(R.layout.fragment_veterinary_addhistory_cat, container, false);
        } else {
            return inflater.inflate(R.layout.fragment_veterinary_addhistory_dog, container, false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);
        user = User.getUserInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        addhistoryFragmentElements();
        createMenu();
        initSubmit();
    }

    private void addhistoryFragmentElements() {
        zToolbar = getView().findViewById(R.id.history_action_bar);
        zHistoryTitle = getView().findViewById(R.id.veterinary_addhistory_title);
        zHistoryDetail = getView().findViewById(R.id.veterinary_addhistory_detail);
        zSubmit = getView().findViewById(R.id.veterinary_addhistory_submit);

        if (pet.getPettype().equals("แมว")) {
            zHistoryVaccineCat1 = getView().findViewById(R.id.veterinary_addhistory_vaccine_cat_1);
            zHistoryVaccineCat2 = getView().findViewById(R.id.veterinary_addhistory_vaccine_cat_2);
            zHistoryVaccineCat3 = getView().findViewById(R.id.veterinary_addhistory_vaccine_cat_3);
            zHistoryVaccineCat4 = getView().findViewById(R.id.veterinary_addhistory_vaccine_cat_4);
            zHistoryVaccineCat5 = getView().findViewById(R.id.veterinary_addhistory_vaccine_cat_5);
            zHistoryVaccineCat6 = getView().findViewById(R.id.veterinary_addhistory_vaccine_cat_6);
            zHistoryVaccineCat7 = getView().findViewById(R.id.veterinary_addhistory_vaccine_cat_7);
            zHistoryVaccineCat8 = getView().findViewById(R.id.veterinary_addhistory_vaccine_cat_8);
        } else {
            zHistoryVaccineDog1 = getView().findViewById(R.id.veterinary_addhistory_vaccine_dog_1);
            zHistoryVaccineDog2 = getView().findViewById(R.id.veterinary_addhistory_vaccine_dog_2);
            zHistoryVaccineDog3 = getView().findViewById(R.id.veterinary_addhistory_vaccine_dog_3);
            zHistoryVaccineDog4 = getView().findViewById(R.id.veterinary_addhistory_vaccine_dog_4);
            zHistoryVaccineDog5 = getView().findViewById(R.id.veterinary_addhistory_vaccine_dog_5);
            zHistoryVaccineDog6 = getView().findViewById(R.id.veterinary_addhistory_vaccine_dog_6);
        }
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
    }

    private void initSubmit() {
        Log.d(TAG, "init submit button");
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("กำลังเพิ่มประวัติการรักษา...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        Log.d(TAG, "check pet type");
        if (pet.getPettype().equals("แมว")) {
            zSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "submit button : clicked");
                    progressDialog.show();

                    DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
                    DateFormat fulldate = new SimpleDateFormat("dd/MM/yyyy");
                    DateFormat datetime = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date();

                    String historytitle = zHistoryTitle.getText().toString();
                    String historydetail = zHistoryDetail.getText().toString();

                    List<CheckBox> vaccineItems = new ArrayList<CheckBox>();
                    vaccineItems.add(zHistoryVaccineCat1);
                    vaccineItems.add(zHistoryVaccineCat2);
                    vaccineItems.add(zHistoryVaccineCat3);
                    vaccineItems.add(zHistoryVaccineCat4);
                    vaccineItems.add(zHistoryVaccineCat5);
                    vaccineItems.add(zHistoryVaccineCat6);
                    vaccineItems.add(zHistoryVaccineCat7);
                    vaccineItems.add(zHistoryVaccineCat8);
                    for (CheckBox item : vaccineItems) {
                        if (item.isChecked()) {
                            vaccineList.add(item.getText().toString());
                        }
                    }

                    if (historytitle.isEmpty() || historydetail.isEmpty()) {
                        Toast.makeText(getActivity(), "กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                    } else {
                        History historyPet;
                        if (vaccineList.isEmpty()) {
                            vaccineList.add("-");
                        }
                        historyPet = new History(pet.getPetkey(), "HT" + dateFormat.format(date), historytitle, historydetail, vaccineList,
                                user.getUid(), fulldate.format(date), datetime.format(date));

                        firebaseFirestore.collection("pet").document(pet.getPetkey())
                                .collection("history").document("HT" + dateFormat.format(date))
                                .set(historyPet)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity(), "เพิ่มประวัติการรักษาสำเร็จ", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        getActivity().getSupportFragmentManager().popBackStack();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "add history is failed : " + e.getMessage());
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            });
        }
    }
}
