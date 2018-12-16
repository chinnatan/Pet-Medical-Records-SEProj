package com.brainnotfound.g04.petmedicalrecords.control.petowner.adapter;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PetAdapter extends ArrayAdapter {

    private List<Pet> petList = new ArrayList<Pet>();

    private Context context;

    // Firebase
    private FirebaseStorage firebaseStorage;
    // Load Image
    private StorageReference storageReference;

    private TextView zPetname;
    private TextView zPettype;

    public PetAdapter(@NonNull Context context, int resourse, @NonNull List<Pet> objects) {
        super(context, resourse, objects);
        this.petList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View listPetItem = LayoutInflater.from(context).inflate(R.layout.fragment_pet_item, parent, false);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final ImageView zImageViewPet = listPetItem.findViewById(R.id.pet_item_image);
        zPetname = listPetItem.findViewById(R.id.pet_item_petname);
        zPettype = listPetItem.findViewById(R.id.pet_item_pettype);

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
        zPettype.setText(row.getPettype());
        zPetname.setVisibility(View.VISIBLE);
        zPettype.setVisibility(View.VISIBLE);

        return listPetItem;
    }
}
