package com.brainnotfound.g04.petmedicalrecords;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.brainnotfound.g04.petmedicalrecords.module.SaveFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new IntroFragment()).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(SaveFragment.getName().equals("registerFragment")) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_view, new IntroFragment()).commit();
        }
    }
}
