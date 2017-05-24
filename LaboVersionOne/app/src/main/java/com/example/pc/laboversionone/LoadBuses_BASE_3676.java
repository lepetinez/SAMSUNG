package com.example.pc.laboversionone;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;


public class LoadBuses extends AsyncTask<Object, String, JSONArray> {
    // double currentLat;
    //double currentLon;
    private Context context;
    private GoogleMap mMap;
    private String linia;
    private JSONArray googleBusesOrTrams;
    private List<Marker> busMarkers ;
    private List<String>busesDetailsList;

    public LoadBuses(String linia) {
        this.linia = linia;
    }

    @Override
    protected JSONArray doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");

            mMap = (GoogleMap) params[0];
            String urlBuses = (String) params[1];
            context = (Context) params[2];
            busMarkers = (List<Marker>)params[3];
            busesDetailsList = (List<String>) params[4];
            // String urlStops = (String) params[3];
            //currentLat = (Double) params[4];
            //currentLon = (Double) params[5];

            DownloadJsonData downloadJsonDataVehicle = new DownloadJsonData();

            googleBusesOrTrams = downloadJsonDataVehicle.JsonBusesParser(urlBuses);

            //googleStops = downloadJsonDataVehicle.JsonStopsParser(urlStops);
            //googleComunicationData[1] = googleStops;

            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googleBusesOrTrams;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        DataParser busesParser = new DataParser(linia);
        List<HashMap<String, String>> busesData = null;
        try {
            busesData = busesParser.busParser(jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (busesData.size() == 0) {
            Toast.makeText(context, "Linia nr: " + linia + " aktualnie nie kursuje", Toast.LENGTH_SHORT).show();
        } else {
            searchForBuses(busesData);
        }
    }

    private void searchForBuses(List<HashMap<String, String>> busesData) {
        for (int i = 0; i <busesData.size(); i++) {
            Log.d("onPostExecute", "Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = busesData.get(i);
            double lat = Double.valueOf(googlePlace.get("lat"));
            double lng = Double.valueOf(googlePlace.get("lon"));
            String linia = googlePlace.get("linia");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.snippet(Integer.toString(i));
            Marker marker =  mMap.addMarker(markerOptions);
            busMarkers.add(marker);
            busesDetailsList.add(googlePlace.get("linia"));
            busesDetailsList.add(googlePlace.get("z"));
            busesDetailsList.add(googlePlace.get("do"));
            busesDetailsList.add(googlePlace.get("punktualnosc1"));

        }
    }
}