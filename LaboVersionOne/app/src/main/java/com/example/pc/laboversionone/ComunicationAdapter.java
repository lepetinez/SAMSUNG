package com.example.pc.laboversionone;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Pc on 21.03.2017.
 */

public class ComunicationAdapter extends BaseAdapter{
    Context context;
    List<HashMap<String,String>> busesList;

    public ComunicationAdapter(Context context, List<HashMap<String, String>> busesList) {
        this.context = context;
        this.busesList = busesList;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
