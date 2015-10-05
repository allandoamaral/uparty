package view;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.allandoamaralalves.upartyproject.R;

import controller.EventoDAO;
import model.Evento;
import model.JSONParser;

public class TelaVisualizarEvento extends AppCompatActivity {

    private String eventoId;
    protected TextView txtTitulo, txtDescricao, txtEndereco, txtData, txtHora, txtCidade;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private JSONArray evento = null;
    private Evento eventoObj;

    private static final String TAG_ID = "evento_id";
    private static final String TAG_SUCCESS = "success";
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

        protected String doInBackground(String... args) {
            EventoDAO dao = new EventoDAO();
            eventoObj = dao.buscarEvento(Integer.parseInt(eventoId));
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog
            pDialog.dismiss();
            // Edit Text
            txtTitulo = (TextView) findViewById(R.id.txt_titulo);
            txtDescricao = (TextView) findViewById(R.id.txt_descricao);
            txtEndereco = (TextView) findViewById(R.id.txt_endereco);
            txtCidade = (TextView) findViewById(R.id.txt_cidade);
            txtData = (TextView) findViewById(R.id.txt_data);
            txtHora = (TextView) findViewById(R.id.txt_hora);

            txtTitulo.setText(eventoObj.getTitulo());
            txtDescricao.setText(eventoObj.getDescricao());
            txtEndereco.setText(eventoObj.getEndereco());
            txtCidade.setText(eventoObj.getCidade());
            txtData.setText(eventoObj.getDataString().substring(5, 7) + "/" +
                    eventoObj.getDataString().substring(8, 10) + "/" + eventoObj.getDataString().substring(0, 4));
            txtHora.setText(eventoObj.getDataString().substring(10, 16));
        }
    }
}
