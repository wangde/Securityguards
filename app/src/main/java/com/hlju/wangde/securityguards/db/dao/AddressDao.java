package com.hlju.wangde.securityguards.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by XiaoDe on 2017/8/16 9:57.
 * 归属地查询数据库封装
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class AddressDao {
    private static final String PATH = "/data/data/com.hlju.wangde.securityguards/files/address.db";


    public static String getAddress(String number) {
        String address = "未知号码";
        Cursor cursor = null;
        SQLiteDatabase database = null;
        try {

            database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
            //判断是不是手机号码
            //1+[3-9]+9位数字
            //正则表达式^1[3-9]\d{9}$
            if (number.matches("^1[3-9]\\d{9}$")) {
                cursor = database.rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{number.substring(0, 7)});

                if (cursor.moveToFirst()) {
                    address = cursor.getString(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            database.close();
            return address;
        }


    }
}
