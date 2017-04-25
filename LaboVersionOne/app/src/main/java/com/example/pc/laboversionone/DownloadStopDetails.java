package com.example.pc.laboversionone;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Pc on 03.04.2017.
 */

public class DownloadStopDetails {
    public String detailsParser(String url) {
        String result = null;
        InputStream is;
        try {
            URL Url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setRequestMethod("GET");
            is = new BufferedInputStream(conn.getInputStream());
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("/n");
                }
                result = sb.toString();
                reader.close();
                is.close();
                conn.disconnect();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
