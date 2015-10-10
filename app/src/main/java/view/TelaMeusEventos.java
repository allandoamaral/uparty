package view;

import android.app.ProgressDialog;
import android.app.TabActivity;
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
import android.widget.TabHost;
import android.widget.TextView;

import com.example.allandoamaralalves.upartyproject.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import controller.EventoDAO;
import controller.UsuarioDAO;

public class TelaMeusEventos extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    private HashMap<Integer, List<String>> listaEventosCriados = new HashMap<>();
    private HashMap<Integer, List<String>> listaEventosDj = new HashMap<>();
    private HashMap<Integer, List<String>> listaEventosPartic = new HashMap<>();

    //Valores salvos no Shared Preferences (session)
    private String prefNome, prefId;

    private EventoDAO dao = new EventoDAO();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_eventos);

        TabHost tab = (TabHost) findViewById(R.id.tabHost);
        tab.setup();

        TabHost.TabSpec spec = tab.newTabSpec("criados");
        spec.setContent(R.id.layout_criados);
        spec.setIndicator("Criador");
        tab.addTab(spec);

        spec = tab.newTabSpec("dj");
        spec.setContent(R.id.layout_dj);
        spec.setIndicator("Dj");
        tab.addTab(spec);

        spec = tab.newTabSpec("participante");
        spec.setContent(R.id.layout_partic);
        spec.setIndicator("Irei");
        tab.addTab(spec);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        prefNome = sharedPref.getString("usuario_nome", "");
        prefId = sharedPref.getString("usuario_id", "");

        new LoadEvents().execute();
    }

    public void setEventos() {
        // Layout geral
        LinearLayout lm = (LinearLayout)findViewById(R.id.layout_dj);
        //percorrer lista de usuarios retornados da busca e exibir na tela
        for (int i = 0; i < listaEventosDj.size(); i++) {
            // layout de cada item
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            // Create TextView
            TextView itemUsuario = new TextView(this);
            itemUsuario.setText(listaEventosDj.get(i).get(1));
            ll.addView(itemUsuario);
            // Create Button
            final Button btn = new Button(this);
            final String idEvento = listaEventosDj.get(i).get(0);
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
            ll.addView(btn);
            //Adicionar layout item no layout geral
            lm.addView(ll);
        }
    }

    public void setEventosCriados() {
        // Layout geral
        LinearLayout lm = (LinearLayout)findViewById(R.id.layout_criados);
        //percorrer lista de usuarios retornados da busca e exibir na tela
        for (int i = 0; i < listaEventosCriados.size(); i++) {
            // layout de cada item
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            // Create TextView
            TextView itemUsuario = new TextView(this);
            itemUsuario.setText(listaEventosCriados.get(i).get(1));
            ll.addView(itemUsuario);
            // Create Button
            final Button btn = new Button(this);
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
            ll.addView(btn);
            //Adicionar layout item no layout geral
            lm.addView(ll);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meus_eventos, menu);
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

    class LoadEvents extends AsyncTask<String, String, String> {
        private JSONObject json;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaMeusEventos.this);
            pDialog.setMessage("Carregando eventos cadastrados...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            listaEventosDj = dao.getEventosDj(prefId);
            listaEventosCriados = dao.getEventosCriados(prefId);
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            TelaMeusEventos.this.setEventos();
            TelaMeusEventos.this.setEventosCriados();
        }
    }
}
