package com.apps2you.albaraka.ui.locations_v2;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

import com.apps2you.albaraka.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class LocationsV2Activity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView recyclerView;
    private LocationAdapter locationAdapter;
    private List<LocationItem> locationList = new ArrayList<>();
    private Button btnBranch, btnATM, btnPOS, btnMerchant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_v2);

        // Initialize the UI components
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationAdapter = new LocationAdapter(locationList);
        recyclerView.setAdapter(locationAdapter);

        btnBranch = findViewById(R.id.btn_branch);
        btnATM = findViewById(R.id.btn_atm);
        btnPOS = findViewById(R.id.btn_pos);
        btnMerchant = findViewById(R.id.btn_merchant);

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check if the location permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
            return;
        }

        // Get the last known location and fetch nearby branches/ATMs/POS/Merchants
        getCurrentLocation();

        // Set button click listeners to update RecyclerView content
        btnBranch.setOnClickListener(v -> loadLocations("Branch"));
        btnATM.setOnClickListener(v -> loadLocations("ATM"));
        btnPOS.setOnClickListener(v -> loadLocations("POS"));
        btnMerchant.setOnClickListener(v -> loadLocations("Merchant"));
    }

    // Function to get the current location
    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            // Use the location to fetch nearby branches/ATMs/etc.
                            loadLocations("Branch"); // Load branches by default
                        }
                    }
                });
    }

    // Function to load locations based on the selected type (Branch, ATM, etc.)
    private void loadLocations(String locationType) {
        locationList.clear();
        // Mock data - replace with actual API call to fetch locations based on locationType
        locationList.add(new LocationItem(locationType + " 1", "Address 1", 1.2));
        locationList.add(new LocationItem(locationType + " 2", "Address 2", 2.4));
        locationList.add(new LocationItem(locationType + " 3", "Address 3", 3.1));
        locationAdapter.notifyDataSetChanged();
    }

    // Handle location permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }
}
