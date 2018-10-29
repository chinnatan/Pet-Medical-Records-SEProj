package com.brainnotfound.g04.petmedicalrecords.Pets;

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
    FragmentActivity fragmentActivity;
    StorageReference storageReference;

    public PetsAdapter(Context context, int resource, List<Pets> objects, FragmentActivity fragmentActivity){
        super(context, resource, objects);

        this.pet = objects;
        this.context = context;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View _petsItem = LayoutInflater.from(context).inflate(R.layout.fragment_menu_pets_item, parent, false);

        TextView _nameTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_name);
        TextView _typeTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_type);
        TextView _sexTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_sex);
        TextView _ageTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_age);
        final ImageView _petImg = _petsItem.findViewById(R.id.frg_menu_pets_item_image);
        Button _moreBtn = _petsItem.findViewById(R.id.frg_menu_pets_item_moreBtn);


        final Pets _rows = pet.get(position);
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
        _ageTxt.setText("อายุ : " + _rows.getPet_ageYear() + " ปี " + _rows.getPet_ageMonth() + " เดือน " + _rows.getPet_ageDay() + " วัน");
        _moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pets petInformation = Pets.getGetPetsInstance();
                petInformation.setPet_name(_rows.getPet_name());
                petInformation.setPet_type(_rows.getPet_type());
                petInformation.setPet_sex(_rows.getPet_sex());
                petInformation.setPet_ageDay(_rows.getPet_ageDay());
                petInformation.setPet_ageMonth(_rows.getPet_ageMonth());
                petInformation.setPet_ageYear(_rows.getPet_ageYear());
                petInformation.setUrlImage(_rows.getUrlImage());
                petInformation.setKey(_rows.getKey());
                fragmentActivity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .addToBackStack(null)
                        .replace(R.id.main_view, new PetInformationFragment()).commit();
            }
        });

        return _petsItem;
    }
}
