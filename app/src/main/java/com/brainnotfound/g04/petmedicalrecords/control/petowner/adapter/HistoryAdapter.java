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
import com.brainnotfound.g04.petmedicalrecords.module.History;
import com.brainnotfound.g04.petmedicalrecords.module.Pet;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter {

    private List<History> historyList = new ArrayList<History>();

    private Context context;

    private TextView zTitle;
    private TextView zAddby;
    private TextView zDate;

    public HistoryAdapter(@NonNull Context context, int resourse, @NonNull List<History> objects) {
        super(context, resourse, objects);
        this.historyList = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View listHistoryItem = LayoutInflater.from(context).inflate(R.layout.fragment_history_item, parent, false);

        zTitle = listHistoryItem.findViewById(R.id.histoery_item_title);
        zAddby = listHistoryItem.findViewById(R.id.histoery_item_addby);
        zDate = listHistoryItem.findViewById(R.id.histoery_item_date);

        final History row = historyList.get(position);

        zTitle.setText(row.getTitle());
        zAddby.setText(row.getAddby());
        zDate.setText("วันที่ " + row.getDate() + " | เวลา " + row.getDatetime());

        return listHistoryItem;
    }
}
