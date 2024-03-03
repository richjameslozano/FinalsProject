package com.example.finalsproject.dashboard_user_activities;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalsproject.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class history_class extends AppCompatActivity {

    ListView lv_savedLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        lv_savedLocations = findViewById(R.id.lv_savedlocations);
        Geocoder geocoder = new Geocoder(getApplicationContext());
        // Create a list to store readable addresses
        List<String> readableAddresses = new ArrayList<>();
        List<Location> savedLocations = history_list_class.getMyLocations();
        for (Location location : savedLocations) {
            try {
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String addressLine = addresses.get(0).getAddressLine(0);
                    readableAddresses.add(addressLine);
                }
                else {
                    readableAddresses.add("No address found for this location");
                }
            }
            catch (IOException e) {
                readableAddresses.add("Unable to get street address");
                e.printStackTrace();
            }
        }
        lv_savedLocations.setAdapter(new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, readableAddresses));
    }
}