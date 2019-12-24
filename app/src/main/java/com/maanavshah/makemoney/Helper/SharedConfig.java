package com.maanavshah.makemoney.Helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedConfig {

    public static void setConfig(Context context, String key, String value) {
        SharedPreferences sharedpreferences = context.getSharedPreferences("ApplicationConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getConfig(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("ApplicationConfig", Context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }
}
