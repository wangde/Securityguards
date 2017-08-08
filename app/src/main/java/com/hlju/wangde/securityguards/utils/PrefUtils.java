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
}
