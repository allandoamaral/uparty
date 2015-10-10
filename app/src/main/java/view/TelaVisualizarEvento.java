package view;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.allandoamaralalves.upartyproject.R;

import controller.EventoDAO;
import controller.UsuarioDAO;
import model.Evento;
import model.JSONParser;

public class TelaVisualizarEvento extends AppCompatActivity {

    //Valores salvos no Shared Preferences (session)
    private String prefId;

    private String eventoId, criadorId;
    protected TextView txtTitulo, txtDescricao, txtEndereco, txtData, txtHora, txtCidade;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();
    private JSONArray evento = null;
    private Evento eventoObj;
    private boolean participante = false;

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

        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        prefId = sharedPref.getString("usuario_id", "");

        new GetEventDetails().execute();
    }

    public void setBotoes() {
        //se o usuario acessando a tela for o criador do evento
        if (Integer.parseInt(prefId) == Integer.parseInt(criadorId)) {
            System.out.println("CRIADOR - " + criadorId + " PREF - " + prefId);
            Button btnDjs = (Button)findViewById(R.id.btn_gerenciar_djs);

            btnDjs.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent actionDjs = new Intent(TelaVisualizarEvento.this, TelaSelecionarDjs.class);
                    //setar valor eventoId para a proxima tela
                    actionDjs.putExtra(TAG_ID, eventoId);
                    TelaVisualizarEvento.this.startActivity(actionDjs);
                }
            });

            btnDjs.setVisibility(View.VISIBLE);
        } else if (participante) {
            Button btnPedido = (Button)findViewById(R.id.btn_pedido);

            btnPedido.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent actionSolicitarMusica = new Intent(TelaVisualizarEvento.this, TelaSolicitarMusica.class);
                    //setar valor eventoId para a proxima tela
                    actionSolicitarMusica.putExtra(TAG_ID, eventoId);
                    TelaVisualizarEvento.this.startActivity(actionSolicitarMusica);
                }
            });

            btnPedido.setVisibility(View.VISIBLE);
        } else {
            Button btnParticipar = (Button)findViewById(R.id.btn_participar);

            btnParticipar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    new InsertParticipation().execute();
                    Intent i = new Intent(TelaVisualizarEvento.this, TelaVisualizarEvento.class);
                    i.putExtra("evento_id", eventoId);
                    startActivity(i);
                }
            });

            btnParticipar.setVisibility(View.VISIBLE);
        }
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
            participante = dao.isParticipante(prefId, eventoId);
            return null;
        }

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
            criadorId = String.valueOf(eventoObj.getCriador_id());
            TelaVisualizarEvento.this.setBotoes();
        }
    }

    class InsertParticipation extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaVisualizarEvento.this);
            pDialog.setMessage("Atualizando evento...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            UsuarioDAO dao = new UsuarioDAO();
            dao.inserirParticipanteEmEvento(prefId, eventoId);
            return null;
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            finish();
        }
    }
}
