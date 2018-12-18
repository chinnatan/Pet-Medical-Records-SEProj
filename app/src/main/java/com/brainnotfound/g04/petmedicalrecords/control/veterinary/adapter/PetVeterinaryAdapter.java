package com.brainnotfound.g04.petmedicalrecords.control.veterinary.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.brainnotfound.g04.petmedicalrecords.module.Request;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class PetVeterinaryAdapter extends ArrayAdapter {

    private static final String TAG = "PETVETERINARYADAPTER";

    private ArrayList<Request> requestList = new ArrayList<Request>();
    private ArrayList<Pet> petList = new ArrayList<Pet>();
    private Context context;

    private User user;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private boolean isFragmentAdded = false;

    public PetVeterinaryAdapter(@NonNull Context context, int resourse, @NonNull ArrayList<Pet> objects, @NonNull ArrayList<Request> requestObj, boolean isFragmentAdded) {
        super(context, resourse, objects);
        this.petList = objects;
        this.requestList = requestObj;
        this.context = context;
        this.isFragmentAdded = isFragmentAdded;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View listPetItem = LayoutInflater.from(context).inflate(R.layout.fragment_pet_veterinary_item, parent, false);

        final Request request = new Request();
        user = User.getUserInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final ImageView zImageViewPet = listPetItem.findViewById(R.id.frg_pet_vet_image);
        final TextView zPetname = listPetItem.findViewById(R.id.frg_pet_vet_petname);
        final TextView zPettype = listPetItem.findViewById(R.id.frg_pet_vet_pettype);
        final TextView zStatus = listPetItem.findViewById(R.id.frg_pet_vet_status);

        final Pet row = petList.get(position);
        final Request requestRow = requestList.get(position);
        storageReference.child(row.getPetimage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(isFragmentAdded) {
                    Glide.with(listPetItem).load(uri).apply(RequestOptions.circleCropTransform()).into(zImageViewPet);
                }
                zImageViewPet.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Load image failed : " + e.getMessage());
            }
        });

        zPetname.setText(row.getPetname());
        zPettype.setText(row.getPettype());
        zStatus.setText(requestRow.getStatus());
        zPetname.setVisibility(View.VISIBLE);
        zPettype.setVisibility(View.VISIBLE);
        zStatus.setVisibility(View.VISIBLE);

        return listPetItem;
    }
}
