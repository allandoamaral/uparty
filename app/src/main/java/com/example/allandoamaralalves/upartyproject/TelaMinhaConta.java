package com.example.allandoamaralalves.upartyproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class TelaMinhaConta extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta);


        Button btnCriar = (Button)findViewById(R.id.btn_criar_usuario);

        btnCriar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent actionCriar = new Intent(TelaMinhaConta.this, TelaCriarUsuario.class);
                //myIntent.putExtra("key", value); //Optional parameters
                TelaMinhaConta.this.startActivity(actionCriar);
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

    public void actionVoltar() {
        Intent actionInicial = new Intent(TelaMinhaConta.this, TelaMenuInicial.class);
        //myIntent.putExtra("key", value); //Optional parameters
        TelaMinhaConta.this.startActivity(actionInicial);
    }
}
