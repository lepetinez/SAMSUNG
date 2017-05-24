package com.example.pc.laboversionone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String lineRoute = "http://www.zditm.szczecin.pl/json/trasy.inc.php?gmvid=";
    private final static String lineStopsUrl = "http://www.zditm.szczecin.pl/json/slupki.inc.php?linia=";
    private final static String allStopsUrl = "http://www.zditm.szczecin.pl/json/slupki.inc.php";
    private final static String url = "http://www.zditm.szczecin.pl/json/pojazdy.inc.php";
    public TextView[] detailsText = new TextView[17];
    View v;
    String singleBusData[] = new String[17];
    String stopInfo;
    EditText editText;
    Timer linesTimer = new Timer();
    Timer markerTimer = new Timer();
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    double latitude;
    double longitude;
    NavigationView navigationView;
    Toolbar mToolbar;
    private Marker destinationMarker;
    private boolean firstBusesStart = true;
    private String line;
    private LinearLayout searchBar;
    private List<Marker> nearBusesMarkers = new ArrayList<>();
    private List<String> busesDetailsList = new ArrayList<>();
    private List<Marker> busesMarkers = new ArrayList<>();
    private List<Marker> stopsMarkers = new ArrayList<>();
    private List<Polyline> lineDrawing = new ArrayList<>();
    private GoogleMap mMap;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        MultiDex.install(this);
        searchBar = (LinearLayout) findViewById(R.id.place_search_bar);
        searchBar.setVisibility(View.INVISIBLE);
        FloatingActionButton searchPlaces = (FloatingActionButton) findViewById(R.id.search_place);
        FloatingActionButton zoomIn = (FloatingActionButton) findViewById(R.id.zoom_in);
        FloatingActionButton zoomOut = (FloatingActionButton) findViewById(R.id.zoom_out);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomBy(2));

            }
        });
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.animateCamera(CameraUpdateFactory.zoomBy(-2));


            }
        });
        searchPlaces.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchBar.setVisibility(View.VISIBLE);
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        editText = (EditText) findViewById(R.id.editText);
        initBusDetailsView();
        navigationView = (NavigationView) findViewById(R.id.my_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_opened, R.string.drawer_closed) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void initBusDetailsView() {
        v = getLayoutInflater().inflate(R.layout.bus_stop_info_layout, null);
        detailsText[0] = (TextView) v.findViewById(R.id.textView1);
        detailsText[1] = (TextView) v.findViewById(R.id.textView2);
        detailsText[2] = (TextView) v.findViewById(R.id.linia1);
        detailsText[3] = (TextView) v.findViewById(R.id.linia2);
        detailsText[4] = (TextView) v.findViewById(R.id.linia3);
        detailsText[5] = (TextView) v.findViewById(R.id.linia4);
        detailsText[6] = (TextView) v.findViewById(R.id.linia5);
        detailsText[7] = (TextView) v.findViewById(R.id.kierunek1);
        detailsText[8] = (TextView) v.findViewById(R.id.kierunek2);
        detailsText[9] = (TextView) v.findViewById(R.id.kierunek3);
        detailsText[10] = (TextView) v.findViewById(R.id.kierunek4);
        detailsText[11] = (TextView) v.findViewById(R.id.kierunek5);

        detailsText[12] = (TextView) v.findViewById(R.id.godzina1);
        detailsText[13] = (TextView) v.findViewById(R.id.godzina2);
        detailsText[14] = (TextView) v.findViewById(R.id.godzina3);
        detailsText[15] = (TextView) v.findViewById(R.id.godzina4);
        detailsText[16] = (TextView) v.findViewById(R.id.godzina5);
    }

    public void setWindowAdapteronMarkers() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {

                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (marker.getTitle() != null) {
                    stopInfo = "http://www.zditm.szczecin.pl/json/slupekkursy.inc.php?slupek=" + marker.getSnippet();
                    try {
                        new LoadBusStopsDetails().execute(singleBusData, stopInfo, detailsText).get(2500, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                    v.setMinimumWidth(600);
                    v.setMinimumHeight(500);

                    detailsText[0].setText(singleBusData[0]);
                    detailsText[1].setText(singleBusData[1]);

                    detailsText[2].setText(singleBusData[2]);
                    detailsText[3].setText(singleBusData[3]);
                    detailsText[4].setText(singleBusData[4]);
                    detailsText[5].setText(singleBusData[5]);
                    detailsText[6].setText(singleBusData[6]);

                    detailsText[7].setText(singleBusData[7]);
                    detailsText[8].setText(singleBusData[9]);
                    detailsText[9].setText(singleBusData[11]);
                    detailsText[10].setText(singleBusData[13]);
                    detailsText[11].setText(singleBusData[15]);

                    detailsText[12].setText(singleBusData[8]);
                    detailsText[13].setText(singleBusData[10]);
                    detailsText[14].setText(singleBusData[12]);
                    detailsText[15].setText(singleBusData[14]);
                    detailsText[16].setText(singleBusData[16]);
                    return v;
                } else {

                    View view = getLayoutInflater().inflate(R.layout.bus_or_tram_details, null);

                    TextView line = (TextView) view.findViewById(R.id.line);
                    TextView from = (TextView) view.findViewById(R.id.from);
                    TextView to = (TextView) view.findViewById(R.id.to);
                    TextView late = (TextView) view.findViewById(R.id.late);

                    line.setText(busesDetailsList.get((Integer.valueOf(marker.getSnippet())) * 4));
                    from.setText(busesDetailsList.get((Integer.valueOf(marker.getSnippet())) * 4 + 1));
                    to.setText(busesDetailsList.get((Integer.valueOf(marker.getSnippet())) * 4 + 2));
                    late.setText(busesDetailsList.get((Integer.valueOf(marker.getSnippet())) * 4 + 3));

                    view.setMinimumWidth(765);
                    view.setMinimumHeight(320);
                    return view;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.autobusy:
                navigationView.getMenu().setGroupVisible(R.id.main_group, false);
                navigationView.getMenu().setGroupVisible(R.id.bus_category_group, true);
                navigationView.getMenu().setGroupVisible(R.id.dopodzialu, false);
                return true;
            case R.id.tramwaje:
                navigationView.getMenu().setGroupVisible(R.id.tram_group, true);
                navigationView.getMenu().setGroupVisible(R.id.main_group, false);
                navigationView.getMenu().setGroupVisible(R.id.dopodzialu, false);
                return true;
            case R.id.tramwaje1:
                showLine("1", "1");
                showBusesRotation();
                return true;
            case R.id.tramwaje2:
                showLine("2", "2");
                showBusesRotation();
                return true;
            case R.id.tramwaje3:
                showLine("3", "3");
                showBusesRotation();
                return true;
            case R.id.tramwaje4:
                showLine("4", "4");
                showBusesRotation();
                return true;
            case R.id.tramwaje5:
                showLine("5", "5");
                showBusesRotation();
                return true;
            case R.id.tramwaje6:
                showLine("6", "6");
                showBusesRotation();
                return true;
            case R.id.tramwaje7:
                showLine("7", "7");
                showBusesRotation();
                return true;
            case R.id.tramwaje8:
                showLine("8", "8");
                showBusesRotation();
                return true;
            case R.id.tramwaje9:
                showLine("9", "9");
                showBusesRotation();
                return true;
            case R.id.tramwaje10:
                showLine("10", "84");
                showBusesRotation();
                return true;
            case R.id.tramwaje11:
                showLine("11", "10");
                showBusesRotation();
                return true;
            case R.id.tramwaje12:
                showLine("12", "11");
                showBusesRotation();
                return true;
            case R.id.autobusy1:
                showLine("51", "12");
                showBusesRotation();
                return true;
            case R.id.autobusy2:
                showLine("52", "13");
                showBusesRotation();
                return true;
            case R.id.autobusy3:
                showLine("53", "14");
                showBusesRotation();
                return true;
            case R.id.autobusy4:
                showLine("54", "15");
                showBusesRotation();
                return true;
            case R.id.autobusy5:
                showLine("55", "16");
                showBusesRotation();
                return true;
            case R.id.autobusy6:
                showLine("56", "16");
                showBusesRotation();
                return true;
            case R.id.autobusy7:
                showLine("57", "19");
                showBusesRotation();
                return true;
            case R.id.autobusy8:
                showLine("58", "20");
                showBusesRotation();
                return true;
            case R.id.autobusy9:
                showLine("59", "21");
                showBusesRotation();
                return true;
            case R.id.autobusy10:
                showLine("60", "22");
                showBusesRotation();
                return true;
            case R.id.autobusy11:
                showLine("61", "23");
                showBusesRotation();
                return true;
            case R.id.autobusy12:
                showLine("62", "24");
                showBusesRotation();
                return true;
            case R.id.autobusy13:
                showLine("63", "25");
                showBusesRotation();
                return true;
            case R.id.autobusy14:
                showLine("64", "26");
                showBusesRotation();
                return true;
            case R.id.autobusy15:
                showLine("65", "27");
                showBusesRotation();
                return true;
            case R.id.autobusy16:
                showLine("66", "28");
                showBusesRotation();
                return true;
            case R.id.autobusy17:
                showLine("67", "29");
                showBusesRotation();
                return true;
            case R.id.autobusy18:
                showLine("68", "30");
                showBusesRotation();
                return true;
            case R.id.autobusy19:
                showLine("69", "31");
                showBusesRotation();
                return true;
            case R.id.autobusy20:
                showLine("70", "32");
                showBusesRotation();
                return true;
            case R.id.autobusy21:
                showLine("71", "33");
                showBusesRotation();
                return true;
            case R.id.autobusy22:
                showLine("72", "94");
                showBusesRotation();
                return true;
            case R.id.autobusy23:
                showLine("73", "35");
                showBusesRotation();
                return true;
            case R.id.autobusy24:
                showLine("74", "36");
                showBusesRotation();
                return true;
            case R.id.autobusy25:
                showLine("75", "37");
                showBusesRotation();
                return true;
            case R.id.autobusy26:
                showLine("76", "38");
                showBusesRotation();
                return true;
            case R.id.autobusy27:
                showLine("77", "39");
                showBusesRotation();
                return true;
            case R.id.autobusy28:
                showLine("78", "40");
                showBusesRotation();
                return true;
            case R.id.autobusy29:
                showLine("79", "41");
                showBusesRotation();
                return true;
            case R.id.autobusy30:
                showLine("80", "42");
                showBusesRotation();
                return true;
            case R.id.autobusy31:
                showLine("81", "43");
                showBusesRotation();
                return true;
            case R.id.autobusy32:
                showLine("82", "44");
                showBusesRotation();
                return true;
            case R.id.autobusy33:
                showLine("83", "79");
                showBusesRotation();
                return true;
            case R.id.autobusy34:
                showLine("84", "45");
                showBusesRotation();
                return true;
            case R.id.autobusy35:
                showLine("85", "17");
                showBusesRotation();
                return true;
            case R.id.autobusy36:
                showLine("86", "85");
                showBusesRotation();
                return true;
            case R.id.autobusy37:
                showLine("87", "46");
                showBusesRotation();
                return true;
            case R.id.autobusy38:
                showLine("88", "86");
                showBusesRotation();
                return true;
            case R.id.autobusy39:
                showLine("93", "34");
                showBusesRotation();
                return true;
            case R.id.autobusy40:
                showLine("101", "47");
                showBusesRotation();
                return true;
            case R.id.autobusy41:
                showLine("102", "48");
                showBusesRotation();
                return true;
            case R.id.autobusy42:
                showLine("103", "49");
                showBusesRotation();
                return true;
            case R.id.autobusy43:
                showLine("105", "91");
                showBusesRotation();
                return true;
            case R.id.autobusy44:
                showLine("106", "50");
                showBusesRotation();
                return true;
            case R.id.autobusy45:
                showLine("107", "51");
                showBusesRotation();
                return true;
            case R.id.autobusy46:
                showLine("108", "92");
                showBusesRotation();
                return true;
            case R.id.autobusy47:
                showLine("109", "52");
                showBusesRotation();
                return true;
            case R.id.autobusy48:
                showLine("110", "53");
                showBusesRotation();
                return true;
            case R.id.autobusy49:
                showLine("111", "54");
                showBusesRotation();
                return true;
            case R.id.autobusy50:
                showLine("121", "90");
                showBusesRotation();
                return true;
            case R.id.autobusy51:
                showLine("122", "89");
                showBusesRotation();
                return true;
            case R.id.autobusy52:
                showLine("123", "87");
                showBusesRotation();
                return true;
            case R.id.autobusy53:
                showLine("124", "100");
                showBusesRotation();
                return true;
            case R.id.back_dzienne_autobusy:
                navigationView.getMenu().setGroupVisible(R.id.day_bus_group, false);
                navigationView.getMenu().setGroupVisible(R.id.bus_category_group, true);
                navigationView.getMenu().setGroupVisible(R.id.dopodzialu, true);
                return true;
            case R.id.autobusy_posp1:
                showLine("311", "55");
                showBusesRotation();
                return true;
            case R.id.autobusy_posp2:
                showLine("302", "56");
                showBusesRotation();
                return true;
            case R.id.autobusy_posp3:
                showLine("403", "57");
                showBusesRotation();
                return true;
            case R.id.autobusy_posp4:
                showLine("404", "58");
                showBusesRotation();
                return true;
            case R.id.autobusy_posp5:
                showLine("405", "59");
                showBusesRotation();
                return true;
            case R.id.autobusy_posp6:
                showLine("306", "60");
                showBusesRotation();
                return true;
            case R.id.autobusy_posp7:
                showLine("407", "61");
                showBusesRotation();
                return true;
            case R.id.back_autobusy_posp:
                navigationView.getMenu().setGroupVisible(R.id.pospieszne_bus_group, false);
                navigationView.getMenu().setGroupVisible(R.id.bus_category_group, true);
                navigationView.getMenu().setGroupVisible(R.id.dopodzialu, true);
                return true;
            case R.id.autobusy_nocne1:
                showLine("521", "62");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne2:
                showLine("522", "63");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne3:
                showLine("523", "64");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne4:
                showLine("524", "65");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne5:
                showLine("525", "66");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne6:
                showLine("526", "67");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne7:
                showLine("527", "68");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne8:
                showLine("528", "69");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne9:
                showLine("529", "70");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne10:
                showLine("530", "71");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne11:
                showLine("531", "72");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne12:
                showLine("532", "73");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne13:
                showLine("533", "74");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne14:
                showLine("534", "75");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne15:
                showLine("535", "93");
                showBusesRotation();
                return true;
            case R.id.autobusy_nocne16:
                showLine("536", "88");
                showBusesRotation();
                return true;
            case R.id.back_autobusy_nocne:
                navigationView.getMenu().setGroupVisible(R.id.nocne_bus_group, false);
                navigationView.getMenu().setGroupVisible(R.id.bus_category_group, true);
                return true;
            case R.id.cofnij_tramwaje:
                navigationView.getMenu().setGroupVisible(R.id.main_group, true);
                navigationView.getMenu().setGroupVisible(R.id.tram_group, false);
                navigationView.getMenu().setGroupVisible(R.id.dopodzialu, true);

                return true;
            case R.id.Nocne:
                navigationView.getMenu().setGroupVisible(R.id.bus_category_group, false);
                navigationView.getMenu().setGroupVisible(R.id.nocne_bus_group, true);
                return true;
            case R.id.Dzienne:
                navigationView.getMenu().setGroupVisible(R.id.bus_category_group, false);
                navigationView.getMenu().setGroupVisible(R.id.day_bus_group, true);
                return true;
            case R.id.Pośpieszne:
                navigationView.getMenu().setGroupVisible(R.id.bus_category_group, false);
                navigationView.getMenu().setGroupVisible(R.id.pospieszne_bus_group, true);
                return true;
            case R.id.back_autobusy:
                navigationView.getMenu().setGroupVisible(R.id.main_group, true);
                navigationView.getMenu().setGroupVisible(R.id.bus_category_group, false);
                navigationView.getMenu().setGroupVisible(R.id.dopodzialu, true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showLine(final String line, String lineLine) {
        firstBusesStart = true;
        linesTimer.cancel();
        linesTimer = null;
        linesTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!firstBusesStart) {
                            if (busesMarkers.size() == 0) {
                                return;
                            } else {
                                busesDetailsList.clear();
                                for (Marker busMarker : busesMarkers) {
                                    busMarker.remove();
                                }
                                busesMarkers.clear();
                                new LoadBuses(line).execute(mMap, url, getApplicationContext(), busesMarkers, busesDetailsList);
                            }
                        } else {
                            new LoadBuses(line).execute(mMap, url, getApplicationContext(), busesMarkers, busesDetailsList);
                        }
                        if (firstBusesStart) {
                            firstBusesStart = false;
                        }

                    }
                });
            }
        };
        this.line = line;
        String tempLineRoute = lineRoute + line;
        String tempLineStops = lineStopsUrl + lineLine;
        Log.d("onClick", "Button is Clicked");
        Log.d("onClick", url);

        if (busesMarkers.size() != 0 || stopsMarkers.size() != 0 || lineDrawing.size() != 0) {
            for (Marker m : busesMarkers) {
                m.remove();
            }
            for (Marker m : stopsMarkers) {
                m.remove();
            }
            for (Polyline m : lineDrawing) {
                m.remove();
            }
            busesDetailsList.clear();
            busesMarkers.clear();
            stopsMarkers.clear();
            lineDrawing.clear();
        }
        linesTimer.scheduleAtFixedRate(timerTask, 0, 30100);
        new LoadStops().execute(mMap, tempLineStops, getApplicationContext(), stopsMarkers);
        new LoadLines().execute(mMap, tempLineRoute, lineDrawing);


    }

    private void showBusesRotation() {
        markerTimer.cancel();
        markerTimer = null;
        markerTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rotateMarkers();
                    }
                });
            }
        };
        markerTimer.scheduleAtFixedRate(timerTask, 3000, 30100);
    }

    private void rotateMarkers() {
        for (int i = 0; i < busesMarkers.size(); i++) {
            LatLng firstPoint = busesMarkers.get(i).getPosition();
            String destination = busesDetailsList.get((i * 4) + 2);
            Location firstLocation = new Location(LocationManager.GPS_PROVIDER);
            firstLocation.setLatitude(firstPoint.latitude);
            firstLocation.setLongitude(firstPoint.longitude);
            for (int j = 0; j < stopsMarkers.size(); j++) {
                if (stopsMarkers.get(j).getTitle().equals(destination)) {
                    LatLng secondPoint = stopsMarkers.get(j).getPosition();
                    Location secondLocation = new Location(LocationManager.GPS_PROVIDER);
                    secondLocation.setLatitude(secondPoint.latitude);
                    secondLocation.setLongitude(secondPoint.longitude);
                    float bearing = firstLocation.bearingTo(secondLocation);
                    busesMarkers.get(i).setRotation(bearing);
                    break;
                }
            }
        }
    }

    public void onMapSearch(View view) {
        editText.clearFocus();
        InputMethodManager in = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        if (destinationMarker != null) {
            destinationMarker.remove();
        }
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        String location = locationSearch.getText().toString();
        List<Address> addressList = null;
        location += "Szczecin";
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addressList.size() == 0) {
                Toast.makeText(this, "Nie znaleziono podanego adresu", Toast.LENGTH_SHORT).show();
                return;
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.place_dest));
            destinationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            searchBar.setVisibility(View.INVISIBLE);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                searchBar.setVisibility(View.INVISIBLE);
            }
        });

        setWindowAdapteronMarkers();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        mMap.setPadding(0, 105, 0, 0);

        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour > 22 || hour < 6) {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_style_json));
        } else {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.day_style_json));
        }
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (nearBusesMarkers != null) {
                    for (Marker busMarker : nearBusesMarkers) {
                        busMarker.remove();
                    }
                }
                new LoadNearStops().execute(mMap, allStopsUrl, latLng.longitude, latLng.latitude, nearBusesMarkers);
            }
        });
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
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        Toast.makeText(MapsActivity.this, "Twoje Aktualne Położenie", Toast.LENGTH_LONG).show();
        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
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
}