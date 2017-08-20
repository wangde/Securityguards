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
            } else {
                switch (number.length()) {
                    case 3:
                        address = "报警电话等";
                        break;
                    case 4:
                        address = "模拟器";
                        break;
                    case 5:
                        address = "服务电话";
                        break;
                    case 7:
                    case 8:
                        address = "本地座机电话";
                        break;
                    default:
                        if (number.startsWith("0") && number.length() >= 11 && number.length() <= 12) {
                            cursor = database.rawQuery("select location from data2 where area = ?",
                                    new String[]{number.substring(1, 4)});
                            if (cursor.moveToFirst()) {
                                address = cursor.getString(0);
                            }
                            cursor.close();
                            if ("未知号码".equals(address)) {
                                cursor = database.rawQuery("select location from data2 where area = ?",
                                        new String[]{number.substring(1, 3)});
                                if (cursor.moveToFirst()) {
                                    address = cursor.getString(0);
                                }
                                cursor.close();
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            database.close();
            return address;
        }


    }
}
