package controller;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import java.util.ArrayList;
import java.util.List;

import model.Evento;
import model.JSONParser;
import model.PedidoMusica;

/**
 * Created by allandoamaralalves on 05/10/15.
 */
public class PedidoMusicaDAO {
    private JSONParser jsonParser = new JSONParser();
    private JSONObject json;

    // url para inserir novo evento no banco
    private static String url_insert_new = "http://uparty.3eeweb.com/db_inserir_pedido.php";

    // JSON tags
    private String success = "0";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PEDIDO = "pedido";

    //Tags para os campos da tabela Pedidos no bando de dados
    private static final String TAG_ID = "pedido_id";
    private static final String TAG_ARTISTA = "pedido_artista";
    private static final String TAG_MUSICA = "pedido_musica";
    private static final String TAG_DJ = "pedido_dj_id";
    private static final String TAG_EVENTO = "pedido_evento_id";
    private static final String TAG_PEDINTE = "pedido_pedinte_id";
    private static final String TAG_HORA = "pedido_horario";

    private JSONArray pedidoJson = null;
    private PedidoMusica pedidoObj;

    public String inserirPedido(PedidoMusica pedido) {
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(TAG_ARTISTA, pedido.getArtista()));
        params.add(new BasicNameValuePair(TAG_MUSICA, pedido.getMusica()));
        params.add(new BasicNameValuePair(TAG_EVENTO, String.valueOf(pedido.getEventoId())));
        params.add(new BasicNameValuePair(TAG_DJ, String.valueOf(pedido.getDjId())));
        params.add(new BasicNameValuePair(TAG_PEDINTE, String.valueOf(pedido.getUsuarioId())));

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
