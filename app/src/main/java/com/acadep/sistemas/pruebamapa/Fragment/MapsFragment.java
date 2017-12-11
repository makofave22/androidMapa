package com.acadep.sistemas.pruebamapa.Fragment;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.acadep.sistemas.pruebamapa.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener,View.OnClickListener{


    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;

    private List<Address> addresses;
    private Geocoder geocoder;

    private MarkerOptions marker;
    private FloatingActionButton fab;


    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_maps, container, false);
        fab= (FloatingActionButton)rootView.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView=(MapView)rootView.findViewById(R.id.map);
        if(mapView!=null){
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
        gMap= googleMap;

        LatLng place = new LatLng(37.3890924, -5.9844589);

        CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);

        marker= new MarkerOptions();
        marker.position(place);
        marker.title("Mi marcador");

        marker.draggable(true);
        marker.snippet("Esto es una caja de texto donde modificar los datos");
        marker.icon(BitmapDescriptorFactory.fromResource(android.R.drawable.star_on));//aqui podemos poner la imagen que deseemos al marcador
        gMap.addMarker(marker);

        //gMap.addMarker(new MarkerOptions().position(place).title("Marker in Sevilla").draggable(true));//arrastrar el marcador y al soltarlo dame informacion
        gMap.moveCamera(CameraUpdateFactory.newLatLng(place));
        gMap.animateCamera(zoom);

        //
        gMap.setOnMarkerDragListener(this);

        //geocoader nos dara informacion dependiendo de latitude y longitud
        geocoder=new Geocoder(getContext(), Locale.getDefault());




    }
    private void checkIfGPSIsEnable(){
        //activar el gps
        try {
            int gpsSignal= Settings.Secure.getInt(getActivity().getContentResolver(),Settings.Secure.LOCATION_MODE);

            if(gpsSignal==0){
                //No tenemos senal de gps
                showInfoAlert();
            }
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
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
    public void onMarkerDragStart(Marker marker) {

        marker.hideInfoWindow();//cerramos el marcador cuando lo agarramos y arrastramos
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        double latitude = marker.getPosition().latitude;
        double longitude= marker.getPosition().longitude;

        //el numero maxResults es el numero maximo de localizaciones arecibir
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getAddressLine(0);
        String city= addresses.get(0).getLocality();   //getLocality es para la ciudad
        String state= addresses.get(0).getAdminArea(); //Es para el estado
        String country= addresses.get(0).getCountryName();
        String postalCode= addresses.get(0).getPostalCode();

        marker.setSnippet("address: "+address+"\n"+
                        "city: "+city+"\n"+
                        "state: "+state+"\n"+
                        "country: "+country+"\n"+
                        "postalCode: "+postalCode+"\n");

        marker.showInfoWindow();



        /*Toast.makeText(getContext(), "address: "+address+"\n"+
                        "city: "+city+"\n"+
                        "state: "+state+"\n"+
                        "country: "+country+"\n"+
                        "postalCode: "+postalCode+"\n"
                , Toast.LENGTH_SHORT).show();*/



    }

    @Override
    public void onClick(View view) {
        this.checkIfGPSIsEnable();
    }
}
