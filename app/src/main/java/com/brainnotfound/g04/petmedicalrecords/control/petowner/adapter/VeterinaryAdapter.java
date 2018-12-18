package com.brainnotfound.g04.petmedicalrecords.control.petowner.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.control.LoginFragment;
import com.brainnotfound.g04.petmedicalrecords.module.Rating;
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

import java.util.ArrayList;

public class VeterinaryAdapter extends ArrayAdapter {

    private static final String TAG = "VETERINARYADAPTER";

    private ArrayList<Request> requestArrayList = new ArrayList<Request>();
    private Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private User user;
    private FragmentActivity fragmentActivity;
    private VeterinaryAdapter adapter;
    private boolean isFragmentAdded = false;

    private float ratingValue;
    private float ratingShow;

    public VeterinaryAdapter(@NonNull Context context, int resourse, @NonNull ArrayList<Request> objects, FragmentActivity fragmentActivity, boolean isFragmentAdded) {
        super(context, resourse, objects);
        this.requestArrayList = objects;
        this.context = context;
        this.fragmentActivity = fragmentActivity;
        this.adapter = this;
        this.isFragmentAdded = isFragmentAdded;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View listRequestItem = LayoutInflater.from(context).inflate(R.layout.fragment_petowner_veterinary_item, parent, false);

        user = User.getUserInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final ImageView zImageViewVet = listRequestItem.findViewById(R.id.petowner_veterinary_item_image);
        final TextView zVetname = listRequestItem.findViewById(R.id.petowner_veterinary_item_vetname);
        final TextView zVetphone = listRequestItem.findViewById(R.id.petowner_veterinary_item_vetphone);
        final TextView zPetname = listRequestItem.findViewById(R.id.petowner_veterinary_item_petname);
        final Button zRatingBtn = listRequestItem.findViewById(R.id.petowner_veterinary_item_addrating);
        final Button zCancleBtn = listRequestItem.findViewById(R.id.petowner_veterinary_item_cancle);
        final RatingBar zRatingbar = listRequestItem.findViewById(R.id.petowner_veterinary_item_rating);

        final Request row = requestArrayList.get(position);

        firebaseFirestore.collection("account").document(row.getVeterinaryuid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                storageReference.child(documentSnapshot.getString("image")).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if(isFragmentAdded) {
                                    Glide.with(listRequestItem).load(uri).apply(RequestOptions.circleCropTransform()).into(zImageViewVet);
                                }
                                zVetname.setText(documentSnapshot.getString("fullname"));
                                zVetphone.setText(documentSnapshot.getString("phonenumber"));
                                firebaseFirestore.collection("pet").document(row.getPetkey())
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        zPetname.setText("ชื่อสัตว์เลี้ยง : " + documentSnapshot.getString("petname"));
                                        zRatingBtn.setVisibility(View.VISIBLE);
                                        zCancleBtn.setVisibility(View.VISIBLE);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "pet is failed : " + e.getMessage());
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "load image is failed : " + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "account is failed : " + e.getMessage());
            }
        });

        firebaseFirestore.collection("account").document(row.getVeterinaryuid())
                .collection("rating")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ratingShow = 0;
                        if(!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                ratingShow += documentSnapshot.getDouble("rating");
                            }
                            zRatingbar.setRating(ratingShow/queryDocumentSnapshots.size());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "load rating is failed : " + e.getMessage());
            }
        });

        zRatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRatingDialog(row);
            }
        });

        zCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayConfirmDeleteDialog(row);
            }
        });

        return listRequestItem;
    }

    private void displayRatingDialog(final Request request) {
        ratingValue = 0;
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialog_ratingbar,null);
        final RatingBar ratebar = (RatingBar)mView.findViewById(R.id.ratingBar);
        ratebar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = rating;
            }
        });
        builder.setView(mView);
        builder.setTitle("ให้คะแนนสัตวแพทย์")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Rating rating = new Rating(request.getCustomeruid(), ratingValue);
                        firebaseFirestore.collection("account").document(request.getVeterinaryuid())
                                .collection("rating").document(request.getCustomeruid())
                                .set(rating)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(fragmentActivity, "ให้คะแนนเรียบร้อย", Toast.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "add rating is failed : " + e.getMessage());
                            }
                        });
                    }
                })
                .show();
    }

    private void displayConfirmDeleteDialog(final Request request) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("ยืนยันการยกเลิก")
                .setMessage("คุณแน่ใจหรือไม่ที่จะทำการยกเลิกสิทธิ์การเข้าถึง")
                .setPositiveButton("ยืนยีน", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseFirestore.collection("request").document(request.getRequestkey())
                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(fragmentActivity, "ยกเลิกสิทธิ์การเข้าถึงเรียบร้อย", Toast.LENGTH_LONG).show();
                                requestArrayList.remove(request);
                                adapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "decline is failed : " + e.getMessage());
                            }
                        });
                    }
                })
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
