package com.brainnotfound.g04.petmedicalrecords.control.veterinary;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PetRequestAdapter extends ArrayAdapter {

    private ArrayList<Pet> petList = new ArrayList<Pet>();

    private Context context;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public PetRequestAdapter(@NonNull Context context, int resourse, @NonNull ArrayList<Pet> objects) {
        super(context, resourse, objects);
        this.petList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View listPetItem = LayoutInflater.from(context).inflate(R.layout.fragment_addrequest_item, parent, false);

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

        return listPetItem;
    }
}
