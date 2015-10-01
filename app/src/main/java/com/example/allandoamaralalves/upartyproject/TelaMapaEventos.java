package com.example.allandoamaralalves.upartyproject;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.content.Context;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TelaMapaEventos extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Geocoder gc;

    private ProgressDialog pDialog;
    JSONParser jsonParser = new JSONParser();
    // eventos JSONArray
    private JSONArray events = null;

    protected boolean criarEventoFlag;

    //Valores usados na chamada das telas Criar Evento e Visualizar Evento.
    protected String latSelecionada, longSelecionada, cidade, endereco, eventoId;

    ArrayList<HashMap<String, String>> listaEventos;
    private static String url_all_events = "http://uparty.3eeweb.com/db_retornar_eventos.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENTS = "eventos";
    private static final String TAG_ID = "evento_id";
    private static final String TAG_NAME = "evento_titulo";
    private static final String TAG_DESC = "evento_descricao";
    private static final String TAG_LONG = "evento_longitude";
    private static final String TAG_LAT = "evento_latitude";

    //dimensoes tela celular
    private int screenWidth, screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_eventos);
        setUpMapIfNeeded();

        //definir valores de tamanho da tela
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;

        //Geocoder para metodos de localizacao
        gc = new Geocoder(this);
        latSelecionada = "";
        longSelecionada = "";

        final Button btnCriarEvento = (Button)findViewById(R.id.btn_criar_evento);
        //flag para habilitar ou não a seleção do mapa redirecionando pra pagina de criar evento
        criarEventoFlag = false;

        // Loading products in Background Thread
        new LoadAllEvents().execute();

        btnCriarEvento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent actionCriarEvento = new Intent(TelaMapaEventos.this, TelaCriarEvento.class);
                //TelaMapaEventos.this.startActivity(actionCriarEvento);
                if (criarEventoFlag) {
                    criarEventoFlag = false;
                    btnCriarEvento.setBackgroundColor(16711737);
                } else {
                    if ((longSelecionada != "") && (latSelecionada != "")) {
                        Intent actionCriarEvento = new Intent(TelaMapaEventos.this, TelaCriarEvento.class);
                        actionCriarEvento.putExtra("long", longSelecionada);
                        actionCriarEvento.putExtra("lat", latSelecionada);
                        actionCriarEvento.putExtra("cidade", cidade);
                        actionCriarEvento.putExtra("endereco", endereco);
                        //Optional parameters
                        TelaMapaEventos.this.startActivity(actionCriarEvento);
                    } else {
                        Toast.makeText(getApplicationContext(), "Selecione no mapa o local de seu evento!", Toast.LENGTH_LONG).show();
                        //btnCriarEvento.setBackgroundColor(6029333);
                        btnCriarEvento.getBackground().setColorFilter(6029333, PorterDuff.Mode.MULTIPLY);
                        criarEventoFlag = true;
                    }
                }
            }
        });

        Button btn2 = (Button)findViewById(R.id.btn_meus_eventos);

        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent actionMeusEventos = new Intent(TelaMapaEventos.this, TelaMenuInicial.class);
                //myIntent.putExtra("key", value); //Optional parameters
                TelaMapaEventos.this.startActivity(actionMeusEventos);
            }
        });

        this.setUpMap();

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting a reference to the map
        mMap = supportMapFragment.getMap();

        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (criarEventoFlag) {
                    // Setting a click event handler for the map
                    longSelecionada = String.valueOf(latLng.longitude);
                    latSelecionada = String.valueOf(latLng.latitude);

                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (list != null & list.size() > 0) {
                        Address address = list.get(0);
                        cidade = address.getAddressLine(1);
                        endereco = address.getAddressLine(0);
                    } else {
                        cidade = "";
                        endereco = "";
                    }

                    Intent actionCriarEvento = new Intent(TelaMapaEventos.this, TelaCriarEvento.class);
                    actionCriarEvento.putExtra("long", longSelecionada);
                    actionCriarEvento.putExtra("lat", latSelecionada);
                    actionCriarEvento.putExtra("cidade", cidade);
                    actionCriarEvento.putExtra("endereco", endereco);
                    //Optional parameters
                    TelaMapaEventos.this.startActivity(actionCriarEvento);
                } else {
                    longSelecionada = String.valueOf(latLng.longitude);
                    latSelecionada = String.valueOf(latLng.latitude);

                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (list != null & list.size() > 0) {
                        Address address = list.get(0);
                        cidade = address.getAddressLine(1);
                        endereco = address.getAddressLine(0);
                    } else {
                        cidade = "";
                        endereco = "";
                    }

                    TextView t = (TextView) findViewById(R.id.lat_long_text);
                    t.setText("Localização selecionada!");
                }
            }
        });
    }


    public void geoLocate (View v) throws IOException {
        hideSoftKeyboard(v);

        EditText et = (EditText) findViewById(R.id.local_procurado);
        String location = et.getText().toString();

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

    //Configuracoes iniciais do maps
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
            this.goToLocation(myLocation.getLatitude(), myLocation.getLongitude());
            LatLng target = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            CameraPosition position = this.mMap.getCameraPosition();

            CameraPosition.Builder builder = new CameraPosition.Builder();
            builder.zoom(15);
            builder.target(target);

            this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(builder.build()));
            //mMap.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude())).title("You are here!").snippet("Consider yourself located"));
        }

        mMap.setMyLocationEnabled(true);

        //Ação realizada ao clicar em um marcador do mapa representando evento existente .
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marcador) {
                //Coordenadas "x" e "y" da janela pop-up a ser aberta ao clicar nos marcadores do mapa.
                //valores sao definidos a partir do tamanho da tela do aparelho.
                Point pontoPopUp = new Point(screenWidth/15, (int) (screenHeight/7.5));

                showPopup(TelaMapaEventos.this, pontoPopUp, (int) (screenWidth/1.1),
                        (int) (screenHeight/3.5), marcador.getTitle(), marcador.getSnippet());
                return true;
            }
        });
    }


        class LoadAllEvents extends AsyncTask<String, String, String> {
        private JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaMapaEventos.this);
            pDialog.setMessage("Loading events. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            json = jsonParser.makeHttpRequest(url_all_events, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Products: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // retornando json array de eventos
                    events = json.getJSONArray(TAG_EVENTS);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // percorrendo todos os eventos
            for (int i = 0; i < events.length(); i++) {
                JSONObject c = null;
                try {
                    c = events.getJSONObject(i);
                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String lat = c.getString(TAG_LAT);
                    String lng = c.getString(TAG_LONG);

                    Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))).title(name).snippet(id));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Criacao das janelas pop-ups ao clicar em um evento do mapa
    private void showPopup(final Activity context, Point p, int width, int height, String eventoTitulo, String evento_id) {
        int popupWidth = width;
        int popupHeight = height;

        eventoId = evento_id;
        //LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_layout, null);
        TextView titulo_evento = (TextView) layout.findViewById(R.id.txt_titulo_evento);
        titulo_evento.setText(eventoTitulo);

        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;

        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

        // Getting a reference to Close button, and close the popup when clicked.
        Button close = (Button) layout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        Button info = (Button) layout.findViewById(R.id.btn_mais_info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent actionVisualizarEvento = new Intent(TelaMapaEventos.this, TelaVisualizarEvento.class);
                actionVisualizarEvento.putExtra("evento_id", eventoId);
                TelaMapaEventos.this.startActivity(actionVisualizarEvento);
            }
        });
    }
}
