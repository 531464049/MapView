package com.aaa.lib.mapdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;

import com.aaa.lib.map.imp.YXMapView;

public class MainActivity extends AppCompatActivity {

    YXMapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView=findViewById(R.id.mv_main);
        mapView.setBackgroundColor(Color.WHITE);
        mapView.addAreaDivideLineLayer();
    }
}
