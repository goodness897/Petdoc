package com.petdoc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListFragment.ListItemSelectionCallback {

    private final String URL = "http://192.168.11.20/alldata.php"; // php 주소
    private phpDown task;
    public static ArrayList<DocItem> docItemArrayList = new ArrayList<DocItem>();
    private ListFragment listFragment;

    private GoogleMap mapFragment;

    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        task = new phpDown();
        task.execute(URL);
        FragmentManager manager = getSupportFragmentManager();
        listFragment = (ListFragment) manager.findFragmentById(R.id.listFragment);
        mapFragment = ((SupportMapFragment) manager.findFragmentById(R.id.mapFragment)).getMap();
        startLocationService();
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

    private class phpDown extends AsyncTask<String, Integer, String> {

        CustomProgressDialog asyncDialog = new CustomProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {

            //asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            // show dialog
            asyncDialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("MainActivity", "doInBackground 실행중");
            StringBuilder jsonHtml = new StringBuilder();
            try {
                java.net.URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            String line = br.readLine();
                            if (line == null) {
                                Log.i("MainActivity", "doInBackground 끝");

                                break;
                            }
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            String title;
            String address;
            String phone;
            double latitude;
            double longitude;

            try {
                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    title = jo.getString("title");
                    address = jo.getString("address");
                    phone = jo.getString("phone");
                    latitude = jo.getDouble("latitude");
                    longitude = jo.getDouble("longitude");
                    docItemArrayList.add(new DocItem(title, address, phone, latitude, longitude));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i("MainActivity", docItemArrayList.get(0).getTitle() + docItemArrayList.get(0).getAddress() + docItemArrayList.get(0).getPhone());
            // marker 추가
            if (marker == null) {
                marker = new MarkerOptions();
                showItems();
            }
            // listFragment.listView.invalidate();
            listFragment.adapter.notifyDataSetChanged();
            asyncDialog.dismiss();
        }

    }

    private void showItems() {
        Toast.makeText(getApplicationContext(), "showItems 실행", Toast.LENGTH_LONG).show();
        Log.i("MainActivity", "showItems 실행");


        for (int i = 0; i < docItemArrayList.size(); i++) {
            Log.i("MainActivity", "showItems 실행");
            marker.position(new LatLng(docItemArrayList.get(i).getLatitude(), docItemArrayList.get(i).getLongitude()));
            marker.title(docItemArrayList.get(i).getTitle());
            marker.snippet(docItemArrayList.get(i).getAddress());
            marker.draggable(true);
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark));
            mapFragment.addMarker(marker);
        }

    }


}
