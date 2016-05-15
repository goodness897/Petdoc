package com.petdoc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.lang.reflect.Field;

/**
 * Created by STU on 2016-05-03.
 */
public class MapFragment extends Fragment {

    private SupportMapFragment fragment;
    private GoogleMap map;
    private MarkerOptions marker;
    LoadingActivity loadingActivity;

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
            MapsInitializer.initialize(getActivity());

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
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("MainActivity","MapFragment onResume 실행");
        if (map == null){
            map = fragment.getMap();
            showItems();
        }
        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String title = marker.getTitle();
                int position = searchPosition(title);
                if(callback != null){
                    Log.i("MainActivity","MapFragment callback 실행, position : " + position);
                    callback.onMapMarkerSeleted(position);
                }
                return false;
            }
        });


    }

    private int searchPosition(String title) {
        int position;
        for (int i = 0; i < loadingActivity.docItemArrayList.size(); i++){
            if(loadingActivity.docItemArrayList.get(i).getTitle().equals(title)){
                position = loadingActivity.docItemArrayList.get(i).getId();

                return position;
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
        LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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

    @Override
    public void onPause() {
        super.onPause();
        map.setMyLocationEnabled(false);
    }

    private void showItems() {
        Toast.makeText(getContext(), "showItems 실행", Toast.LENGTH_LONG).show();
        Log.i("MainActivity", "showItems 실행");

        marker = new MarkerOptions();
        for (int i = 0; i < loadingActivity.docItemArrayList.size(); i++) {
            Log.i("MainActivity", "showItems 실행");
            marker.position(new LatLng(loadingActivity.docItemArrayList.get(i).getLatitude(), loadingActivity.docItemArrayList.get(i).getLongitude()));
            marker.title(loadingActivity.docItemArrayList.get(i).getTitle());
            marker.snippet(loadingActivity.docItemArrayList.get(i).getAddress());
            marker.draggable(true);
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.mark));
            map.addMarker(marker);

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
