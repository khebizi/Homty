package com.sizii.homty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sizii.homty.Model.MyPlaces;
import com.sizii.homty.Model.Results;
import com.sizii.homty.Remote.IGoogleApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int MY_PERMISSION_CODE = 1000;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;

    private double latitude, longitude;
    private Location lastLocation;
    private Marker marker;
    private LocationRequest locationRequest;

    IGoogleApiService googleApiService;

    MyPlaces currentPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiService = Common.getGoogleApiService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_barber:
                        nearByPlace("hair_care");
                        break;
                    case R.id.action_gym:
                        nearByPlace("gym");
                        break;
                    case R.id.action_restaurant:
                        nearByPlace("restaurant");
                        break;
                    case R.id.action_hospital:
                        nearByPlace("hospital");
                        break;
                    case R.id.action_shopping:
                        nearByPlace("shopping_mall");
                        break;
                }
                return true;
            }
        });
    }

    private void nearByPlace(final String placeType) {
        map.clear();
        String url = getUrl(latitude, longitude, placeType);

        googleApiService.getNearbyPlaces(url).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                currentPlace = response.body();
                if (response.isSuccessful()) {
                    for (int i=0; i < response.body().getResults().length; i++) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        Results googlePlace = response.body().getResults()[i];
                        double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                        double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                        String placeName = googlePlace.getName();
                        String vicinity = googlePlace.getVicinity();
                        LatLng latLng = new LatLng(lat, lng);
                        markerOptions.position(latLng);
                        markerOptions.title(placeName);
                        if (placeType.equals("hospital"))
                            markerOptions.icon(bitmapDescriptorFromVector(getApplication(), R.drawable.hospital_marker));
                        else if (placeType.equals("hair_care"))
                            markerOptions.icon(bitmapDescriptorFromVector(getApplication(), R.drawable.hair_care_marker));
                        else if (placeType.equals("gym"))
                            markerOptions.icon(bitmapDescriptorFromVector(getApplication(), R.drawable.gym_marker));
                        else if (placeType.equals("shopping_mall"))
                            markerOptions.icon(bitmapDescriptorFromVector(getApplication(), R.drawable.shopping_mall_marker));
                        else if (placeType.equals("restaurant"))
                            markerOptions.icon(bitmapDescriptorFromVector(getApplication(), R.drawable.restaurant_marker));
                        else
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        markerOptions.snippet(String.valueOf(i));
                        map.addMarker(markerOptions);
                        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        map.animateCamera(CameraUpdateFactory.zoomTo(11));
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {

            }
        });
    }

    private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?");
        googlePlacesUrl.append("type=" + placeType);
        googlePlacesUrl.append("&location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 50000);
        googlePlacesUrl.append("&key=" + getResources().getString(R.string.places_key));
        Log.d("getUrl", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient != null) {
                            buildGoogleApiClients();
                        map.setMyLocationEnabled(true);
                        }
                    } else {
                        Toast.makeText(this, "Accès refusé.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClients();
                map.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClients();
            map.setMyLocationEnabled(true);
        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Common.currentResult = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];
                startActivity(new Intent(MapsActivity.this, ViewPlaceActivity.class));
                return true;
            }
        });
    }

    private synchronized void buildGoogleApiClients() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (marker != null) {
            marker.remove();
        }
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(getString(R.string.current_location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        marker = map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
