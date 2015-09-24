package com.example.allandoamaralalves.upartyproject;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import java.io.IOException;
import java.util.List;


public class MapaEventos extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String stringTestante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_eventos);
        setUpMapIfNeeded();

        Button btn = (Button)findViewById(R.id.btn_criar_evento);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent actionCriarEvento = new Intent(MapaEventos.this, CriarEvento.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MapaEventos.this.startActivity(actionCriarEvento);
            }
        });

        Button btn2 = (Button)findViewById(R.id.btn_meus_eventos);

        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent actionMeusEventos = new Intent(MapaEventos.this, MenuInicial.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MapaEventos.this.startActivity(actionMeusEventos);
            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting a reference to the map
        mMap = supportMapFragment.getMap();

        // Setting a click event handler for the map
        mMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                String longSelecionada = String.valueOf(latLng.longitude);

                TextView t1 = (TextView)findViewById(R.id.long_text);
                t1.setText(longSelecionada);

                String latSelecionada = String.valueOf(latLng.latitude);

                TextView t2 = (TextView)findViewById(R.id.lat_text);
                t2.setText(latSelecionada);

                // Creating a marker
                //MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                //markerOptions.position(latLng);

                // Setting the title for the marker.
                // This will be displayed on taping the marker
                //markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                // Animating to the touched position
                //mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                //mMap.addMarker(markerOptions);
            }
        });
    }

    public void geoLocate (View v) throws IOException {
        hideSoftKeyboard(v);

        EditText et = (EditText) findViewById(R.id.local_procurado);
        String location = et.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(location, 1);

        Address add = list.get(0);
        //Local retornado da pesquisa
        String locality = add.getLocality();

        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        double lat = add.getLatitude();
        double lng = add.getLongitude();

        goToLocation(lat, lng);
    }

    private void goToLocation(double lat, double lng) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
        mMap.moveCamera(update);
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Get LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Get Current Location
        Location myLocation = locationManager.getLastKnownLocation(provider);

        if (myLocation != null) {

            LatLng target = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            CameraPosition position = this.mMap.getCameraPosition();

            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);

            TextView t1 = (TextView)findViewById(R.id.long_text);
            t1.setText(String.valueOf(myLocation.getLongitude()));

            TextView t2 = (TextView)findViewById(R.id.lat_text);
            t2.setText(String.valueOf(myLocation.getLatitude()));

            this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
            //mMap.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).title("You are here!").snippet("Consider yourself located"));
        }

        mMap.addMarker(new MarkerOptions().position(new LatLng(-8.0166618659, -34.94987614452839)).title("Calourada UFRPE").snippet("Mesa Farta"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-8.0166618859, -34.94987613452839)).title("Festinha da Marcela"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-8.0166618899, -34.94987614452839)).title("Piscina do Wagner"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(-8.0196618899, -34.95987614452839)).title("OpenBar de Toddynho"));
        mMap.setMyLocationEnabled(true);
    }
}
