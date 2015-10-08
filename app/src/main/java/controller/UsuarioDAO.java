package controller;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import model.JSONParser;
import model.Usuario;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsuarioDAO {
    JSONParser jsonParser = new JSONParser();
    private JSONObject json;
    private JSONArray usuarioJson = null;
    private Usuario usuarioObj;

    // url para inserir novo usuario no banco do servidor online
    private static String url_insert_new = "http://uparty.3eeweb.com/db_inserir_usuario.php";
    private static String url_insert_dj_event = "http://uparty.3eeweb.com/db_inserir_dj_evento.php";
    private static String url_login_user = "http://uparty.3eeweb.com/db_login_usuario.php";
    private static String url_get_users_by_name = "http://uparty.3eeweb.com/db_get_user_by_name.php";
    private static String url_get_djs_by_event = "http://uparty.3eeweb.com/db_retornar_djs.php";
    private static String url_remove_djs_event = "http://uparty.3eeweb.com/db_remover_djs_evento.php";

    //Tags
    // JSON Node names
    private int success = 0;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USUARIO = "usuario";
    private static final String TAG_USUARIOS = "usuarios";
    private static final String TAG_DJS = "djs";

    //Tags para os campos da tabela Usuarios no bando de dados
    private static final String TAG_ID = "usuario_id";
    private static final String TAG_DJ_ID = "usuariodj_id";
    private static final String TAG_NOME = "usuario_nome";
    private static final String TAG_NASC = "usuario_nascimento";
    private static final String TAG_CIDADE = "usuario_cidade";
    private static final String TAG_UF = "usuario_uf";
    private static final String TAG_EMAIL = "usuario_email";
    private static final String TAG_USER = "usuario_username";
    private static final String TAG_SENHA = "usuario_senha";
    private static final String TAG_EVENTO_ID = "evento_id";

    public int inserirUsuario (Usuario usuario) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_NOME, usuario.getNome()));
        params.add(new BasicNameValuePair(TAG_NASC, usuario.getDataNascimento().getYear() + "-" + usuario.getDataNascimento().getMonth() + "-" + usuario.getDataNascimento().getDay()));
        params.add(new BasicNameValuePair(TAG_CIDADE, usuario.getCidade()));
        params.add(new BasicNameValuePair(TAG_UF, usuario.getUf()));
        params.add(new BasicNameValuePair(TAG_EMAIL, usuario.getEmail()));
        params.add(new BasicNameValuePair(TAG_USER, usuario.getNomeUsuario()));
        params.add(new BasicNameValuePair(TAG_SENHA, usuario.getSenha()));

        // sending modified data through http request
        // Notice that update product url accepts POST met8hod
        json = jsonParser.makeHttpRequest(url_insert_new,
                "GET", params);

        // check log cat fro response
        Log.d("Create Response", json.toString());

        // check json success tag
        try {
            success = json.getInt(TAG_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return success;
    }

    public Usuario logarUsuario (String username, String senha) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_USER, username));
        params.add(new BasicNameValuePair(TAG_SENHA, senha));
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_login_user, "GET", params);
        usuarioObj = new Usuario();
        Log.d("Usuario: ", json.toString());
        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                // retornando json array de eventos
                usuarioJson = json.getJSONArray(TAG_USUARIO);
                JSONObject c = usuarioJson.getJSONObject(0);
                // set resultado em objeto usario
                usuarioObj.setId(Integer.parseInt(c.getString(TAG_ID)));
                usuarioObj.setNome(c.getString(TAG_NOME));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return usuarioObj;
    }

    public HashMap<Integer, List<String>> pesquisarUsuario (String nome) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("nome_pesquisa", nome));
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_get_users_by_name, "GET", params);

        HashMap<Integer, List<String>> listaUsuarios = new HashMap<>();
        Log.d("Usuario: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                // retornando json array de eventos
                usuarioJson = json.getJSONArray(TAG_USUARIOS);
                for (int i = 0; i < usuarioJson.length(); i++) {
                    JSONObject c = null;
                    c = usuarioJson.getJSONObject(i);
                    List<String> tempList = new ArrayList<>();
                    tempList.add(c.getString(TAG_ID));
                    tempList.add(c.getString(TAG_NOME) + " (" + c.getString(TAG_USER) + ")");
                    listaUsuarios.put(i, tempList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaUsuarios;
    }

    public HashMap<Integer, List<String>> getDjsEvento(String eventoId) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_EVENTO_ID, eventoId));
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_get_djs_by_event, "GET", params);

        HashMap<Integer, List<String>> listaDjs = new HashMap<>();
        Log.d("Lala Djs: ", json.toString());

        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                // retornando json array de eventos
                usuarioJson = json.getJSONArray(TAG_DJS);
                for (int i = 0; i < usuarioJson.length(); i++) {
                    JSONObject c = null;
                    c = usuarioJson.getJSONObject(i);
                    List<String> tempList = new ArrayList<>();
                    tempList.add(c.getString(TAG_DJ_ID));
                    tempList.add(c.getString(TAG_NOME) + " (" + c.getString(TAG_USER) + ")");
                    listaDjs.put(i, tempList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listaDjs;
    }

    public int inserirDjEmEvento (String usuarioId, String eventoId) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_ID, usuarioId));
        params.add(new BasicNameValuePair(TAG_EVENTO_ID, eventoId));

        // sending modified data through http request
        // Notice that update product url accepts POST met8hod
        json = jsonParser.makeHttpRequest(url_insert_dj_event,
                "GET", params);

        // check log cat fro response
        Log.d("Create Response", json.toString());

        // check json success tag
        try {
            success = json.getInt(TAG_SUCCESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return success;
    }

    public int removerDjsEvento (String eventoId) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_EVENTO_ID, eventoId));
        // getting JSON string from URL
        json = jsonParser.makeHttpRequest(url_remove_djs_event, "GET", params);
        try {
            // Checking for SUCCESS TAG
            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                return 1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
