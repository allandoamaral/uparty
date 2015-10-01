package com.example.allandoamaralalves.upartyproject;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class TelaVisualizarEvento extends AppCompatActivity {

    private String eventoId;
    protected TextView txtTitulo, txtDescricao, txtEndereco, txtData, txtCidade, txtLatitude, txtLongitude;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private JSONArray evento = null;

    // url da consulta ao banco via servidor externo / uso de php e json
    private static String url_event_details = "http://uparty.3eeweb.com/db_retornar_evento.php";

    private static final String TAG_ID = "evento_id";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENT = "evento";
    private static final String TAG_NAME = "evento_titulo";
    private static final String TAG_DESCRIPTION = "evento_descricao";
    private static final String TAG_ADDRESS = "evento_endereco";
    private static final String TAG_CITY = "evento_cidade";
    private static final String TAG_DATE = "evento_data_hora";
    private static final String TAG_LONG = "evento_longitude";
    private static final String TAG_LAT = "evento_latitude";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_evento);

        // buscando o parametro evento_id enviado pela tela anterior
        Intent i = getIntent();
        eventoId = i.getStringExtra(TAG_ID);

        new GetEventDetails().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tela_visualizar_evento, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Background Async Task para retornar informacoes do evento
     * */
    class GetEventDetails extends AsyncTask<String, String, String> {
        private JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaVisualizarEvento.this);
            pDialog.setMessage("Procurando Evento no banco de dados...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Getting product details in background thread
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("evento_id", eventoId));
            // getting JSON string from URL
            json = jsonParser.makeHttpRequest(url_event_details, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Event: ", json.toString());
            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // retornando json array de eventos
                    evento = json.getJSONArray(TAG_EVENT);
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
            // dismiss the dialog once got all details
            pDialog.dismiss();
            // product with this pid found
            // Edit Text
            txtTitulo = (TextView) findViewById(R.id.txt_titulo);
            txtDescricao = (TextView) findViewById(R.id.txt_descricao);
            txtEndereco = (TextView) findViewById(R.id.txt_endereco);
            txtCidade = (TextView) findViewById(R.id.txt_cidade);
            txtData = (TextView) findViewById(R.id.txt_data);
            txtLongitude = (TextView) findViewById(R.id.txt_longitude);
            txtLatitude = (TextView) findViewById(R.id.txt_latitude);

            JSONObject c = null;
            try {
                c = evento.getJSONObject(0);
                // display product data in EditText
                txtTitulo.setText(c.getString(TAG_NAME));
                txtDescricao.setText(c.getString(TAG_DESCRIPTION));
                txtEndereco.setText(c.getString(TAG_ADDRESS));
                txtCidade.setText(c.getString(TAG_CITY));
                txtData.setText(c.getString(TAG_DATE));
                txtLongitude.setText(c.getString(TAG_LONG));
                txtLatitude.setText(c.getString(TAG_LAT));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
