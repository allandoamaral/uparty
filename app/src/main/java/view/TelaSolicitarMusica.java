package view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allandoamaralalves.upartyproject.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import controller.EventoDAO;
import controller.PedidoMusicaDAO;
import model.Evento;
import model.JSONParser;
import model.PedidoMusica;

public class TelaSolicitarMusica extends AppCompatActivity {

    private ProgressDialog pDialog;

    private static final String TAG_EVENTO_ID = "evento_id";
    JSONParser jsonParser = new JSONParser();

    private static String url_get_djs = "http://uparty.3eeweb.com/db_retornar_djs.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_DJS = "djs";
    // djs JSONArray
    private JSONArray djs = null;

    //Lista de opcoes para o combobox de selecao do dj da festa
    private final List<String> listDjs = new ArrayList<String>();
    //Lista de ids dos usuarios djs referentes aos nomes da lista 'listDjs'
    private final List<String> listDjsIds = new ArrayList<String>();

    private PedidoMusica pedidoObj = new PedidoMusica();
    private String eventoId, usuarioId, djId;
    private EditText txt_musica, txt_artista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_musica);

        // buscando o parametro evento_id enviado pela tela anterior
        Intent i = getIntent();
        eventoId = i.getStringExtra(TAG_EVENTO_ID);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        usuarioId = sharedPref.getString("usuario_id", "");

        // Campos de texto do formulario
        txt_musica = (EditText)findViewById(R.id.txt_titulo_musica);
        txt_artista = (EditText)findViewById(R.id.txt_artista);

        TextView txtInfo = (TextView) findViewById(R.id.text_info);
        txtInfo.setText("Info: ID " + usuarioId + " / Evento " + eventoId);

        new LoadDjs().execute();

        Button btnSolicitar = (Button)findViewById(R.id.btn_solicitar);
        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //retornando numero do item da lista de nomes de djs para
                // buscar o id correspondente a ele na outra lista
                Spinner djSpinner=(Spinner) findViewById(R.id.spinner_dj);
                Integer itemPosition = djSpinner.getSelectedItemPosition();
                djId = listDjsIds.get(itemPosition);
                pedidoObj.setDjId(Integer.parseInt(djId));
                pedidoObj.setEventoId(Integer.parseInt(eventoId));
                pedidoObj.setUsuarioId(Integer.parseInt(usuarioId));
                pedidoObj.setArtista(txt_artista.getText().toString());
                pedidoObj.setMusica(txt_musica.getText().toString());

                InsertNewPedido thread = new InsertNewPedido();
                thread.execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_solicitar_musica, menu);
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

    class LoadDjs extends AsyncTask<String, String, String> {
        private JSONObject json;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaSolicitarMusica.this);
            pDialog.setMessage("Loading djs. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_EVENTO_ID, eventoId));
            // getting JSON string from URL
            json = jsonParser.makeHttpRequest(url_get_djs, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All Djs: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // retornando json array de eventos
                    djs = json.getJSONArray(TAG_DJS);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            // percorrendo todos os eventos
            if (djs != null) {
                for (int i = 0; i < djs.length(); i++) {
                    JSONObject c = null;
                    try {
                        c = djs.getJSONObject(i);
                        listDjsIds.add(c.getString("usuariodj_id"));
                        listDjs.add(c.getString("usuario_nome") + " (" + c.getString("usuario_username") + ")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Spinner sp=(Spinner) findViewById(R.id.spinner_dj);
                ArrayAdapter<String> adapter;
                ArrayAdapter<String> adp= new ArrayAdapter<String> (TelaSolicitarMusica.this,
                        android.R.layout.simple_list_item_1,listDjs);
                adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sp.setAdapter(adp);
            }
        }
    }

    class InsertNewPedido extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog *
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaSolicitarMusica.this);
            pDialog.setMessage("Cadastrando novo pedido...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            PedidoMusicaDAO dao = new PedidoMusicaDAO();
            String resultado = dao.inserirEvento(pedidoObj);
            return resultado;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String result) {
            // dismiss the dialog once product uupdated
            if(result.equalsIgnoreCase("1")){
                Toast.makeText(getApplicationContext(), "Novo pedido salvo...", Toast.LENGTH_LONG).show();
                Intent actionLogin = new Intent(TelaSolicitarMusica.this, TelaMapaEventos.class);
                TelaSolicitarMusica.this.startActivity(actionLogin);
            } else {
                Toast.makeText(getApplicationContext(), "Erro no cadastro!", Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
            finish();
        }
    }
}
