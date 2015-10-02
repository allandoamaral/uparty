package com.example.allandoamaralalves.upartyproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    //Campos de texto
    private EditText txtnome, txtcidade, txtuf, txtemail, txtusername, txtpassword;

    private Button btn_date, btnsavenew;

    //DatePicker variaveis
    private int year, month, day;
    private static final int DATE_DIALOG_ID = 0;

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
            //criar usuario
            Usuario user = new Usuario();

            //resgatar valores dos campos
            user.setNome(txtnome.getText().toString());
            user.setDataNascimento(new Date(year, month, day));
            user.setCidade(txtcidade.getText().toString());
            user.setUf(txtuf.getText().toString());
            user.setEmail(txtemail.getText().toString());
            user.setNomeUsuario(txtusername.getText().toString());
            user.setSenha(txtpassword.getText().toString());
            
            UsuarioDAO dao = new UsuarioDAO();
            
            int resultado = dao.inserirUsuario(user);
            return (String.valueOf(resultado));
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