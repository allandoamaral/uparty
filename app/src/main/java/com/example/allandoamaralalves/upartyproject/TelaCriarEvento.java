package com.example.allandoamaralalves.upartyproject;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import android.widget.TimePicker;

public class TelaCriarEvento extends AppCompatActivity {
    // Progress Dialog
    private ProgressDialog pDialog;

    //Campos de texto
    private EditText txtnome, txtdescricao, txtcidade, txtendereco;
    private String latitude, longitude;

    Button btn_date, btn_time;
    //Valores da data do evento
    int event_year, event_month, event_day, event_hour, event_minute;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_evento);

        // Campos de texto do formulario
        txtnome = (EditText)findViewById(R.id.txtnome);
        txtdescricao = (EditText)findViewById(R.id.txtdescricao);

        //get extra parameters - Latitude e longitude
        Bundle extraParams = getIntent().getExtras();

        txtcidade = (EditText)findViewById(R.id.txtcidade);
        txtcidade.setText(extraParams.getString("cidade"));

        txtendereco = (EditText)findViewById(R.id.txtendereco);
        txtendereco.setText(extraParams.getString("endereco"));

        latitude = extraParams.getString("lat");
        longitude = extraParams.getString("long");

        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0)
            System.out.println(addresses.get(0).getLocality());

        //set valor latLong no campo de texto da tela
        TextView txtlatlong = (TextView)findViewById(R.id.text_latlong);
        txtlatlong.setText("Longitude: " + longitude + " / Latitude: " + latitude);

        final Calendar cal = Calendar.getInstance();
        event_year = cal.get(Calendar.YEAR);
        event_month = cal.get(Calendar.MONTH);
        event_day = cal.get(Calendar.DAY_OF_MONTH);
        showDialogOnButtonClick();

        Button btnVoltar = (Button)findViewById(R.id.btn_voltar);

        btnVoltar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent actionVoltar = new Intent(TelaCriarEvento.this, TelaMapaEventos.class);
                //myIntent.putExtra("key", value); //Optional parameters
                TelaCriarEvento.this.startActivity(actionVoltar);
            }
        });

        Button btnCriar = (Button)findViewById(R.id.btn_criar);

        btnCriar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InsertNewEvent thread = new InsertNewEvent();
                thread.execute();
            }
        });
    }

    class InsertNewEvent extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog *
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TelaCriarEvento.this);
            pDialog.setMessage("Cadastrando novo evento...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            Evento evento = new Evento();
            evento.setTitulo(txtnome.getText().toString());
            evento.setDescricao(txtdescricao.getText().toString());
            evento.setCidade(txtcidade.getText().toString());
            evento.setEndereco(txtendereco.getText().toString());
            evento.setData_hora(new Date(event_year, event_month, event_day, event_hour, event_minute));
            evento.setLatitude(Double.parseDouble(latitude));
            evento.setLongitude(Double.parseDouble(longitude));

            EventoDAO dao = new EventoDAO();

            String resultado = dao.inserirEvento(evento);
            return resultado;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String result) {
            // dismiss the dialog once product uupdated
            if(result.equalsIgnoreCase("1")){
                Toast.makeText(getApplicationContext(), "Novo evento salvo...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Erro no cadastro!", Toast.LENGTH_LONG).show();
            }
            pDialog.dismiss();
            finish();
        }
    }

    //DatePicker

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

        btn_time = (Button) findViewById(R.id.time_picker_button);

        btn_time.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(TIME_DIALOG_ID);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case (DATE_DIALOG_ID):
                return new DatePickerDialog(this, dpickerListener, event_year, event_month, event_day);
            case (TIME_DIALOG_ID):
                return new TimePickerDialog(this, mTimeSetListener, event_hour, event_minute, false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int hour_minute) {
            event_hour = hourOfDay;
            event_minute = hour_minute;
            //Toast.makeText(getBaseContext(), "Setted time: " + event_hour + event_minute, Toast.LENGTH_LONG).show();
            TextView t = (TextView)findViewById(R.id.text_hora);
            t.setText(event_hour + " : " + event_minute);
        }
    };

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            event_year = year;
            event_month = monthOfYear + 1;
            event_day = dayOfMonth;
            //Aviso na tela
            //Toast.makeText(TelaCriarEvento.this, day_x + "/" + month_x + "/" + year_x, Toast.LENGTH_LONG).show();
            TextView t = (TextView)findViewById(R.id.text_data);
            t.setText(event_day + "/" + event_month + "/" + event_year);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_criar_evento, menu);
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
}
