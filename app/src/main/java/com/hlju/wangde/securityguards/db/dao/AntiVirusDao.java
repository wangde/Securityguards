package com.hlju.wangde.securityguards.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by XiaoDe on 2017/8/16 9:57.
 * 病毒数据库封装
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class AntiVirusDao {
    private static final String PATH = "/data/data/com.hlju.wangde.securityguards/files/antivirus.db";


    public static boolean isVirus(String md5) {

        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = database.rawQuery("select * from datable where md5=?", new String[]{md5});
        boolean isVirus = false;
        if (cursor.moveToFirst()) {
            isVirus = true;
        }

        cursor.close();
        database.close();
        return isVirus;

    }
}
