package com.example.pc.laboversionone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

public class ChoosModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choos_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageButton comunicationButton = (ImageButton) findViewById(R.id.comunicationMode);
        comunicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent comunicationIntent = new Intent(ChoosModeActivity.this, MapsActivity.class);
                startActivity(comunicationIntent);

            }
        });
        final ImageButton placesButton = (ImageButton) findViewById(R.id.placesMode);
        placesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent placesIntent = new Intent(ChoosModeActivity.this, MapsPlacesActivity.class);
                startActivity(placesIntent);

            }
        });
    }

}
