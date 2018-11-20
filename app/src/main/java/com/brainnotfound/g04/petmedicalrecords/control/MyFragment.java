package com.brainnotfound.g04.petmedicalrecords.control;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.brainnotfound.g04.petmedicalrecords.MainActivity;
import com.brainnotfound.g04.petmedicalrecords.R;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class MyFragment extends Fragment {

    private static final String TAG = "MY";

    private final String title = "โปรไฟล์";

    private ImageView zImageViewMe;
    private TextView zFullname;
    private TextView zPhonenumber;
    private Toolbar zToolBar;
    private ProgressBar zLoading;

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.onFragmentChanged(TAG);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        user = User.getUserInstance();

        myFragmentElements();
        createMenu();
        loadMy();
    }

    private void myFragmentElements() {
        zLoading = getView().findViewById(R.id.my_loading);
        zToolBar = getView().findViewById(R.id.toolbar);
        zImageViewMe = getView().findViewById(R.id.frg_my_image);
        zFullname = getView().findViewById(R.id.frg_my_fullname);
        zPhonenumber = getView().findViewById(R.id.frg_my_phonenumber);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createMenu() {
        Log.d(TAG, "create menu");
        Objects.requireNonNull(getActivity()).setActionBar(zToolBar);
        zToolBar.setTitle(title);
        zToolBar.setOverflowIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_more_event));
        zToolBar.inflateMenu(R.menu.navigation_my);
        zToolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_edit:
                        Log.d(TAG, "menu item click : edit");
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new MyEditFragment()).addToBackStack(null).commit();
                        return true;
                    case R.id.navigation_logout:
                        Log.d(TAG, "menu item click : logout");
                        user = null;
                        firebaseAuth.signOut();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new LoginFragment()).commit();
                        return true;
                    default:
                        return true;
                }
            }
        });

    }

    private void loadMy() {
        Log.d(TAG, "load my");
        storageReference.child(user.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d(TAG, "load image and my : success");
                if (isAdded()) {
                    Glide.with(getActivity()).load(uri).apply(RequestOptions.circleCropTransform()).into(zImageViewMe);
                    zFullname.setText(user.getFullname());
                    zPhonenumber.setText(user.getPhonenumber());
                    zImageViewMe.setVisibility(View.VISIBLE);
                    zFullname.setVisibility(View.VISIBLE);
                    zPhonenumber.setVisibility(View.VISIBLE);
                }
                zLoading.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "load image failed : " + e.getMessage());
            }
        });
    }
}
