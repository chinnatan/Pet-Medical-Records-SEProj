package com.brainnotfound.g04.petmedicalrecords;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.control.HomeFragment;
import com.brainnotfound.g04.petmedicalrecords.control.LoginFragment;
import com.brainnotfound.g04.petmedicalrecords.control.MyFragment;
import com.brainnotfound.g04.petmedicalrecords.module.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";

    private static BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private Fragment fragmentHome;
    private Fragment fragmentVeterinary;
    private Fragment fragmentRequest;
    private Fragment fragmentMy;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fragmentHome = new HomeFragment();
        fragmentMy = new MyFragment();
        init(savedInstanceState);
        setupNav();
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void init(Bundle bundle) {
        if(firebaseAuth.getCurrentUser() == null) {
            if (bundle == null) {
                fragment = new LoginFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, fragment)
                        .commit();
            }
        } else if(firebaseAuth.getCurrentUser() != null) {
            if (bundle == null) {
                fragment = new HomeFragment();
                firebaseFirestore.collection("account").document(firebaseAuth.getCurrentUser().getUid())
                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = User.getUserInstance();
                        user.setUid(firebaseAuth.getCurrentUser().getUid());
                        user.setFullname(documentSnapshot.getString("fullname"));
                        user.setPhonenumber(documentSnapshot.getString("phonenumber"));
                        user.setImage(documentSnapshot.getString("image"));
                        user.setType(documentSnapshot.getString("type"));
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_view, fragment)
                                .commit();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "LOAD DATA ERROR - " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private void setupNav() {
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        fragment = fragmentHome;
                        break;
                    case R.id.navigation_veterinary:
                        fragment = fragmentVeterinary;
                        break;
                    case R.id.navigation_request:
                        fragment = fragmentRequest;
                        break;
                    case R.id.navigation_profile:
                        fragment = fragmentMy;
                        break;
                }

                if(fragment != null) {
                    Log.d(TAG, "Change page");
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_view, fragment)
                            .commit();
                }
                return true;
            }
        });
    }

    public static void onFragmentChanged(String fragmentName) {
        Log.d(TAG, "onFragmentChanged: (Change to page)" + fragmentName);
        if (fragmentName.equalsIgnoreCase("HOME") || fragmentName.equalsIgnoreCase("MY")) {
            if (fragmentName.equals("HOME")) {
                Log.d(TAG, "check nav 0");
                bottomNavigationView.getMenu().getItem(0).setChecked(true);
            }
            else if (fragmentName.equals("MY")) {
                Log.d(TAG, "check nav 3");
                bottomNavigationView.getMenu().getItem(3).setChecked(true);
            }
//            else if (fragmentName.equals("ME")) {
//                Log.d(TAG, "check nav 2");
//                bottomNavigationView.getMenu().getItem(2).setChecked(true);
//            }
            Log.d(TAG, "Visible nav");
            bottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "Invisible nav");
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

}
