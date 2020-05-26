package com.sizii.homty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        firebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigationView = findViewById(R.id.home_nav);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction homeTr = getSupportFragmentManager().beginTransaction();
        homeTr.replace(R.id.content, homeFragment, "");
        homeTr.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            HomeFragment homeFragment = new HomeFragment();
                            FragmentTransaction homeTr = getSupportFragmentManager().beginTransaction();
                            homeTr.replace(R.id.content, homeFragment, "");
                            homeTr.commit();
                            return true;
                        case R.id.nav_profile:
                            ProfileFragment profileFragment = new ProfileFragment();
                            FragmentTransaction profileTr = getSupportFragmentManager().beginTransaction();
                            profileTr.replace(R.id.content, profileFragment, "");
                            profileTr.commit();
                            return true;
                    }

                    return false;
                }
            };

    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {

        } else {
            startActivity(new Intent(DashboardActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }
}
