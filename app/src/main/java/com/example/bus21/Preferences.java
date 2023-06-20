package com.example.bus21;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    private static final String DATA_LOGIN = "status_login",
            DATA_USERTYPE = "usertype";

    private static SharedPreferences getSharedPreferences (Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);

    }

    public static void setDataUsertype(Context context, String data){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(DATA_USERTYPE, data);
        editor.apply();
    }

    public static String getDataUsertype(Context context){
        return getSharedPreferences(context).getString(DATA_USERTYPE,"");
    }
    public static void setDataLogin(Context context, boolean status){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(DATA_LOGIN, status);
        editor.apply();
    }

    public static boolean getDataLogin (Context context){
        return getSharedPreferences(context).getBoolean(DATA_LOGIN, false);
    }

    public static void clearData (Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(DATA_USERTYPE);
        editor.remove(DATA_LOGIN);
        editor.apply();
    }
}
