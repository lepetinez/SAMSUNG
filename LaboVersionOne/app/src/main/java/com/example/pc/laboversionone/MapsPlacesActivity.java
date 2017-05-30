package com.example.pc.laboversionone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MapsPlacesActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    double latitude;
    double longitude;
    private List<String> placesDetailsList = new ArrayList<>();
    private int PROXIMITY_RADIUS = 20000;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_maps);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(getDrawable(R.mipmap.add_icon));

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView itemIcon = new ImageView(this);
        ImageView itemIcon2 = new ImageView(this);
        ImageView itemIcon3 = new ImageView(this);
        ImageView itemIcon4 = new ImageView(this);
        ImageView itemIcon5 = new ImageView(this);

        itemIcon2.setImageDrawable(getDrawable(R.mipmap.pub_icon));
        itemIcon.setImageDrawable(getDrawable(R.mipmap.restaurant_icon) );
        itemIcon3.setImageDrawable(getDrawable(R.mipmap.disco_icon) );
        itemIcon4.setImageDrawable(getDrawable(R.mipmap.fun_icon));
        itemIcon5.setImageDrawable(getDrawable(R.mipmap.culture_icon));

        SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
        SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();
        SubActionButton button3 = itemBuilder.setContentView(itemIcon3).build();
        SubActionButton button4 = itemBuilder.setContentView(itemIcon4).build();
        SubActionButton button5 = itemBuilder.setContentView(itemIcon5).build();
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1,150,150)
                .addSubActionView(button2,150,150)
                .addSubActionView(button3,150,150)
                .addSubActionView(button4,150,150)
                .addSubActionView(button5,150,150)
                .setStartAngle(285)
                .setEndAngle(165)
                .attachTo(actionButton)
                .build();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    String Restaurant = "restauracja";
                    Log.d("onClick", "Button is Clicked");
                    mMap.clear();
                    placesDetailsList.clear();
                    String url = getUrl(latitude, longitude, Restaurant);
                    Object[] DataTransfer = new Object[3];
                    DataTransfer[0] = mMap;
                    DataTransfer[1] = url;
                    DataTransfer[2] = placesDetailsList;
                    Log.d("onClick", url);
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                    getNearbyPlacesData.execute(DataTransfer);
                    Toast.makeText(MapsPlacesActivity.this, "Pobliskie restauracje", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MapsPlacesActivity.this, "Wymagane  jest połączenie z internetem", Toast.LENGTH_LONG).show();
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    String Restaurant = "pub";
                    Log.d("onClick", "Button is Clicked");
                    mMap.clear();
                    placesDetailsList.clear();
                    String url = getUrl(latitude, longitude, Restaurant);
                    Object[] DataTransfer = new Object[3];
                    DataTransfer[0] = mMap;
                    DataTransfer[1] = url;
                    DataTransfer[2] = placesDetailsList;
                    Log.d("onClick", url);
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                    getNearbyPlacesData.execute(DataTransfer);
                    Toast.makeText(MapsPlacesActivity.this, "Pobliskie puby", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MapsPlacesActivity.this, "Wymagane  jest połączenie z internetem", Toast.LENGTH_LONG).show();
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    String Restaurant = "klub";
                    Log.d("onClick", "Button is Clicked");
                    mMap.clear();
                    placesDetailsList.clear();
                    String url = getUrl(latitude, longitude, Restaurant);
                    Object[] DataTransfer = new Object[3];
                    DataTransfer[0] = mMap;
                    DataTransfer[1] = url;
                    DataTransfer[2] = placesDetailsList;
                    Log.d("onClick", url);
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                    getNearbyPlacesData.execute(DataTransfer);
                    Toast.makeText(MapsPlacesActivity.this, "Pobliskie kluby", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MapsPlacesActivity.this, "Wymagane  jest połączenie z internetem", Toast.LENGTH_LONG).show();
                }
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    String Restaurant = "atrakcje";
                    Log.d("onClick", "Button is Clicked");
                    mMap.clear();
                    placesDetailsList.clear();
                    String url = getUrl(latitude, longitude, Restaurant);
                    Object[] DataTransfer = new Object[3];
                    DataTransfer[0] = mMap;
                    DataTransfer[1] = url;
                    DataTransfer[2] = placesDetailsList;
                    Log.d("onClick", url);
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                    getNearbyPlacesData.execute(DataTransfer);
                    Toast.makeText(MapsPlacesActivity.this, "Pobliskie atrakcje", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MapsPlacesActivity.this, "Wymagane  jest połączenie z internetem", Toast.LENGTH_LONG).show();
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {
                    String Restaurant = "kultura";
                    Log.d("onClick", "Button is Clicked");
                    mMap.clear();
                    placesDetailsList.clear();
                    String url = getUrl(latitude, longitude, Restaurant);
                    Object[] DataTransfer = new Object[3];
                    DataTransfer[0] = mMap;
                    DataTransfer[1] = url;
                    DataTransfer[2] = placesDetailsList;
                    Log.d("onClick", url);
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
                    getNearbyPlacesData.execute(DataTransfer);
                    Toast.makeText(MapsPlacesActivity.this, "Pobliskie muzea, wystawy", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(MapsPlacesActivity.this, "Wymagane  jest połączenie z internetem", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public void setWindowAdapteronMarkers() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                    View view = getLayoutInflater().inflate(R.layout.places_details, null);

                    TextView nazwa = (TextView) view.findViewById(R.id.nazwa_miejsca);
                    TextView adres= (TextView) view.findViewById(R.id.adres);
                    TextView czynne = (TextView) view.findViewById(R.id.czynne);
                    TextView ocena = (TextView) view.findViewById(R.id.ocena);

                    nazwa.setText(placesDetailsList.get((Integer.valueOf(marker.getSnippet())) * 4));
                    adres.setText(placesDetailsList.get((Integer.valueOf(marker.getSnippet())) * 4 + 1));
                    czynne.setText(placesDetailsList.get((Integer.valueOf(marker.getSnippet())) * 4 + 2));
                    ocena.setText(placesDetailsList.get((Integer.valueOf(marker.getSnippet())) * 4 + 3));

                    view.setMinimumWidth(765);
                    view.setMinimumHeight(320);
                    return view;
                }

        });
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        setWindowAdapteronMarkers();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if(hour > 22 || hour < 6){
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.night_style_json));
        }
        else {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.day_style_json));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.424212, 14.571040), 11.0f)); // Szczecin -> LatLng(53.424212, 14.571040)
        mMap.getUiSettings().setMapToolbarEnabled(false);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&keyword=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Toast.makeText(MapsPlacesActivity.this,"Twoje aktualne polozenie :", Toast.LENGTH_LONG).show();
        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f",latitude,longitude));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}