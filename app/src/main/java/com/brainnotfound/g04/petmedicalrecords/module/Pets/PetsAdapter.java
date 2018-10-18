package com.brainnotfound.g04.petmedicalrecords.module.Pets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brainnotfound.g04.petmedicalrecords.R;

import java.util.ArrayList;
import java.util.List;

public class PetsAdapter extends ArrayAdapter<Pets> {

    List<Pets> pet = new ArrayList<Pets>();
    Context context;

    public PetsAdapter(Context context, int resource, List<Pets> objects){
        super(context, resource, objects);

        this.pet = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View _petsItem = LayoutInflater.from(context).inflate(R.layout.fragment_menu_pets_item, parent, false);

        TextView _nameTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_name);
        TextView _typeTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_type);
        TextView _sexTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_sex);
        TextView _ageTxt = _petsItem.findViewById(R.id.frg_menu_pets_item_age);

        Pets _rows = pet.get(position);
        _nameTxt.setText(_rows.getPet_name());
        _typeTxt.setText(_rows.getPet_type());
        _sexTxt.setText(_rows.getPet_sex());
        _ageTxt.setText(_rows.getPet_age().toString());

        return _petsItem;
    }
}
