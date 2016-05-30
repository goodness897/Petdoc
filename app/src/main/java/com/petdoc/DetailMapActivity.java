package com.petdoc;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private int position;
    private ActionBar ab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_map);
        ab = this.getSupportActionBar();
        setCustomActionBar(ab);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng location = new LatLng(LoadingActivity.docItemArrayList.get(position).getLatitude(), LoadingActivity.docItemArrayList.get(position).getLongitude());

        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
        googleMap.addMarker(new MarkerOptions()
                .title(LoadingActivity.docItemArrayList.get(position).getTitle())
                .snippet(LoadingActivity.docItemArrayList.get(position).getAddress())
                .position(location))
                .setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_dog));
    }

    private void setCustomActionBar(ActionBar ab) {

        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ab.setDisplayShowCustomEnabled(true);
        ab.setCustomView(R.layout.custom_actionbar);
        ab.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        View view = ab.getCustomView();
        ImageButton backButton = (ImageButton) view.findViewById(R.id.drawer_button);
        ImageButton nullButton = (ImageButton) view.findViewById(R.id.search_button);
        nullButton.setImageResource(0);
        backButton.setImageResource(R.drawable.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}


