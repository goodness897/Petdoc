package com.petdoc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by STU on 2016-05-03.
 */
public class MapFragment extends Fragment {
    private double from_to_distance = 10000;
    private SupportMapFragment fragment;
    private GoogleMap map;
    private MarkerOptions marker;
    LoadingActivity loadingActivity;
    private GPSListener gpsListener;
    private double latitude;
    private double longitude;
    public static ArrayList<DocItem> doc_list = new ArrayList<>();
    ArrayList<String> distance_list = new ArrayList<>();
    String finalSDistance;
    private LocationManager manager;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            finalSDistance = b.getString("My Distance");
            distance_list.add(finalSDistance);
        }
    };


    public static interface MapMarkerSelectionCallback {
        public void onMapMarkerSeleted(int position);
    }
    public MapMarkerSelectionCallback callback;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        startLocationService();
        checkDangerousPermissions();
    }
    private void checkDangerousPermissions() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(getActivity(), permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity", "권한 있음");
        } else {
            Log.i("MainActivity", "권한 없음");

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[0])) {
                Log.i("MainActivity", "권한 설명 필요함");
            } else {
                ActivityCompat.requestPermissions(getActivity(), permissions, 1);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i("MainActivity","MapFragment onAttach 실행");

        if (context instanceof MapMarkerSelectionCallback){
            callback = (MapMarkerSelectionCallback) context;
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MainActivity","MapFragment onCreateView 실행");
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("MainActivity","MapFragment onResume 실행");
        if (map == null){
            map = fragment.getMap();
            if(doc_list.size() > 0){
                doc_list.clear();
            }
            showItems();
            gpsListener.showCurrentLocation(latitude, longitude);

        }
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String title = marker.getTitle();
                int id = searchPosition(title);
                if(callback != null){
                    Log.i("MainActivity","MapFragment callback 실행, id 값 : " + id);
                    callback.onMapMarkerSeleted(id);
                }
                return false;
            }
        });
    }
    private int searchPosition(String title) {
        int id;
        for (int i = 0; i < loadingActivity.docItemArrayList.size(); i++){
            if(loadingActivity.docItemArrayList.get(i).getTitle().equals(title)){
                id = loadingActivity.docItemArrayList.get(i).getId();
                return id;
            }
        }
        return 0;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void startLocationService() {
        // 위치 관리자 객체 참조
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        // 리스너 객체 생성
        gpsListener = new GPSListener();
        long minTime = 10000;
        float minDistance = 0;
        try {
            // GPS 기반 요청
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
            // 네트워크 기반 위치 요청
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, gpsListener);
            //Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location lastLocation = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (lastLocation != null) {
                latitude = lastLocation.getLatitude();
                longitude = lastLocation.getLongitude();
                Log.i("MainActivity", "startLocation 실행, 현재 위치 : " + latitude + "," + longitude);
                gpsListener.showCurrentLocation(latitude, longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        map.setMyLocationEnabled(false);
        Log.i("MainActivity", "MapFragment onPause 실행");
        manager.removeUpdates(gpsListener);

    }

    private void showItems() {
        ArrayList<DocItem> key = null;
        key = loadingActivity.docItemArrayList;
        double distance = 0.0;
        int j = 0;
        marker = new MarkerOptions();
        for (int i = 0; i < key.size(); i++) {
            // 현재 위치와 거리 계산 후 반경안에 드는 것만 마커를 띄워주기
            distance = calDistance(latitude, longitude, key.get(i).getLatitude(), key.get(i).getLongitude());
            if(distance < from_to_distance) { // m 단위
                Log.i("MainActivity", "showItems 실행");
                marker.position(new LatLng(key.get(i).getLatitude(), key.get(i).getLongitude()));
                marker.title(key.get(i).getTitle());
                marker.snippet(key.get(i).getAddress());
                marker.draggable(true);
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pet_marker));
                map.addMarker(marker);
                doc_list.add(new DocItem(key.get(i).getId(), key.get(i).getTitle()
                        ,key.get(i).getAddress(), key.get(i).getPhone(), key.get(i).getLatitude(), key.get(i).getLongitude(), distance));
            }
        }
    }
    public double calDistance(double lat1, double lon1, double lat2, double lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }

    private class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Double current_latitude = location.getLatitude();
            Double current_longitude = location.getLongitude();
            // showCurrentLocation(latitude, longitude);
            double distance = calDistance(latitude, longitude, current_latitude, current_longitude);
            if (distance > 100 ) {
                showItems();
            }
            Log.i("MainActivity", "onLocationChanged 실행, 현재 위치 : " + latitude + "," + longitude);
        }
        public void showCurrentLocation(Double latitude, Double longitude) {
            Log.i("MainActivity", "showCurrentLocation 실행 : " + latitude + "," + longitude);
            // 현재 위치를 이용해 LatLng 객체 생성
            LatLng curPoint = new LatLng(latitude, longitude);
            CameraPosition cp = new CameraPosition.Builder().target((curPoint)).zoom(15).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
            // mapFragment.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
