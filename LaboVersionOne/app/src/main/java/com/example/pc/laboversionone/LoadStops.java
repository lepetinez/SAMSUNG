package com.example.pc.laboversionone;

import android.content.Context;
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

/**
 * Created by Pc on 18.04.2017.
 */

public class LoadStops extends AsyncTask<Object,Void, JSONArray> {

    private Context context;
    private GoogleMap mMap;
    private JSONArray googleStops;
    private List<Marker> busesStops;

    @Override
    protected JSONArray doInBackground(Object... params) {
        mMap = (GoogleMap) params[0];
        String urlStops = (String) params[1];
        context = (Context) params[2];
        busesStops = (List<Marker>) params[3];

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
            Log.d("onPostExecute", "Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = stopsData.get(i);
            double lat = Double.valueOf(googlePlace.get("szerokoscgeo"));
            double lng = Double.valueOf(googlePlace.get("dlugoscgeo"));
            String nazwa = googlePlace.get("nazwa");
            String id = googlePlace.get("id");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.bus_stop_icon));
            markerOptions.title(nazwa);
            markerOptions.snippet(id);
            Marker marker = mMap.addMarker(markerOptions);
            busesStops.add(marker);
        }
    }
}
