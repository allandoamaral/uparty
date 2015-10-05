package view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.allandoamaralalves.upartyproject.R;

import org.json.JSONObject;

import controller.EventoDAO;
import controller.UsuarioDAO;
import model.Usuario;

public class TelaMinhaConta extends AppCompatActivity {

    protected static final SharedPreferences settings = null;

    protected String txtUser, txtSenha;
    private Usuario usuarioObj;

    //Valores salvos no Shared Preferences (session)
    private String prefNome, prefId;

    //Valores exibidos na tela
    protected TextView txtNome, txtId;

    // Progress Dialog
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        prefNome = sharedPref.getString("usuario_nome", "");
        prefId = sharedPref.getString("usuario_id", "");

        //No caso de haver informacoes de usuario logado na sessao SharedPref
        if (prefId != "") {
            LinearLayout layoutLogin = (LinearLayout) findViewById(R.id.login_content);
            layoutLogin.setVisibility(View.INVISIBLE);
            LinearLayout layoutAccount = (LinearLayout) findViewById(R.id.account_content);
            layoutAccount.setVisibility(View.VISIBLE);
            txtId = (TextView) findViewById(R.id.id_usuario_txt);
            txtId.setText(prefId);

            Button btnLogout = (Button)findViewById(R.id.btn_logout);

            btnLogout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("usuario_nome", "");
                    editor.putString("usuario_id", "");
                    editor.commit();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });
        }

        if (prefNome != "") {
            txtNome = (TextView) findViewById(R.id.nome_usuario_txt);
            txtNome.setText(prefNome);
        }

        Button btnCriar = (Button)findViewById(R.id.btn_criar_usuario);

        btnCriar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent actionCriar = new Intent(TelaMinhaConta.this, TelaCriarUsuario.class);
                //myIntent.putExtra("key", value); //Optional parameters
                TelaMinhaConta.this.startActivity(actionCriar);
            }
        });

        Button btnOk = (Button)findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText campoUser = (EditText)findViewById(R.id.txt_username);
                EditText campoSenha = (EditText)findViewById(R.id.txt_password);
                txtUser = campoUser.getText().toString();
                txtSenha = campoSenha.getText().toString();
                new LoginUser().execute();
            }
        });

        Button btnVoltar = (Button)findViewById(R.id.btn_conta_voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
                Toast.makeText(getApplicationContext(), sharedPref.getString("username", ""), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_minha_conta, menu);
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

    class LoginUser extends AsyncTask<String, String, String> {
        private JSONObject json;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaMinhaConta.this);
            pDialog.setMessage("Verificando Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            UsuarioDAO dao = new UsuarioDAO();
            usuarioObj = dao.logarUsuario(txtUser, txtSenha);
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog
            pDialog.dismiss();
            if (usuarioObj.getId() == 0) {
                Toast.makeText(TelaMinhaConta.this, "Usuario não encontrado!", Toast.LENGTH_LONG).show();
            } else {
                SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("usuario_nome", usuarioObj.getNome());
                editor.putString("usuario_id", String.valueOf(usuarioObj.getId()));
                editor.commit();
                Toast.makeText(TelaMinhaConta.this, "Usuário " + sharedPref.getString("usuario_nome", "") + " logado com sucesso!", Toast.LENGTH_LONG).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    }
}
