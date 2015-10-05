package model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class UpartySettings {
    public static final UpartySettings INSTANCE = new UpartySettings();

    private String prefNome, prefId;

    public static UpartySettings getInstance() {
        return INSTANCE;
    }

    public void getSettingsValues(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefNome = prefs.getString("usuario_nome", "");
        prefId = prefs.getString("usuario_id", "");
    }

    public String getPrefNome() {
        return prefNome;
    }

    public void setPrefNome(String prefNome) {
        this.prefNome = prefNome;
    }

    public String getPrefId() {
        return prefId;
    }

    public void setPrefId(String prefId) {
        this.prefId = prefId;
    }
}
