package com.example.finalsproject.dashboard_user_activities;

import static com.example.finalsproject.SplashScreen.EMAIL;
import static com.example.finalsproject.SplashScreen.PASSWORD;
import static com.example.finalsproject.SplashScreen.SHARED_PREFS;
import static com.example.finalsproject.SplashScreen.STATUS;
import static com.example.finalsproject.SplashScreen.UID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalsproject.R;
import com.example.finalsproject.login;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String uiD;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        //FIREBASE INSTANCES//
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        uiD = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        setNavigationView();
    }
    private void setNavigationView(){
        //NAVIGATION//
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        //actionbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //navigation
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        // HANDLE HOW EACH DASHBOARD DIFFERS PER USER THROUGH LAYOUTS //
        DocumentReference documentReference = fStore.collection("users").document(uiD);
        documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                Menu menu = navigationView.getMenu();
                menu.clear();
                switch (Objects.requireNonNull(documentSnapshot.getString("acc_type")).toLowerCase()){
                    case "admin":
                        navigationView.inflateMenu(R.menu.admin_menu);
                        break;
                    case "customer":
                        navigationView.inflateMenu(R.menu.customer_menu);
                        break;
                    case "employee":
                        navigationView.inflateMenu(R.menu.employee_menu);
                        break;
                    case "subcontractor":
                        navigationView.inflateMenu(R.menu.subcontractor_menu);
                        break;
                }
            }
        });
    }

    @Override   //FRAGMENTS UTILIZATION// ISOLATE EACH FRAGMENT BASED ON ACCOUNT TYPE //
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        item.setCheckable(false);
        DocumentReference documentReference = fStore.collection("users").document(uiD);
        documentReference.addSnapshotListener(this, (documentSnapshot, error) -> {
            if (documentSnapshot != null) {
                switch (Objects.requireNonNull(documentSnapshot.getString("acc_type")).toLowerCase()){
                    case "admin":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                setHomeFragment();
                                break;
                            case "Account Management":
                                //
                                break;
                            case "Transaction History":
                                //
                                break;
                            case "Profile":
                                setProfileFragment(new profile());
                                break;
                            case "Logout":
                                logout();
                                break;
                        }
                    break;
                    case "employee":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                setHomeFragment();
                                break;
                            case "Delivery Inquiries":
                                //
                                break;
                            case "Pending Deliveries":
                                //
                                break;
                            case "Deliveries History":
                                //
                                break;
                            case "Profile":
                                setProfileFragment(new profile());
                                break;
                            case "Account Settings":
                                setAccountSettingsFragment(new account_settings());
                                break;
                            case "Logout":
                                logout();
                                break;
                        }
                    break;
                    case "customer":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                setHomeFragment();
                                break;
                            case "Inquire Lost Luggage":
                                //
                                break;
                            case "Luggage Monitoring":
                                //
                                break;
                            case "Profile":
                                setProfileFragment(new profile());
                                break;
                            case "Account Settings":
                                setAccountSettingsFragment(new account_settings());
                                break;
                            case "Logout":
                                logout();
                                break;
                        }
                    break;
                    case "subcontractor":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                setHomeFragment();
                                break;
                            case "Available Deliveries":
                                //
                                break;
                            case "Delivery History":
                                //
                                break;
                            case "Profile":
                                setProfileFragment(new profile());
                                break;
                            case "Account Settings":
                                setAccountSettingsFragment(new account_settings());
                                break;
                            case "Logout":
                                logout();
                                break;
                        }
                    break;
                }
            }
        });
        return true;
    }
    public void un_remember_save(){ //SHARED PREFERENCES SET BACK TO DEFAULT VALUES
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(STATUS, false)
        .putString(UID,"uid")
        .putString(EMAIL,"email")
        .putString(PASSWORD,"password")
        .apply();
    }
    private void logout(){
        fAuth.signOut();
        un_remember_save();
        startActivity(new Intent(this, login.class));
        finish();
    }
    @Override
    public void onBackPressed() {//slide to back function
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    //ALL FRAGMENTS//
    private void setHomeFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dashboard_layout, new Fragment());
        transaction.addToBackStack(null);
        transaction.commit();

    }
    private void setProfileFragment(profile a) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,a);
        fragmentTransaction.commit();
    }
    private void setAccountSettingsFragment(account_settings a) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,a);
        fragmentTransaction.commit();
    }
}