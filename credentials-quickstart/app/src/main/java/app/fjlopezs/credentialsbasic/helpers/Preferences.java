package app.fjlopezs.credentialsbasic.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class Preferences extends ContextWrapper{
    private  SharedPreferences prefs;
    private final Context context;


    public Preferences(Context base) {
        super(base);
        prefs = PreferenceManager.getDefaultSharedPreferences(base);
        context = base;
    }

    public void setSharedPreferences(String name){
        prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return prefs;
    }

    public void savePreferences(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }
    public boolean exist(String key){
        String result =  prefs.getString(key, "");
        return  !result.equals("");
    }

    public void clearPreferences(String key) {
        prefs.edit().putString(key, "").apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }

    public String getPreferences(String key) {
        return prefs.getString(key, "");
    }





}
