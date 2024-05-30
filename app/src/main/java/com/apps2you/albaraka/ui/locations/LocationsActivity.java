package com.apps2you.albaraka.ui.locations;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Branch;
import com.apps2you.albaraka.databinding.ActivityLocationsBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.LocationsVM;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LocationsActivity extends BaseActivity<ActivityLocationsBinding, LocationsVM>
        implements OnMapReadyCallback {

    private final int REQUEST_GPS = 122,
            RC_PERM_LOCATION = 123, // request code for location permission used in getMyLocation
            RP_LOCATION_ENABLE = 1234; // request code for location permission used in enableMyLocation

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_locations;
    }

    @Override
    public Class<LocationsVM> setViewModel() {
        return LocationsVM.class;
    }

    @Override
    public void setUpView() {
        setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.guest_locations));

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(20 * 1000);
        // By default locations are continuously updated until the request is explicitly removed,
        // however you can optionally request a set number of updates.
        locationRequest.setNumUpdates(1);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NotNull LocationResult locationResult) {
                if (locationResult != null)
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(me, 17));
                        }
                    }
            }
        };


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        hideBottomSheet();

        getViewDataBinding().btnBranch.setOnClickListener(v -> {
//            getData(Constants.TYPE_BRANCH);
            setMarkers(Constants.TYPE_BRANCH);

            getViewDataBinding().btnBranch.setBackgroundResource(R.drawable.bg_shadow_primary);
            getViewDataBinding().btnBranch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_branch_white, 0, 0, 0);
            getViewDataBinding().btnBranch.setTextColor(getResources().getColor(R.color.white));

            getViewDataBinding().btnAtm.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnAtm.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_atm, 0, 0, 0);
            getViewDataBinding().btnAtm.setTextColor(getResources().getColor(R.color.orange));

            getViewDataBinding().btnPos.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnPos.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pos, 0, 0, 0);
            getViewDataBinding().btnPos.setTextColor(getResources().getColor(R.color.gray));

            getViewDataBinding().btnMerchant.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnMerchant.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pos, 0, 0, 0);
            getViewDataBinding().btnMerchant.setTextColor(getResources().getColor(R.color.blue));
        });

        getViewDataBinding().btnAtm.setOnClickListener(v -> {
//            getData(Constants.TYPE_ATM);
            setMarkers(Constants.TYPE_ATM);

            getViewDataBinding().btnAtm.setBackgroundResource(R.drawable.bg_shadow_orange);
            getViewDataBinding().btnAtm.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_atm_white, 0, 0, 0);
            getViewDataBinding().btnAtm.setTextColor(getResources().getColor(R.color.white));

            getViewDataBinding().btnBranch.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnBranch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_branch, 0, 0, 0);
            getViewDataBinding().btnBranch.setTextColor(getResources().getColor(R.color.red));

            getViewDataBinding().btnPos.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnPos.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pos, 0, 0, 0);
            getViewDataBinding().btnPos.setTextColor(getResources().getColor(R.color.gray));

            getViewDataBinding().btnMerchant.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnMerchant.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pos, 0, 0, 0);
            getViewDataBinding().btnMerchant.setTextColor(getResources().getColor(R.color.blue));
        });

        getViewDataBinding().btnPos.setOnClickListener(v -> {
//            getData(Constants.TYPE_POS);
            setMarkers(Constants.TYPE_POS);

            getViewDataBinding().btnPos.setBackgroundResource(R.drawable.bg_shadow_gray);
            getViewDataBinding().btnPos.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pos_white, 0, 0, 0);
            getViewDataBinding().btnPos.setTextColor(getResources().getColor(R.color.white));

            getViewDataBinding().btnBranch.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnBranch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_branch, 0, 0, 0);
            getViewDataBinding().btnBranch.setTextColor(getResources().getColor(R.color.red));

            getViewDataBinding().btnAtm.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnAtm.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_atm, 0, 0, 0);
            getViewDataBinding().btnAtm.setTextColor(getResources().getColor(R.color.orange));

            getViewDataBinding().btnMerchant.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnMerchant.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pos, 0, 0, 0);
            getViewDataBinding().btnMerchant.setTextColor(getResources().getColor(R.color.blue));
        });

        getViewDataBinding().btnMerchant.setOnClickListener(v -> {
//            getData(Constants.TYPE_POS);
            setMarkers(Constants.TYPE_MERCHANT);

            getViewDataBinding().btnPos.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnPos.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pos, 0, 0, 0);
            getViewDataBinding().btnPos.setTextColor(getResources().getColor(R.color.gray));

            getViewDataBinding().btnBranch.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnBranch.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_branch, 0, 0, 0);
            getViewDataBinding().btnBranch.setTextColor(getResources().getColor(R.color.red));

            getViewDataBinding().btnAtm.setBackgroundResource(R.drawable.bg_shadow_white);
            getViewDataBinding().btnAtm.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_atm, 0, 0, 0);
            getViewDataBinding().btnAtm.setTextColor(getResources().getColor(R.color.orange));

            getViewDataBinding().btnMerchant.setBackgroundResource(R.drawable.bg_shadow_blue);
            getViewDataBinding().btnMerchant.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_pos_white, 0, 0, 0);
            getViewDataBinding().btnMerchant.setTextColor(getResources().getColor(R.color.white));
        });
    }

    @Override
    public void onMapReady(@NotNull final GoogleMap googleMap) {

        mMap = googleMap;

        enableMyLocation();

        getData(Constants.TYPE_BRANCH);

        googleMap.setOnMarkerClickListener(marker -> {

            final Branch branch = getViewModel().getLocation(Integer.parseInt(marker.getTag().toString()));
            if (branch != null && (!branch.isMerchant() || (branch.isMerchant() && branch.isAvaliable()))) {
                BottomSheetBehavior<LinearLayout> sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                getViewDataBinding().bottomSheet.setItem(branch);
                getViewDataBinding().bottomSheet.executePendingBindings();

                getViewDataBinding().bottomSheet.iBtnPhone.setOnClickListener(v -> {
                    if (branch.isBranch() || branch.isMerchant()) { // not an ATM machine/POS
                        MyApplication.skipQuit = true;
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        String phone = branch.getPhoneNumber();
                        intent.setData(Uri.parse("tel:" + phone));

                        if (intent.resolveActivity(getPackageManager()) != null)
                            startActivity(intent);
                    }
                });

                getViewDataBinding().bottomSheet.iBtnDirection.setOnClickListener(v -> {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="
                            .concat(Double.toString(branch.getLatitude()))
                            .concat(",")
                            .concat(Double.toString(branch.getLongitude())));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(getPackageManager()) != null)
                        startActivity(mapIntent);
                });

                moveToLocation(branch);
            }
            else{
                hideBottomSheet();
            }
            return false;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GPS)
            if (resultCode == Activity.RESULT_OK) // Gps turned on
                getMyLocation();
        // if (resultCode == Activity.RESULT_CANCELED) // Gps request denied
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            if (requestCode == RP_LOCATION_ENABLE)
                enableMyLocation();
            else if (requestCode == RC_PERM_LOCATION)
                getMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fetchData() {
    }

    @Override
    public void listenToVariables() {
    }

    private void getData(String type) {
        getViewModel().getLocations(type)
                .observe(this, arrayListResource -> {
                    switch (arrayListResource.status) {
                        case LOADING:
                            mMap.clear(); // clear previous markers
                            getViewModel().clearLocationsLists(); // clear previous data

                            hideBottomSheet();
                            getViewDataBinding().progressBar.setVisibility(View.VISIBLE);
                            break;

                        case ERROR:
                            getViewDataBinding().progressBar.setVisibility(View.GONE);
                            showToast(arrayListResource.message);
                            break;

                        case SUCCESS:
                            getViewDataBinding().progressBar.setVisibility(View.GONE);
                            getViewModel().addAllLocations(arrayListResource.data);
                            setMarkers(type);
                            break;
                    }
                });
    }

    private void setMarkers(String type) {
        mMap.clear(); // clear previous markers
        hideBottomSheet();
        getViewModel().setSelectedType(type);

        if (getViewModel().getBranches().isEmpty()
                && getViewModel().getAtms().isEmpty()
                && getViewModel().getPos().isEmpty()) {//request the API if there is no data (refresh replacement)
            getData(type);
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        ArrayList<Branch> data = new ArrayList<>();
        switch (type) {
            case Constants.TYPE_BRANCH:
                data.addAll(getViewModel().getBranches());
                break;

            case Constants.TYPE_ATM:
                data.addAll(getViewModel().getAtms());
                break;

            case Constants.TYPE_POS:
                data.addAll(getViewModel().getPos());
                break;
            case Constants.TYPE_MERCHANT:
                data.addAll(getViewModel().getMerchant());
                break;
        }

        for (int i = 0; i < data.size(); i++) {
            Branch branch = data.get(i);
            LatLng pos = new LatLng(branch.getLatitude(), branch.getLongitude());

            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .title(branch.getName()));
            marker.setTag(i);

            if (branch.isBranch())
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_branch));
            else if (branch.isAtm())
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_atm));
            else if (branch.isPos())
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_pos));
            else if (branch.isMerchant())
            {
                if(branch.isAvaliable())
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_merchant_green));
                else {
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_merchant_red));
                }

            }


            builder.include(pos);
        }

        // zoom camera to the center of all the locations
        if(type== Constants.TYPE_MERCHANT)
        {
            getMyLocation();
        }
        else if (data.size() > 0) {
            LatLngBounds bounds = builder.build();

            // set the size of map
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
        }
    }

    private void moveToLocation(Branch branch) {
        LatLng pos = new LatLng(branch.getLatitude(), branch.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(pos, 17);

        if (mMap != null)
            mMap.animateCamera(cameraUpdate);
    }

    private void hideBottomSheet() {
        BottomSheetBehavior<LinearLayout> sheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void enableMyLocation() {
        // ask for location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, RP_LOCATION_ENABLE);
                return;
            }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMyLocationButtonClickListener(() -> {
            getMyLocation();
            return false;
        });
    }

    private void getMyLocation() {

        // ask for location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, RC_PERM_LOCATION);
                return;
            }

        // this LocationSettingsRequest to turn Gps on
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        settingsBuilder.setAlwaysShow(true);

        // check whether the location settings are satisfied
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this)
                .checkLocationSettings(settingsBuilder.build());

        result.addOnSuccessListener(locationSettingsResponse -> {
            // All location settings are satisfied. The client can initialize
            // location requests here.
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        });

        result.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Cast to a resolvable exception.
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    resolvable.startResolutionForResult(
                            LocationsActivity.this,
                            REQUEST_GPS);
                } catch (IntentSender.SendIntentException sendEx) {
                    // Ignore the error.
                }
            }
        });
    }
}
