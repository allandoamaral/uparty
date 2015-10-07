package controller;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.JSONParser;

/**
 * Created by allandoamaralalves on 05/10/15.
 */
public class DjDAO {
    private JSONParser jsonParser = new JSONParser();
    private JSONObject json;

    // JSON tags
    private String success = "0";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_EVENTO_ID = "evento_id";
    private static final String TAG_ID = "usuario_id";
    private static final String TAG_NOME = "usuario_nome";

    private static final String TAG_DJS = "djs";

    private JSONArray djJson = null;


}
