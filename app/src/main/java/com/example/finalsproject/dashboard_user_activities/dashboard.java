package com.example.finalsproject.dashboard_user_activities;

import static com.example.finalsproject.SplashScreen.EMAIL;
import static com.example.finalsproject.SplashScreen.PASSWORD;
import static com.example.finalsproject.SplashScreen.SHARED_PREFS;
import static com.example.finalsproject.SplashScreen.STATUS;
import static com.example.finalsproject.SplashScreen.UID;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalsproject.R;
import com.example.finalsproject.SplashScreen;
import com.example.finalsproject.dashboard_user_activities.admin.account_management;
import com.example.finalsproject.dashboard_user_activities.customer.inquire_lost_luggage;
import com.example.finalsproject.dashboard_user_activities.employee.delivery_inquiries;
import com.example.finalsproject.dashboard_user_activities.subcontractor.available_deliveries;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
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
        setHomeFragment(new fragment_home());
        DocumentReference documentReference = fStore.collection("users").document(uiD);
        documentReference.addSnapshotListener((value, error) -> {
            if (value != null) {
                if (!value.exists()) {
                    handleInvalidAccount();
                } else {
                    setNavigationView();
                }
            }
            else {
                Toast.makeText(this, "Logging out.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void handleInvalidAccount() {
        runOnUiThread(() -> {
            Toast.makeText(this, "Your account is invalid, please register an account.", Toast.LENGTH_SHORT).show();
            fAuth.signOut();
            un_remember_save();
            startActivity(new Intent(getApplicationContext(), SplashScreen.class));
            finish();
        });
    }
    private void setNavigationView(){
        //NAVIGATION//
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setVisibility(View.VISIBLE);
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
                        requestNotificationPermissions();
                        Notify();
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
            else{
                logout();
            }
        });
    }
    @SuppressLint("ObsoleteSdkInt")
    private void requestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (!notificationManager.areNotificationsEnabled()) {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivityForResult(intent, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            Notify();
        }
    }
    private void Notify() {
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null && notificationManager.getActiveNotifications().length == 0) {
            Intent intent = new Intent(this, dashboard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            DocumentReference docRef = fStore.collection("delivery_info").document(uiD);
            docRef.addSnapshotListener(this, (documentSnapshot, error) -> {
                if (documentSnapshot != null) {
                    String status = documentSnapshot.getString("delivery_status");
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(dashboard.this, "test");
                    NotificationChannel channel = new NotificationChannel("test", "EGC-GHE Delivery Service App", NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                    builder.setContentIntent(notifyPendingIntent)
                            .setSmallIcon(R.drawable.luggage)
                            .setContentTitle("EGC-GHE Delivery Service App");
                    if (status != null && status.equals("Processing ")) {
                        builder.setContentText("Your inquiry is in for processing.")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("We are currently processing your inquiry"))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        notificationManager.notify(1, builder.build());
                    } else if (status != null && status.equals("Out for Delivery")) {
                        String e_name = documentSnapshot.getString("endorser_name");
                        builder.setContentText("Your luggage is out for delivery.")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Your luggage has been processed and located by " + e_name + "."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        notificationManager.notify(1, builder.build());
                    } else if (status != null && status.equals("Delivery in Progress")) {
                        String sub_name = documentSnapshot.getString("subcontractor_name");
                        builder.setContentText("Your luggage is currently in delivery.")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Your luggage has been picked up by " + sub_name + "."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        notificationManager.notify(1, builder.build());
                    } else if (status != null && status.equals("Luggage Delivered")) {
                        builder.setContentText("Your luggage has been delivered.")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("Your luggage has been delivered to your destined location."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        notificationManager.notify(1, builder.build());
                    } else if (status != null && status.equals("Attempt Failed")) {
                        builder.setContentText("Delivery attempt failed.")
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText("A delivery attempt for your luggage was made."))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        notificationManager.notify(1, builder.build());
                    }
                }
            });
        }
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
                                setHomeFragment(new fragment_home());
                                break;
                            case "Account Management":
                                setAccountManagementFragment(new account_management());
                                break;
                            case "Luggage Monitoring":
                                setLuggageMonitoring(new fragment_luggage_monitoring());
                                break;
                            case "Profile":
                                setProfileFragment(new fragment_profile());
                                break;
                            case "Account Settings":
                                setAccountSettingsFragment(new fragment_account_settings());
                                break;
                            case "Logout":
                                logout();
                                break;
                        }
                    break;
                    case "employee":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                setHomeFragment(new fragment_home());
                                break;
                            case "Delivery Inquiries":
                                setDeliveryInquiries(new delivery_inquiries());
                                break;
                            case "Luggage Monitoring":
                                setLuggageMonitoring(new fragment_luggage_monitoring());
                                break;
                            case "Profile":
                                setProfileFragment(new fragment_profile());
                                break;
                            case "Account Settings":
                                setAccountSettingsFragment(new fragment_account_settings());
                                break;
                            case "Logout":
                                logout();
                                break;
                        }
                    break;
                    case "customer":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                setHomeFragment(new fragment_home());
                                break;
                            case "Inquire Lost Luggage":
                                setInquireLostLuggage(new inquire_lost_luggage());
                                break;
                            case "Luggage Monitoring":
                                setLuggageMonitoring(new fragment_luggage_monitoring());
                                break;
                            case "Profile":
                                setProfileFragment(new fragment_profile());
                                break;
                            case "Account Settings":
                                setAccountSettingsFragment(new fragment_account_settings());
                                break;
                            case "Logout":
                                logout();
                                break;
                            case "Contact Us":
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/richjames.lozano.3")));
                                break;
                        }
                    break;
                    case "subcontractor":
                        switch (Objects.requireNonNull(item.getTitle()).toString()){
                            case "Home":
                                setHomeFragment(new fragment_home());
                                break;
                            case "Available Deliveries":
                                setAvailableDeliveries(new available_deliveries());
                                break;
                            case "Luggage Monitoring":
                                setLuggageMonitoring(new fragment_luggage_monitoring());
                                break;
                            case "Profile":
                                setProfileFragment(new fragment_profile());
                                break;
                            case "Account Settings":
                                setAccountSettingsFragment(new fragment_account_settings());
                                break;
                            case "Logout":
                                logout();
                                break;
                            case "Contact Us":
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/curt.a.wong")));
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
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this);
        passwordResetDialog
                .setTitle("Are you sure you want to logout?")
                .setMessage("You are logging out of your account.")
                .setNegativeButton("No",(dialog, which) -> {})
                .setPositiveButton("Yes",(dialog, which) ->{
                    fAuth.signOut();
                    un_remember_save();
                    startActivity(new Intent(this, SplashScreen.class));
                    finish();
                })
                .show();
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
    private void setHomeFragment(fragment_home a){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dashboard_layout, a);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void setAccountManagementFragment(account_management a) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,a);
        fragmentTransaction.commit();
    }
    private void setInquireLostLuggage(inquire_lost_luggage a){
        DocumentReference docRef = fStore.collection("delivery_info").document(uiD);
        docRef.addSnapshotListener(this, (documentSnapshot, error) -> {
            if(documentSnapshot!=null){
                String status = documentSnapshot.getString("delivery_status");
                if(status==null){
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.dashboard_layout, a);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                else {
                    if(status.equals("Processing")){
                        AlertDialog.Builder cancelDialog = new AlertDialog.Builder(this);
                        cancelDialog
                        .setTitle("You already have a transaction")
                        .setMessage("Do you want to cancel your transaction.")
                        .setNegativeButton("No",(dialog, which) ->{
                            dialog.dismiss();
                            setLuggageMonitoring(new fragment_luggage_monitoring());
                        })
                        .setPositiveButton("Yes",(dialog, which) ->{
                        docRef.delete();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.dashboard_layout, a);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        dialog.dismiss();
                        })
                        .show();
                    }
                    else if(status.equals("Out for Delivery")||status.equals("Delivery in Progress") ||status.equals("Attempt Failed")){
                        Toast.makeText(this, "You cannot cancel this transaction. Please wait until it is finished.", Toast.LENGTH_LONG).show();
                    }
                    else{
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.dashboard_layout, a);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            }
        });
    }
    private void setDeliveryInquiries(delivery_inquiries a){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dashboard_layout, a);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void setLuggageMonitoring(fragment_luggage_monitoring a){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dashboard_layout, a);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void setAvailableDeliveries(available_deliveries a){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.dashboard_layout, a);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void setProfileFragment(fragment_profile a) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,a);
        fragmentTransaction.commit();
    }
    private void setAccountSettingsFragment(fragment_account_settings a) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.dashboard_layout,a);
        fragmentTransaction.commit();
    }
}