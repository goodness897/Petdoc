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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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
        mapFragment.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(), "마커 id : " + marker.getId(), Toast.LENGTH_LONG).show();
                return false;
            }
        });


        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(savedInstanceState == null) {
            showItems();
        }

        startLocationService();

        checkDangerousPermissions();

        listFragment.adapter.notifyDataSetChanged();
    }



    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity", "권한 있음");
        } else {
            Log.i("MainActivity", "권한 없음");

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Log.i("MainActivity", "권한 설명 필요함");
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("MainActivity", "권한 승인됨");
                } else {
                    Log.i("MainActivity", "권한 승인이 되지 않음");
                }
            }
        }
    }
    @Override
    public void onListItemSelected(int position) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("MainActivity", "onResume() 실행");

        mapFragment.setMyLocationEnabled(true);
        startLocationService();

    }

    @Override
    protected void onPause() {

        super.onPause();
        Log.i("MainActivity", "onPause() 실행");

        mapFragment.setMyLocationEnabled(false);
        /*if (marker == null)
            marker = new MarkerOptions();*/
    }

    private void startLocationService() {
        // 위치 관리자 객체 참조
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 리스너 객체 생성
        GPSListener gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;
        try {
            // GPS 기반 요청
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            // 네트워크 기반 위치 요청
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();
                Log.i("MainActivity", "startLocation 실행, 현재 위치 : " + latitude + "," + longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showItems() {
        Toast.makeText(getApplicationContext(), "showItems 실행", Toast.LENGTH_LONG).show();
        Log.i("MainActivity", "showItems 실행");

        marker = new MarkerOptions();

        for (int i = 0; i < loadingActivity.docItemArrayList.size(); i++) {
            Log.i("MainActivity", "showItems 실행");
            marker.position(new LatLng(loadingActivity.docItemArrayList.get(i).getLatitude(), loadingActivity.docItemArrayList.get(i).getLongitude()));
            marker.title(loadingActivity.docItemArrayList.get(i).getTitle());
            marker.snippet(loadingActivity.docItemArrayList.get(i).getAddress());
            marker.draggable(true);
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
            mapFragment.addMarker(marker);
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

        private void showCurrentLocation(Double latitude, Double longitude) {
            Log.i("MainActivity", "showCurrentLocation 실행 : " + latitude + "," + longitude);
            // 현재 위치를 이용해 LatLng 객체 생성
            LatLng curPoint = new LatLng(latitude, longitude);

            CameraPosition cp = new CameraPosition.Builder().target((curPoint)).zoom(15).build();
            mapFragment.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
            // mapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            mapFragment.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            /*if (marker == null){
                marker = new MarkerOptions();
                showItems(latitude, longitude);
            }*/
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
