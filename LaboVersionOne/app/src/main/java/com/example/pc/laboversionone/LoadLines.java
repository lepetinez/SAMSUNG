package com.example.pc.laboversionone;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoadLines extends AsyncTask<Object, Void, JSONObject> {
    private GoogleMap mMap;
    private List<Polyline> lineList;

    @Override
    protected JSONObject doInBackground(Object... params) {
        mMap = (GoogleMap) params[0];
        String urlBuses = (String) params[1];
        lineList = (List<Polyline>) params[2];

        DownloadJsonData downloadJsonDataVehicle = new DownloadJsonData();

        JSONObject lineJson = downloadJsonDataVehicle.JsonLineParser(urlBuses);

        return lineJson;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        drawLinePolyline(jsonObject);

    }

    private void drawLinePolyline(JSONObject jsonObject) {
        Polyline line;
       // PolylineOptions options;
        JSONArray features = null;
        try {
            features = jsonObject.getJSONArray("features");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < features.length(); i++) {
            PolylineOptions options = new PolylineOptions().width(7).color(Color.BLUE).visible(true);
            JSONObject zero = null;
            try {
                zero = features.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject geometry = null;
            try {
                geometry = zero.getJSONObject("geometry");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray coordinates = null;
            try {
                coordinates = geometry.getJSONArray("coordinates");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            int length = coordinates.length();
            JSONArray tempArray = null;
            for (int j = 0; j < length; j++) {
                try {
                    tempArray = coordinates.getJSONArray(j);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    options.add(new LatLng(tempArray.getDouble(1), tempArray.getDouble(0)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            line = mMap.addPolyline(options);
            lineList.add(line);
        }


    }
}
