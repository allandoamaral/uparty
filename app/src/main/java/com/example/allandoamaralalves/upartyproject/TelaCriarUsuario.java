package com.example.allandoamaralalves.upartyproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TelaCriarUsuario extends AppCompatActivity implements OnClickListener {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    //Campos de texto
    private EditText txtnome, txtcidade, txtuf, txtemail, txtusername, txtpassword;

    private Button btn_date, btnsavenew;

    //DatePicker variaveis
    private int year, month, day;
    private static final int DATE_DIALOG_ID = 0;

    // url para inserir novo usuario no banco
    private static String url_insert_new = "http://uparty.3eeweb.com/db_inserir_usuario.php";

    // JSON Node names
    private int success;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_usuario);

        // Campos de texto do formulario
        txtnome = (EditText)findViewById(R.id.textnome);
        txtcidade = (EditText)findViewById(R.id.textcidade);
        txtuf = (EditText)findViewById(R.id.textestado);
        txtemail = (EditText)findViewById(R.id.textemail);
        txtusername = (EditText)findViewById(R.id.textusername);
        txtpassword = (EditText)findViewById(R.id.textpassword);

        final Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        showDialogOnButtonClick();

        // Save button
        btnsavenew = (Button) findViewById(R.id.btnsavenew);
        // button click event
        btnsavenew.setOnClickListener(this);
    }

    public void showDialogOnButtonClick() {
        btn_date = (Button) findViewById(R.id.date_picker_button);

        btn_date.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DATE_DIALOG_ID);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case (DATE_DIALOG_ID):
                return new DatePickerDialog(this, dpickerListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int yearBirth, int monthOfYear, int dayOfMonth) {
            year = yearBirth;
            month = monthOfYear + 1;
            day = dayOfMonth;
            TextView t = (TextView)findViewById(R.id.text_data);
            t.setText(day + "/" + month + "/" + year);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnsavenew) {
            //call the InsertNewIdiom thread
            InsertNewUser thread = new InsertNewUser();
            thread.execute();
        }
    }

    /**
     * Background Async Task to Create new Idioms *
     */

    class InsertNewUser extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog *
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaCriarUsuario.this);
            pDialog.setMessage("Cadastrando novo usuário...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            //resgatar valores dos campos
            String nome = txtnome.getText().toString();
            String nascimento = year + "-" + month + "-" + day;
            String cidade = txtcidade.getText().toString();
            String uf = txtuf.getText().toString();
            String email = txtemail.getText().toString();
            String username = txtusername.getText().toString();
            String password = txtpassword.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("usuario_nome", nome));
            params.add(new BasicNameValuePair("usuario_nascimento", nascimento));
            params.add(new BasicNameValuePair("usuario_cidade", cidade));
            params.add(new BasicNameValuePair("usuario_uf", uf));
            params.add(new BasicNameValuePair("usuario_email", email));
            params.add(new BasicNameValuePair("usuario_username", username));
            params.add(new BasicNameValuePair("usuario_senha", password));

            // sending modified data through http request
            // Notice that update product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_insert_new,
                    "GET", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check json success tag
            try {
                success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about product update
                    //setResult(100, i);
                    return "1";
                } else {
                    // failed to update product
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "0";
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String result) {
            // dismiss the dialog once product uupdated
            if(result.equalsIgnoreCase("1")){
                Toast.makeText(getApplicationContext(), "Novo usuario salvo...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Erro no cadastro!", Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
            finish();
        }
    }
}