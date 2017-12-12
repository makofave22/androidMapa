package com.acadep.sistemas.pruebamapa.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acadep.sistemas.pruebamapa.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, LocationListener{


    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;
    private FloatingActionButton fab;

    private Location currentLocation,location;
    private LocationManager locationManager;
    private Marker marker;

    private CameraPosition cameraZoom;
    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) rootView.findViewById(R.id.map);
        if (mapView != null) {
            //crear manualmente
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        locationStart();


    }

    private void zoomToLocation(Location location){
        cameraZoom= new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(),location.getLongitude()))
                .zoom(15)//limit 21
                .bearing(0)//0-365
                .tilt(30)  //0-90
                .build();  // 10 Ciudad  15 calle 20 edificio
        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraZoom));
    }

    //codigo para activar el GPS
    private void locationStart(){
        locationManager=(LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpsEnabled){
            Intent settingIntent= new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingIntent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
            //Toast.makeText(getContext(),"Failed!!!",Toast.LENGTH_LONG).show();
            return;
        }
        //gMap.setMyLocationEnabled(true);
        //gMap.getUiSettings().setMyLocationButtonEnabled(false);

        //
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0,this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,this);
    }

    private void locationStart(LocationManager locationManager1,Location location){
        locationManager1=(LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager1.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gpsEnabled){
            Intent settingIntent= new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingIntent);
        }
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},1000);
            //Toast.makeText(getContext(),"Failed!!!",Toast.LENGTH_LONG).show();
            return ;
        }

         location = locationManager1.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location==null){
            location= locationManager1.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        currentLocation=location;
        if(currentLocation!=null){
           createOrUpdateMarkerByLocation(location);
           zoomToLocation(location);
        }
    }
    private void createOrUpdateMarkerByLocation(Location location){

        if(marker == null){
            marker= gMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).draggable(true));

        }else{
            marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        }

    }
    private boolean isGPSEnabled(){
        //activar el gps
        try {
            int gpsSignal= Settings.Secure.getInt(getActivity().getContentResolver(),Settings.Secure.LOCATION_MODE);

            if(gpsSignal==0){
                //No tenemos senal de gps
                return false;
            }
            else{
                return true;
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showInfoAlert(){
        new AlertDialog.Builder(getContext()).setTitle("GPS Signal")
                .setMessage("You don't have GPS signal. Would you like to enable the GPS signal?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                }).setNegativeButton("CANCEL",null).show();
    }

    @Override
    public void onClick(View view) {
       if( !this.isGPSEnabled()){
           showInfoAlert();
       }else{

          locationStart(locationManager,location);


       }
    }

    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(getContext(),"Changed! ->"+ location.getProvider(),Toast.LENGTH_LONG).show();
        createOrUpdateMarkerByLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
