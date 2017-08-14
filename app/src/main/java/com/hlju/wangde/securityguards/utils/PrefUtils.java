package com.hlju.wangde.securityguards.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by XiaoDe on 2017/8/8 20:02.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 *         对SharePreference封装
 */

public class PrefUtils {

    public static void putBoolean(String key, boolean value, Context context){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();
    }
    public static boolean getBoolean(String key, boolean defValue, Context context){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getBoolean(key,defValue);
    }
    public static void putString(String key, String value, Context context){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
    public static String getString(String key, String defValue, Context context){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key,defValue);
    }
    public static void putInt(String key, int value, Context context){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();
    }
    public static int getBoolean(String key, int defValue, Context context){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getInt(key,defValue);
    }

    public static void remove(String key, Context context) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }
}
