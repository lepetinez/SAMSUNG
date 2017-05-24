package com.example.pc.laboversionone;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

import static java.lang.Math.abs;

/**
 * Created by Pc on 18.04.2017.
 */

public class LoadNearStops extends AsyncTask<Object, Void, JSONArray> {

    private double currentLon;
    private double currentLat;
    private GoogleMap mMap;
    private JSONArray googleStops;
    private List<Marker> nearBusesStops;

    @Override
    protected JSONArray doInBackground(Object... params) {
        mMap = (GoogleMap) params[0];
        String urlStops = (String) params[1];
        currentLon = (double) params[2];
        currentLat = (double) params[3];
        nearBusesStops = (List<Marker>) params[4];

        DownloadJsonData downloadJsonDataVehicle = new DownloadJsonData();

        googleStops = downloadJsonDataVehicle.JsonStopsParser(urlStops);

        return googleStops;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);

        DataParser stopsParser = new DataParser();
        List<HashMap<String, String>> stopsData = null;
        try {
            stopsData = stopsParser.stopParse(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        searchForStops(stopsData);

    }

    private void searchForStops(List<HashMap<String, String>> stopsData) {
        for (int i = 0; i < stopsData.size(); i++) {
            if (abs(Double.valueOf(stopsData.get(i).get("szerokoscgeo"))- currentLat ) < 0.003 && abs(Double.valueOf(stopsData.get(i).get("dlugoscgeo"))- currentLon)  < 0.003) {
                Log.d("onPostExecute", "Entered into showing locations");
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = stopsData.get(i);
                double lat = Double.valueOf(googlePlace.get("szerokoscgeo"));
                double lng = Double.valueOf(googlePlace.get("dlugoscgeo"));
                String nazwa = googlePlace.get("nazwa");
                String id = googlePlace.get("id");
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.new_bus_stop_icon));
                markerOptions.title(nazwa);
                markerOptions.snippet(id);
                Marker marker = mMap.addMarker(markerOptions);
                nearBusesStops.add(marker);
            }
        }
    }
}
