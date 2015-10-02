package com.example.allandoamaralalves.upartyproject;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    JSONParser jsonParser = new JSONParser();

    // url para inserir novo evento no banco
    private static String url_insert_new = "http://uparty.3eeweb.com/db_inserir_evento.php";

    // JSON Node names
    private String success = "0";
    private static final String TAG_SUCCESS = "success";

    //Tags para os campos da tabela Eventos no bando de dados
    private static final String TAG_TITULO = "evento_nome";
    private static final String TAG_DESC = "evento_descricao";
    private static final String TAG_CIDADE = "evento_cidade";
    private static final String TAG_ENDERECO = "evento_endereco";
    private static final String TAG_DATA = "evento_data_hora";
    private static final String TAG_LNG = "evento_longitude";
    private static final String TAG_LAT = "evento_latitude";

    public String inserirEvento(Evento evento) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_TITULO, evento.getTitulo()));
        params.add(new BasicNameValuePair(TAG_DESC, evento.getDescricao()));
        params.add(new BasicNameValuePair(TAG_CIDADE, evento.getCidade()));
        params.add(new BasicNameValuePair(TAG_ENDERECO, evento.getEndereco()));
        params.add(new BasicNameValuePair(TAG_DATA,
                evento.getData_hora().getYear() + "-" + evento.getData_hora().getMonth() + "-"
                        + evento.getData_hora().getDay() + " " + evento.getData_hora().getHours()
                        + ":" + evento.getData_hora().getMinutes()));
        params.add(new BasicNameValuePair(TAG_LNG, String.valueOf(evento.getLongitude())));
        params.add(new BasicNameValuePair(TAG_LAT, String.valueOf(evento.getLatitude())));

        // sending modified data through http request
        // Notice that update product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_insert_new,
                "GET", params);

        // check log cat fro response
        Log.d("Create Response", json.toString());

        // check json success tag
        try {
            success = json.getString(TAG_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return success;
    }
}
