package com.brainnotfound.g04.petmedicalrecords.module.Pets;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PetsAdapter extends ArrayAdapter<Pets> {

    List<Pets> pet = new ArrayList<Pets>();
    Context context;
    StorageReference storageReference;


    public PetsAdapter(Context context, int resource, List<Pets> objects){
        super(context, resource, objects);

        this.pet = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View _petsItem = LayoutInflater.from(context).inflate(R.layout.fragment_menu_pets_item, parent, false);

        TextView _nameTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_name);
        TextView _typeTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_type);
        TextView _sexTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_sex);
        TextView _ageTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_age);
        final ImageView _petImg = _petsItem.findViewById(R.id.frg_menu_pets_item_image);


        Pets _rows = pet.get(position);
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child(_rows.getUrlImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(_petsItem).load(uri).apply(RequestOptions.circleCropTransform()).into(_petImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("PetsAdapter", e.getMessage());
            }
        });
        _nameTxt.setText("ชื่อ : " + _rows.getPet_name());
        _typeTxt.setText("ประเภท : " + _rows.getPet_type());
        _sexTxt.setText("เพศ : " + _rows.getPet_sex());
        _ageTxt.setText("อายุ : " + _rows.getPet_ageDay() + " วัน " + _rows.getPet_ageMonth() + " เดือน " + _rows.getPet_ageYear() + " ปี");

        return _petsItem;
    }
}
