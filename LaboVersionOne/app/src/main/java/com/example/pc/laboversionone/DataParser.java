package com.example.pc.laboversionone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private final static String DATA_DLUGOSC_GEOGRAFICZNA = "lon";
    private final static String DATA_SZEROKOSC_GEOGRAFICZNA = "lat";
    private final static String DATA_LINIA = "linia";
    private final static String DATA_Z = "z";
    private final static String DATA_DO = "do";
    private final static String DATA_PUNKTUALNSOSC = "punktualnosc1";
    private final static String ID = "id";
    private String linia;
    private final static String NAZWA = "nazwa";
    private final static String DLUGOSC_GEO ="dlugoscgeo";
    private final static String SZEROKOSC_GEO = "szerokoscgeo";

    public DataParser(String linia) {

        this.linia = linia;
    }
    public DataParser(){

    }

    public List<HashMap<String, String>> busParser(JSONArray jsonArray) throws JSONException {
        int jsonLength = jsonArray.length();
        HashMap<String, String> tempMap;
        List<HashMap<String, String>> communicationData = new ArrayList<>();
        for (int i = 0; i < jsonLength; i++) {
            JSONObject tempObject = jsonArray.getJSONObject(i);
            tempMap = new HashMap<>();
                if (tempObject.getString(DATA_LINIA).equals(linia
                )) {
                    tempMap.put(DATA_DLUGOSC_GEOGRAFICZNA, tempObject.getString(DATA_DLUGOSC_GEOGRAFICZNA));
                    tempMap.put(DATA_SZEROKOSC_GEOGRAFICZNA, tempObject.getString(DATA_SZEROKOSC_GEOGRAFICZNA));
                    tempMap.put(DATA_LINIA, tempObject.getString(DATA_LINIA));
                    tempMap.put(DATA_Z, tempObject.getString(DATA_Z));
                    tempMap.put(DATA_DO, tempObject.getString(DATA_DO));
                    tempMap.put(DATA_PUNKTUALNSOSC, tempObject.getString(DATA_PUNKTUALNSOSC));
                    communicationData.add(tempMap);
                }
        }
        return communicationData;
    }
    public List<HashMap<String, String>> stopParse(JSONArray jsonArray) throws JSONException {
        int jsonLength = jsonArray.length();
        HashMap<String, String> tempMap;
        List<HashMap<String, String>> stopsData = new ArrayList<>();
        for (int i = 0; i < jsonLength; i++) {
            JSONObject tempObject = jsonArray.getJSONObject(i);
            tempMap = new HashMap<>();
                tempMap.put(ID, tempObject.getString(ID));
                tempMap.put(DLUGOSC_GEO, tempObject.getString(DLUGOSC_GEO));
                tempMap.put(SZEROKOSC_GEO, tempObject.getString(SZEROKOSC_GEO));
                tempMap.put(NAZWA, tempObject.getString(NAZWA));
                stopsData.add(tempMap);

        }
        return stopsData;
    }
}
