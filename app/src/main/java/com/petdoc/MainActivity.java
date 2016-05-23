package com.petdoc;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.MapsInitializer;

public class MainActivity extends AppCompatActivity implements MapFragment.MapMarkerSelectionCallback {

    private ListFragment listFragment;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();
        listFragment = (ListFragment) manager.findFragmentById(R.id.listFragment);
        mapFragment = (MapFragment) manager.findFragmentById(R.id.mapFragment);
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume() 실행");
        // startLocationService();

    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.i("MainActivity", "onPause() 실행");

    }

    @Override
    public void onMapMarkerSeleted(int position) {
        listFragment.setSeletedItem(position);
    }

}
