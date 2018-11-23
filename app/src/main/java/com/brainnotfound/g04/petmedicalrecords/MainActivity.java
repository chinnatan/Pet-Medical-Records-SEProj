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

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MAINACTIVITY";

    private static BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private Fragment fragmentHome;
    private Fragment fragmentVeterinary;
    private Fragment fragmentRequest;
    private Fragment fragmentMy;

    private static FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fragmentHome = new HomeFragment();
        fragmentMy = new MyFragment();
        user = User.getUserInstance();
        init(savedInstanceState);
        checkCurrentUser();
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    private void init(Bundle bundle) {
        if (firebaseAuth.getCurrentUser() == null) {
            if (bundle == null) {
                fragment = new LoginFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, fragment)
                        .commit();
            }
        }
    }

    private void checkCurrentUser() {
        if (firebaseAuth.getCurrentUser() != null) {
            fragment = new HomeFragment();
            firebaseFirestore.collection("account").document(firebaseAuth.getCurrentUser().getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
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

    public static void onFragmentChanged(String fragmentName) {
        Log.d(TAG, "onFragmentChanged: (Change to page)" + fragmentName);
        if (user.getType() != null) {
            if (user.getType().equals("เจ้าของสัตว์เลี้ยง")) {
                if (fragmentName.equalsIgnoreCase("HOME") || fragmentName.equalsIgnoreCase("MY")) {
                    if (fragmentName.equals("HOME")) {
                        Log.d(TAG, "check nav 0");
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    } else if (fragmentName.equals("MY")) {
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
            } else if (user.getType().equals("สัตวแพทย์")) {
                if (fragmentName.equalsIgnoreCase("HOME") || fragmentName.equalsIgnoreCase("MY")) {
                    if (fragmentName.equals("HOME")) {
                        Log.d(TAG, "check nav 0");
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    } else if (fragmentName.equals("MY")) {
                        Log.d(TAG, "check nav 1");
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
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
        } else {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    public static void onBottomNavigationChanged(String usertype) {
        Log.d(TAG, "onBottomNavigationChanged: (Change to Menu)" + usertype);
        bottomNavigationView.getMenu().clear();
        if (usertype.equals("เจ้าของสัตว์เลี้ยง")) {
            bottomNavigationView.inflateMenu(R.menu.navigation);
        } else if (usertype.equals("สัตวแพทย์")) {
            bottomNavigationView.inflateMenu(R.menu.navigation_veterinary);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (user.getType().equals("เจ้าของสัตว์เลี้ยง")) {
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

            if (fragment != null) {
                Log.d(TAG, "Change page");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, fragment)
                        .commit();
            }
            return true;
        } else if (user.getType().equals("สัตวแพทย์")) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    fragment = fragmentHome;
                    break;
                case R.id.navigation_profile:
                    fragment = fragmentMy;
                    break;
            }

            if (fragment != null) {
                Log.d(TAG, "Change page");
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_view, fragment)
                        .commit();
            }
            return true;
        }
        return false;
    }
}
