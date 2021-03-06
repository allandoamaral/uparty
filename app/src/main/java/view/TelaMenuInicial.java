package view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.allandoamaralalves.upartyproject.R;

public class TelaMenuInicial extends AppCompatActivity {

    //Valor salvo do usuario logado no Shared Preferences (session)
    private String prefId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        prefId = sharedPref.getString("usuario_id", "");

        Button btn = (Button)findViewById(R.id.btn_main_eventos);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent actionEventos = new Intent(TelaMenuInicial.this, TelaMapaEventos.class);
                //myIntent.putExtra("key", value); //Optional parameters
                TelaMenuInicial.this.startActivity(actionEventos);
            }
        });

        Button btn2 = (Button)findViewById(R.id.btn_main_conta);

        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent actionConta = new Intent(TelaMenuInicial.this, TelaMinhaConta.class);
                //myIntent.putExtra("key", value); //Optional parameters
                TelaMenuInicial.this.startActivity(actionConta);
            }
        });

        if (prefId != "") {
            Button btn3 = (Button) findViewById(R.id.btn_meus_eventos);

            btn3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent actionEventos = new Intent(TelaMenuInicial.this, TelaMeusEventos.class);
                    //myIntent.putExtra("key", value); //Optional parameters
                    TelaMenuInicial.this.startActivity(actionEventos);
                }
            });

            btn3.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_inicial, menu);
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
