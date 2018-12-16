package com.brainnotfound.g04.petmedicalrecords.control.veterinary;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.HomeFragment;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.brainnotfound.g04.petmedicalrecords.module.Request;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PetRequestAdapter extends ArrayAdapter {

    private static final String TAG = "PETREQUESTADAPTER";

    private ArrayList<Pet> petList = new ArrayList<Pet>();
    private Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private User user;
    private FragmentActivity fragmentActivity;
    private ProgressDialog progressDialog;

    public PetRequestAdapter(@NonNull Context context, int resourse, @NonNull ArrayList<Pet> objects, FragmentActivity fragmentActivity) {
        super(context, resourse, objects);
        this.petList = objects;
        this.context = context;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View listPetItem = LayoutInflater.from(context).inflate(R.layout.fragment_addrequest_item, parent, false);

        progressDialog = new ProgressDialog(fragmentActivity);
        user = User.getUserInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final ImageView zImageViewPet = listPetItem.findViewById(R.id.addrequest_item_image);
        TextView zPetname = listPetItem.findViewById(R.id.addrequest_item_name);
        TextView zPetkey = listPetItem.findViewById(R.id.addrequest_item_key);
        Button zRequestBtn = listPetItem.findViewById(R.id.addrequest_item_requestbtn);

        final Pet row = petList.get(position);
        storageReference.child(row.getPetimage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(listPetItem).load(uri).apply(RequestOptions.circleCropTransform()).into(zImageViewPet);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("PETADAPTER", e.getMessage());
            }
        });

        zPetname.setText(row.getPetname());
        zPetkey.setText("PET ID : " + row.getPetkey());
        zPetname.setVisibility(View.VISIBLE);
        zPetkey.setVisibility(View.VISIBLE);
        zRequestBtn.setVisibility(View.VISIBLE);
        zRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("กำลังส่งคำขอ...");
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                final DateFormat dateFormatDoc = new SimpleDateFormat("ddMMyyyyHHmmss");
                final Date date = new Date();

                final Request request = new Request(String.valueOf(row.getPetkey()), row.getPetownerUid(), user.getUid(), "รออนุมัติ");
                request.setRequestkey("re" + dateFormatDoc.format(date));

                firebaseFirestore.collection("request").whereEqualTo("petkey", row.getPetkey())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if(!queryDocumentSnapshots.isEmpty()) {
                                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                        if(documentSnapshot.getString("veterinaryuid").equals(user.getUid())) {
                                            Toast.makeText(fragmentActivity, "คุณเคยส่งคำขอแล้ว. กรุณารอเจ้าของอนุมัติ", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        } else {
                                            firebaseFirestore.collection("request").document(request.getRequestkey())
                                                    .set(request)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(fragmentActivity, "ส่งคำขอเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                            fragmentActivity.getSupportFragmentManager().beginTransaction()
                                                                    .replace(R.id.main_view, new HomeFragment())
                                                                    .commit();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "SET DATA FAILED : " + e.getMessage());
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    firebaseFirestore.collection("request").document(request.getRequestkey())
                                            .set(request)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(fragmentActivity, "ส่งคำขอเรียบร้อย", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    fragmentActivity.getSupportFragmentManager().beginTransaction()
                                                            .replace(R.id.main_view, new HomeFragment())
                                                            .commit();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "SET DATA FAILED : " + e.getMessage());
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "CHECK DATA FAILED : " + e.getMessage());
                        progressDialog.dismiss();
                    }
                });
            }
        });

        return listPetItem;
    }
}
