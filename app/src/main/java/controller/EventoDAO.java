package controller;

import android.util.Log;

import model.Evento;
import model.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    private JSONParser jsonParser = new JSONParser();
    private JSONObject json;

    // url para inserir novo evento no banco
    private static String url_insert_new = "http://uparty.3eeweb.com/db_inserir_evento.php";
    private static String url_event_details = "http://uparty.3eeweb.com/db_retornar_evento.php";

    // JSON tags
    private String success = "0";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENT = "evento";

    //Tags para os campos da tabela Eventos no bando de dados
    private static final String TAG_ID = "evento_id";
    private static final String TAG_TITULO = "evento_titulo";
    private static final String TAG_DESC = "evento_descricao";
    private static final String TAG_CIDADE = "evento_cidade";
    private static final String TAG_ENDERECO = "evento_endereco";
    private static final String TAG_DATA = "evento_data_hora";
    private static final String TAG_LNG = "evento_longitude";
    private static final String TAG_LAT = "evento_latitude";

    private JSONArray eventoJson = null;
    private Evento eventoObj;

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

    public Evento buscarEvento(int eventoId) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_ID, String.valueOf(eventoId)));
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_event_details, "GET", params);
        eventoObj = new Evento();
        // Check your log cat for JSON reponse
        Log.d("Event: ", json.toString());
        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                // retornando json array de eventos
                eventoJson = json.getJSONArray(TAG_EVENT);
                JSONObject c = eventoJson.getJSONObject(0);
                // set resultado em objeto evento
                eventoObj.setId(Integer.parseInt(c.getString(TAG_ID)));
                eventoObj.setTitulo(c.getString(TAG_TITULO));
                eventoObj.setDescricao(c.getString(TAG_DESC));
                eventoObj.setEndereco(c.getString(TAG_ENDERECO));
                eventoObj.setCidade(c.getString(TAG_CIDADE));
                eventoObj.setDataString(c.getString(TAG_DATA));
                eventoObj.setLongitude(Double.parseDouble(c.getString(TAG_LNG)));
                eventoObj.setLatitude(Double.parseDouble(c.getString(TAG_LAT)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventoObj;
    }
}