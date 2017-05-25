package com.example.pc.laboversionone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class FullscreenActivity extends Activity{


    private static final int THREAD_LENGTH = 2000;
    private boolean PAUSE_FLAG = false;
    private Runnable RUNNABLE;
    private Handler RUNNABLE_HANDLER;
    private long TIME_OF_PAUSE;
    private long TIME_OF_CREATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TIME_OF_CREATE = System.currentTimeMillis();
        setContentView(R.layout.activity_fullscreen);
        initialize();
    }

    public void onBackPressed() {
        super.onBackPressed();
        RUNNABLE_HANDLER.removeCallbacks(RUNNABLE);
    }

    public void initialize() {
        RUNNABLE_HANDLER = new Handler();
        RUNNABLE = new Runnable() {
            @Override
            public void run() {
                if (!PAUSE_FLAG) {
                    startActivity(new Intent(getApplicationContext(), ChoosModeActivity.class));
                    finish();
                }
            }
        };
        RUNNABLE_HANDLER.postDelayed(RUNNABLE, THREAD_LENGTH);
    }

    protected void onPause() {
        TIME_OF_PAUSE = System.currentTimeMillis();
        super.onPause();
        PAUSE_FLAG = true;
        RUNNABLE_HANDLER.removeCallbacks(RUNNABLE);
    }

    protected void onRestart() {
        super.onRestart();
        PAUSE_FLAG = false;
        RUNNABLE_HANDLER.postDelayed(RUNNABLE, TIME_OF_CREATE - TIME_OF_PAUSE);
    }
}