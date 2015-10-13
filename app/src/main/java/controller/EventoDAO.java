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
import java.util.HashMap;
import java.util.List;

public class EventoDAO {
    private JSONParser jsonParser = new JSONParser();
    private JSONObject json;

    // url para as operações de busca e inserção no banco de dados online
    private static String url_insert_new = "http://uparty.3eeweb.com/db_inserir_evento.php";
    private static String url_event_details = "http://uparty.3eeweb.com/db_retornar_evento.php";
    private static String url_get_events_by_dj = "http://uparty.3eeweb.com/db_retornar_eventos_dj.php";
    private static String url_get_events_created = "http://uparty.3eeweb.com/db_retornar_eventos_criados.php";
    private static String url_get_events_partic = "http://uparty.3eeweb.com/db_retornar_eventos_participante.php";
    private static String url_is_participante = "http://uparty.3eeweb.com/db_retornar_participacao.php";

    private static String url_all_events = "http://uparty.3eeweb.com/db_retornar_eventos.php";
    private static String url_actual_events = "http://uparty.3eeweb.com/db_retornar_eventos_atuais.php";

    // JSON tags
    private String success = "0";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENT = "evento";
    private static final String TAG_EVENTOS = "eventos";

    //Tags para os campos da tabela Eventos no bando de dados
    private static final String TAG_ID = "evento_id";
    private static final String TAG_TITULO = "evento_titulo";
    private static final String TAG_DESC = "evento_descricao";
    private static final String TAG_CIDADE = "evento_cidade";
    private static final String TAG_ENDERECO = "evento_endereco";
    private static final String TAG_DATA = "evento_data_hora";
    private static final String TAG_LNG = "evento_longitude";
    private static final String TAG_LAT = "evento_latitude";
    private static final String TAG_CRIADOR = "evento_criador_id";
    private static final String TAG_USER_ID = "usuario_id";

    private JSONArray eventoJson = null;
    private Evento eventoObj;

    ArrayList<HashMap<String, String>> listaEventos;

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
        params.add(new BasicNameValuePair(TAG_CRIADOR, String.valueOf(evento.getCriador_id())));

        // sending modified data through http request
        // Notice that update product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_insert_new,
                "GET", params);

        // check log cat fro response
        Log.d("Create Response", json.toString());

        // check json success tag
        try {
            if (json.getString(TAG_SUCCESS) == "1") {
                return json.getString("created_id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "0";
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
                eventoObj.setCriador_id(Integer.parseInt(c.getString(TAG_CRIADOR)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return eventoObj;
    }

    public JSONArray getTodosEventos() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_all_events, "GET", params);

        Log.d("Djs: ", json.toString());

        JSONArray events = null;

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // retornando json array de eventos
                events = json.getJSONArray(TAG_EVENTOS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return events;
    }

    public JSONArray getEventosAtuais () {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_actual_events, "GET", params);

        Log.d("Djs: ", json.toString());

        JSONArray events = null;

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);

            if (success == 1) {
                // retornando json array de eventos
                events = json.getJSONArray(TAG_EVENTOS);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return events;
    }

    public HashMap<Integer, List<String>> getEventosDj(String usuarioId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_USER_ID, usuarioId));
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_get_events_by_dj, "GET", params);

        HashMap<Integer, List<String>> listaEventos = new HashMap<>();
        Log.d("Djs: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                // retornando json array de eventos
                eventoJson = json.getJSONArray(TAG_EVENTOS);
                for (int i = 0; i < eventoJson.length(); i++) {
                    JSONObject c = null;
                    c = eventoJson.getJSONObject(i);
                    List<String> tempList = new ArrayList<>();
                    tempList.add(c.getString(TAG_ID));
                    tempList.add(c.getString(TAG_TITULO));
                    listaEventos.put(i, tempList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaEventos;
    }

    public HashMap<Integer, List<String>> getEventosCriados(String usuarioId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_USER_ID, usuarioId));
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_get_events_created, "GET", params);

        HashMap<Integer, List<String>> listaEventos = new HashMap<>();
        Log.d("Djs: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                // retornando json array de eventos
                eventoJson = json.getJSONArray(TAG_EVENTOS);
                for (int i = 0; i < eventoJson.length(); i++) {
                    JSONObject c = null;
                    c = eventoJson.getJSONObject(i);
                    List<String> tempList = new ArrayList<>();
                    tempList.add(c.getString(TAG_ID));
                    tempList.add(c.getString(TAG_TITULO));
                    listaEventos.put(i, tempList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaEventos;
    }

    public HashMap<Integer, List<String>> getEventosParticipante(String usuarioId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_USER_ID, usuarioId));
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_get_events_partic, "GET", params);

        HashMap<Integer, List<String>> listaEventos = new HashMap<>();
        Log.d("Eventos: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                // retornando json array de eventos
                eventoJson = json.getJSONArray(TAG_EVENTOS);
                for (int i = 0; i < eventoJson.length(); i++) {
                    JSONObject c = null;
                    c = eventoJson.getJSONObject(i);
                    List<String> tempList = new ArrayList<>();
                    tempList.add(c.getString(TAG_ID));
                    tempList.add(c.getString(TAG_TITULO));
                    listaEventos.put(i, tempList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaEventos;
    }

    public Boolean isParticipante (String usuarioId, String eventoId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_USER_ID, usuarioId));
        params.add(new BasicNameValuePair(TAG_ID, eventoId));
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_is_participante, "GET", params);

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
