package com.qinggan.testtouchbar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        DateTouchBar touchBar = (DateTouchBar) findViewById(R.id.touchBar);
        touchBar.setDate("2017-09-01", "2017-09-07");
        touchBar.setDateChangedListener(new DateTouchBar.DateChangedListener()
        {
            @Override
            public void dateChanged(List<String> date)
            {
                Log.d(TAG, "Start: " + date.get(0) + ", end: " + date.get(1));
            }
        });
    }
}
