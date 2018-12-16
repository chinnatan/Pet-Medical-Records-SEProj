package com.brainnotfound.g04.petmedicalrecords.control.petowner.request;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class RequestAdapter extends ArrayAdapter {

    private static final String TAG = "REQUESTADAPTER";

    private ArrayList<Request> requestArrayList = new ArrayList<Request>();
    private Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private User user;

    public RequestAdapter(@NonNull Context context, int resourse, @NonNull ArrayList<Request> objects) {
        super(context, resourse, objects);
        this.requestArrayList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View listRequestItem = LayoutInflater.from(context).inflate(R.layout.fragment_petowner_request_item, parent, false);

        user = User.getUserInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final ImageView zImageViewVet = listRequestItem.findViewById(R.id.petowner_request_item_image);
        final TextView zVetname = listRequestItem.findViewById(R.id.petowner_request_item_vetname);
        final TextView zPetid = listRequestItem.findViewById(R.id.petowner_request_item_request);
        Button zAcceptBtn = listRequestItem.findViewById(R.id.petowner_request_item_accept);
        Button zDeclineBtn = listRequestItem.findViewById(R.id.petowner_request_item_decline);

        final Request row = requestArrayList.get(position);

        firebaseFirestore.collection("account").document(row.getVeterinaryuid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                storageReference.child(documentSnapshot.getString("image")).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(listRequestItem).load(uri).apply(RequestOptions.circleCropTransform()).into(zImageViewVet);
                                zVetname.setText(documentSnapshot.getString("fullname"));
                                zPetid.setText("PET ID : " + row.getPetkey());
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

        return listRequestItem;
    }

}
