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

public class UsuarioDAO {
    JSONParser jsonParser = new JSONParser();

    // url para inserir novo usuario no banco do servidor online
    private static String url_insert_new = "http://uparty.3eeweb.com/db_inserir_usuario.php";

    //Tags
    // JSON Node names
    private int success = 0;
    private static final String TAG_SUCCESS = "success";

    //Tags para os campos da tabela Usuarios no bando de dados
    private static final String TAG_NOME = "usuario_nome";
    private static final String TAG_NASC = "usuario_nascimento";
    private static final String TAG_CIDADE = "usuario_cidade";
    private static final String TAG_UF = "usuario_uf";
    private static final String TAG_EMAIL = "usuario_email";
    private static final String TAG_USER = "usuario_username";
    private static final String TAG_SENHA = "usuario_senha";

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
        // Notice that update product url accepts POST method
        JSONObject json = jsonParser.makeHttpRequest(url_insert_new,
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
}
