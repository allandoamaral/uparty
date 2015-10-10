package view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.allandoamaralalves.upartyproject.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import controller.EventoDAO;
import controller.PedidoMusicaDAO;

public class TelaPedidos extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    private HashMap<Integer, List<String>> listaPedidosEvento = new HashMap<>();

    //Valores salvos no Shared Preferences (session)
    private String prefNome, prefId, eventoId;

    private PedidoMusicaDAO dao = new PedidoMusicaDAO();

    private static final String TAG_EVENTO_ID = "evento_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        // buscando o parametro evento_id enviado pela tela anterior
        Intent i = getIntent();
        eventoId = i.getStringExtra(TAG_EVENTO_ID);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        prefNome = sharedPref.getString("usuario_nome", "");
        prefId = sharedPref.getString("usuario_id", "");

        new LoadRequests().execute();
    }

    public void setResultado() {
        // Layout geral
        LinearLayout lm = (LinearLayout)findViewById(R.id.layout_resultado);
        //percorrer lista de usuarios retornados da busca e exibir na tela
        for (int i = 0; i < listaPedidosEvento.size(); i++) {
            // layout de cada item
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            // Create TextView
            TextView itemLista;
            for (int j = 1; j < 6; j++) {
                itemLista = new TextView(this);
                itemLista.setText(listaPedidosEvento.get(i).get(j));
                ll.addView(itemLista);
            }
            // Create Button
            /*final Button btn = new Button(this);
            final String idEvento = listaEventosCriados.get(i).get(0);
            btn.setText("+ Info");
            // Set click listener for button
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent actionVisualizarEvento = new Intent(TelaMeusEventos.this, TelaVisualizarEvento.class);
                    actionVisualizarEvento.putExtra("evento_id", idEvento);
                    TelaMeusEventos.this.startActivity(actionVisualizarEvento);
                }
            });

            //Add button to LinearLayout
            ll.addView(btn);*/
            //Adicionar layout item no layout geral
            lm.addView(ll);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pedidos, menu);
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

    class LoadRequests extends AsyncTask<String, String, String> {
        private JSONObject json;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaPedidos.this);
            pDialog.setMessage("Carregando pedidos...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            listaPedidosEvento = dao.getPedidosEvento(prefId, eventoId);
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            TelaPedidos.this.setResultado();
        }
    }
}
