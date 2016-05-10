package com.petdoc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements ListFragment.ListItemSelectionCallback {

    private ListFragment listFragment;
    private GoogleMap mapFragment;
    LoadingActivity loadingActivity;
    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getSupportFragmentManager();
        listFragment = (ListFragment) manager.findFragmentById(R.id.listFragment);
        mapFragment = ((SupportMapFragment) manager.findFragmentById(R.id.mapFragment)).getMap();
        startLocationService();
        listFragment.adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemSelected(int position) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (mapFragment != null) {
            mapFragment.setMyLocationEnabled(true);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (mapFragment != null)
            mapFragment.setMyLocationEnabled(false);
        /*if (marker == null)
            marker = new MarkerOptions();*/
    }

    private void startLocationService() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastLocation != null) {

                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();

                Log.i("MainActivity", "startLocation 실행, 현재 위치 : " + latitude + "," + longitude);
                gpsListener.showCurrentLocation(latitude, longitude);
                gpsListener.showItems();

            }
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Double latitude = location.getLatitude();
            Double longitude = location.getLongitude();

            showCurrentLocation(latitude, longitude);

            Log.i("MainActivity", "onLocationChanged 실행, 현재 위치 : " + latitude + "," + longitude);

        }

        public void showCurrentLocation(Double latitude, Double longitude) {

            Log.i("MainActivity", "showCurrentLocation 실행 : " + latitude + "," + longitude);
            LatLng curPoint = new LatLng(latitude, longitude);

            // # 1
            /*CameraPosition cp = new CameraPosition.Builder().target((curPoint)).zoom(15).build();
            mapFragment.animateCamera(CameraUpdateFactory.newCameraPosition(cp));*/

            mapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            mapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            /*if (marker == null){
                marker = new MarkerOptions();
                showItems(latitude, longitude);
            }*/
        }

        private void showItems() {
            Toast.makeText(getApplicationContext(), "showItems 실행", Toast.LENGTH_LONG).show();
            Log.i("MainActivity", "showItems 실행");


            for (int i = 0; i < loadingActivity.docItemArrayList.size(); i++) {
                Log.i("MainActivity", "showItems 실행");
                marker.position(new LatLng(loadingActivity.docItemArrayList.get(i).getLatitude(), loadingActivity.docItemArrayList.get(i).getLongitude()));
                marker.title(loadingActivity.docItemArrayList.get(i).getTitle());
                marker.snippet(loadingActivity.docItemArrayList.get(i).getAddress());
                marker.draggable(true);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark));
                mapFragment.addMarker(marker);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }





}
