package com.example.pc.laboversionone;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class LoadBusStopsDetails extends AsyncTask<Object, Void, String[]> {
    String stopInfo;
    String[] singleBusData;
    Document doc;

    @Override
    protected String[] doInBackground(Object... params) {
        singleBusData = (String[]) params[0];
        stopInfo = (String) params[1];
        createDoc();
        if(doc == null){
            fillBusesWithNull();
            return singleBusData;
        }
        Elements newsHeadlines = doc.getElementsByTag("strong");
        if(newsHeadlines.size() == 0){
            singleBusData[0] = "wystapil blad. sprobuj ponownie za chwile";
            for(int i = 0; i < 16;i++){
                singleBusData[i+1] = "";
            }
            return singleBusData;
        }
        String label1 = newsHeadlines.get(0).text();
        singleBusData[0] = label1;
        if(newsHeadlines.size()== 2){
            singleBusData[1] = "z tego przystanku obecnie nie ma zadnych kursow";
            for(int i = 0; i < 15;i++){
                singleBusData[i+2] = "";
            }
            return singleBusData;
        }
        else {
            String label2 = newsHeadlines.get(2).text();
            singleBusData[1] = label2;

            Elements busLines = doc.getElementsByClass("komorkalinia");
            if(busLines.size()==4){
                fillBusesDetailsInfoWindow(busLines,4,8);
            }else if(busLines.size()==5){
                fillBusesDetailsInfoWindow(busLines,5,10);
            }
            else if(busLines.size() == 2){
                fillBusesDetailsInfoWindow(busLines,2,4);
            }
            else if(busLines.size() == 3){
                fillBusesDetailsInfoWindow(busLines,3,6);
            }
            return singleBusData;
        }
    }
    public void createDoc(){
        try {
            this.doc = Jsoup.connect(stopInfo).get();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public void fillBusesWithNull(){
        singleBusData[0] = "wystapil blad. sprobuj ponownie za chwile";
        for(int i = 0; i < 16;i++){
            singleBusData[i+1] = "";
        }
    }
    public void fillBusesDetailsInfoWindow(Elements busLines,int first, int second){
        for (int i = 0; i < first; i++) {
            singleBusData[i + 2] = busLines.get(i).text();
        }
        Elements busDates = doc.getElementsByTag("a");
        for (int i = 0; i < second; i++) {
            singleBusData[i + 7] = busDates.get(i).text();
        }
    }
}