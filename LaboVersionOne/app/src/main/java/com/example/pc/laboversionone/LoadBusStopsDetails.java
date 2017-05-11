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
        try {
            doc = Jsoup.connect(stopInfo).get();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Elements newsHeadlines = doc.getElementsByTag("strong");
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
            for (int i = 0; i < 5; i++) {
                singleBusData[i + 2] = busLines.get(i).text();
            }

            Elements busDates = doc.getElementsByTag("a");
            for (int i = 0; i < 10; i++) {
                singleBusData[i + 7] = busDates.get(i).text();
            }

            return singleBusData;
        }
    }
}