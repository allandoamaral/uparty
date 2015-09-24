package com.example.allandoamaralalves.upartyproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class CriarEvento extends AppCompatActivity {
    Button btn_date, btn_time;
    //Valores da data do evento
    int event_year, event_month, event_day, event_hour, event_minute;
    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_evento);

        final Calendar cal = Calendar.getInstance();
        event_year = cal.get(Calendar.YEAR);
        event_month = cal.get(Calendar.MONTH);
        event_day = cal.get(Calendar.DAY_OF_MONTH);
        showDialogOnButtonClick();

        Button btn = (Button)findViewById(R.id.btn_voltar);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent actionVoltar = new Intent(CriarEvento.this, MapaEventos.class);
                //myIntent.putExtra("key", value); //Optional parameters
                CriarEvento.this.startActivity(actionVoltar);
            }
        });
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
            //Toast.makeText(CriarEvento.this, day_x + "/" + month_x + "/" + year_x, Toast.LENGTH_LONG).show();
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
