package view;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allandoamaralalves.upartyproject.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import controller.EventoDAO;
import controller.UsuarioDAO;
import model.Evento;
import view.TelaVisualizarEvento;

public class TelaSelecionarDjs extends AppCompatActivity {

    EditText txt_nome_usuario;

    // Progress Dialog
    private ProgressDialog pDialog;

    private static final String TAG_EVENTO_ID = "evento_id";

    private String nomePesquisado = "";
    private HashMap<Integer, List<String>> listaUsuarios = new HashMap<>();
    private List<String> listaUsuariosAdicionados = new ArrayList<>();
    private HashMap<String, String> infoUsuariosAdicionados = new HashMap<>();
    private String eventoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecionar_djs);

        // buscando o parametro evento_id enviado pela tela anterior
        Intent i = getIntent();
        eventoId = i.getStringExtra(TAG_EVENTO_ID);

        txt_nome_usuario = (EditText) findViewById(R.id.txt_nome_usuario);

        Button btnPesquisar = (Button)findViewById(R.id.btn_pesquisar);
        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                nomePesquisado = txt_nome_usuario.getText().toString();
                GetUsersByName thread = new GetUsersByName();
                thread.execute();
            }
        });

        Button btnInserir = (Button)findViewById(R.id.btn_definir_djs);
        btnInserir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InsertDjs thread = new InsertDjs();
                thread.execute();
            }
        });

        new LoadDjs().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_selecionar_djs, menu);
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

    public void setResultSearch() {
        // Layout geral
        LinearLayout lm = (LinearLayout)findViewById(R.id.layout_result);
        ((LinearLayout) lm).removeAllViews();
        //percorrer lista de usuarios retornados da busca e exibir na tela
        for (int i = 0; i < listaUsuarios.size(); i++) {
            //variaveis para id e nome do usuario percorrido
            final String idUser = listaUsuarios.get(i).get(0);
            final String nomeUser = listaUsuarios.get(i).get(1);

            if (!listaUsuariosAdicionados.contains(idUser)) {
                // layout de cada item
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);

                // Create TextView
                TextView itemUsuario = new TextView(this);
                itemUsuario.setText(listaUsuarios.get(i).get(1));
                ll.addView(itemUsuario);

                // Create Button
                final Button btn = new Button(this);

                btn.setText("+");
                // Set click listener for button
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listaUsuariosAdicionados.add(idUser);
                        infoUsuariosAdicionados.put(idUser, nomeUser);
                        listaUsuariosAdicionados.toString();
                        TelaSelecionarDjs.this.setSelectedUsers();
                        TelaSelecionarDjs.this.setResultSearch();
                    }
                });

                //Add button to LinearLayout
                ll.addView(btn);

                //Adicionar layout item no layout geral
                lm.addView(ll);
            }
        }
    }

    public void setSelectedUsers() {
        // Layout geral
        LinearLayout ls = (LinearLayout)findViewById(R.id.layout_selected);
        ((LinearLayout) ls).removeAllViews();

        //percorrer lista de usuarios retornados da busca e exibir na tela
        for (int i = 0; i < listaUsuariosAdicionados.size(); i++) {
            // layout de cada item
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);

            //Id do usuario
            final String index = listaUsuariosAdicionados.get(i);

            // Create TextView
            TextView itemUsuario = new TextView(this);
            itemUsuario.setText(infoUsuariosAdicionados.get(index));
            ll.addView(itemUsuario);

            // Create Button
            final Button btn = new Button(this);

            btn.setText("-");
            // Set click listener for button
            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    listaUsuariosAdicionados.remove(index);
                    TelaSelecionarDjs.this.setResultSearch();
                    TelaSelecionarDjs.this.setSelectedUsers();
                }
            });

            //Add button to LinearLayout
            ll.addView(btn);

            //Adicionar layout item no layout geral
            ls.addView(ll);
        }
    }

    class GetUsersByName extends AsyncTask<String, String, String> {
        private JSONObject json;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaSelecionarDjs.this);
            pDialog.setMessage("Procurando Evento no banco de dados...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            UsuarioDAO dao = new UsuarioDAO();
            listaUsuarios = dao.pesquisarUsuario(nomePesquisado);
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog
            pDialog.dismiss();
            TelaSelecionarDjs.this.setResultSearch();
        }
    }

    class LoadDjs extends AsyncTask<String, String, String> {
        private JSONObject json;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaSelecionarDjs.this);
            pDialog.setMessage("Loading djs. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            UsuarioDAO dao = new UsuarioDAO();
            HashMap<Integer, List<String>> djsEventoResult = dao.getDjsEvento(eventoId);
            if (djsEventoResult != null) {
                for (int i = 0; i < djsEventoResult.size(); i++) {
                    listaUsuariosAdicionados.add(djsEventoResult.get(i).get(0));
                    infoUsuariosAdicionados.put(djsEventoResult.get(i).get(0), djsEventoResult.get(i).get(1));
                }
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            TelaSelecionarDjs.this.setSelectedUsers();
        }
    }

    class InsertDjs extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog *
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaSelecionarDjs.this);
            pDialog.setMessage("Atualizando evento...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            UsuarioDAO dao = new UsuarioDAO();
            for (int i = 0; i < listaUsuariosAdicionados.size(); i++) {
                dao.inserirDjEmEvento(listaUsuariosAdicionados.get(i), eventoId);
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            finish();
        }
    }
}
