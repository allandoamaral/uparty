package com.example.allandoamaralalves.upartyproject;

import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CriarUsuario extends AppCompatActivity implements OnClickListener {

    // Progress Dialog
    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();
    private EditText txtusername;
    private EditText txtpassword;
    private Button btnsavenew;
    private int success; //to determine JSON signal insert success/fail

    // url to insert new idiom (change accordingly)
    private static String url_insert_new = "http://uparty.3eeweb.com/insertnew.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_usuario);

        // Edit Text
        txtusername = (EditText)findViewById(R.id.textusername);
        txtpassword = (EditText)findViewById(R.id.textpassword);

        // Save button
        btnsavenew = (Button) findViewById(R.id.btnsavenew);
        // button click event
        btnsavenew.setOnClickListener(this);
    }

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
            pDialog = new ProgressDialog(CriarUsuario.this);
            pDialog.setMessage("Saving the new USER...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            //capture values from EditText
            String username = txtusername.getText().toString();
            String password = txtpassword.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));

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

            return null;
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