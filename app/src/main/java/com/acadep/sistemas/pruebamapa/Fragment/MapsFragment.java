package com.acadep.sistemas.pruebamapa.Fragment;



import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener{


    private View rootView;
    private MapView mapView;
    private GoogleMap gMap;

    private List<Address> addresses;
    private Geocoder geocoder;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_maps, container, false);
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
    public void onMapReady(GoogleMap googleMap) {
        gMap= googleMap;

        LatLng sydney = new LatLng(-37.3890924, -5.9844589);

        CameraUpdate zoom= CameraUpdateFactory.zoomTo(15);
        
        gMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sevilla").draggable(true));//arrastrar el marcador y al soltarlo dame informacion
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        gMap.animateCamera(zoom);

        //
        gMap.setOnMarkerDragListener(this);

        //geocoader nos dara informacion dependiendo de latitude y longitud
        geocoder=new Geocoder(getContext(), Locale.getDefault());

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

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


        Toast.makeText(getContext(), "address: "+address+"\n"+
                        "city: "+city+"\n"+
                        "state: "+state+"\n"+
                        "country: "+country+"\n"+
                        "postalCode: "+postalCode+"\n"
                , Toast.LENGTH_SHORT).show();



    }
}
