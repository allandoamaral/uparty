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
import android.widget.Toast;

import com.example.allandoamaralalves.upartyproject.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import controller.PedidoMusicaDAO;

public class TelaPedidosEvento extends AppCompatActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    private HashMap<Integer, List<String>> listaPedidosEvento = new HashMap<>();

    //Valores salvos no Shared Preferences (session)
    private String prefNome, prefId, eventoId, pedidoId;

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

            LinearLayout l1 = new LinearLayout(this);
            l1.setOrientation(LinearLayout.VERTICAL);
            // Create TextView
            TextView itemLista;

            itemLista = new TextView(this);
            itemLista.setText("Artista - " + listaPedidosEvento.get(i).get(1));
            l1.addView(itemLista);

            itemLista = new TextView(this);
            itemLista.setText("Música - " + listaPedidosEvento.get(i).get(2));
            l1.addView(itemLista);

            itemLista = new TextView(this);
            itemLista.setText("Hora do pedido - " + listaPedidosEvento.get(i).get(3));
            l1.addView(itemLista);

            itemLista = new TextView(this);
            itemLista.setText("Pedido por - " + listaPedidosEvento.get(i).get(5));
            l1.addView(itemLista);

            LinearLayout l2 = new LinearLayout(this);
            l2.setOrientation(LinearLayout.VERTICAL);

            final String id = listaPedidosEvento.get(i).get(0);
            final Button btnAcc = new Button(this);
            btnAcc.setText("Aceitar");
            btnAcc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pedidoId = id;
                    new AcceptPedido().execute();
                }
            });
            l2.addView(btnAcc);

            final Button btnRec = new Button(this);
            btnRec.setText("Recusar");
            btnRec.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    pedidoId = id;
                    new RecusarPedido().execute();
                }
            });
            l2.addView(btnRec);

            ll.addView(l1);
            ll.addView(l2);

            //Adicionar layout item no layout geral
            lm.addView(ll);
        }

        if (listaPedidosEvento.isEmpty()) {
            TextView text = new TextView(this);
            text.setText("Não há novos pedidos realizados para este DJ/evento.");
            lm.addView(text);
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
            pDialog = new ProgressDialog(TelaPedidosEvento.this);
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
            TelaPedidosEvento.this.setResultado();
        }
    }

    class AcceptPedido extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaPedidosEvento.this);
            pDialog.setMessage("Aceitando pedido...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            PedidoMusicaDAO dao = new PedidoMusicaDAO();
            String resultado = dao.aceitarPedido(pedidoId);
            return resultado;
        }

        protected void onPostExecute(String result) {
            // dismiss the dialog once product uupdated
            if(result.equalsIgnoreCase("1")){
                Toast.makeText(getApplicationContext(), "Pedido aceito...", Toast.LENGTH_LONG).show();
                Intent actionPedidos = new Intent(TelaPedidosEvento.this, TelaPedidosEvento.class);
                actionPedidos.putExtra("evento_id", eventoId);
                TelaPedidosEvento.this.startActivity(actionPedidos);
            } else {
                Toast.makeText(getApplicationContext(), "Erro no cadastro!", Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
            finish();
        }
    }

    class RecusarPedido extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaPedidosEvento.this);
            pDialog.setMessage("Recusando pedido...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            PedidoMusicaDAO dao = new PedidoMusicaDAO();
            String resultado = dao.recusarPedido(pedidoId);
            return resultado;
        }

        protected void onPostExecute(String result) {
            if(result.equalsIgnoreCase("1")){
                Toast.makeText(getApplicationContext(), "Pedido recusado...", Toast.LENGTH_LONG).show();
                Intent actionPedidos = new Intent(TelaPedidosEvento.this, TelaPedidosEvento.class);
                actionPedidos.putExtra("evento_id", eventoId);
                TelaPedidosEvento.this.startActivity(actionPedidos);
            } else {
                Toast.makeText(getApplicationContext(), "Erro no cadastro!", Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
            finish();
        }
    }
}
