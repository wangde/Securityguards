package com.hlju.wangde.securityguards.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hlju.wangde.securityguards.domain.BlackNumberInfo;

import java.util.ArrayList;

/**
 * Created by XiaoDe on 2017/8/18 19:44.
 * 黑名单增删改查
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class BlackNumberDao {

    private static BlackNumberDao sInstance = null;
    private final BlackNumberOpenhelp mHelper;


    private BlackNumberDao(Context context) {
        mHelper = new BlackNumberOpenhelp(context);
    }

    public static BlackNumberDao getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BlackNumberDao.class) {
                if (sInstance == null) {
                    sInstance = new BlackNumberDao(context);
                }
            }
        }
        return sInstance;
    }

    public void add(String number, int mode) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        database.insert("blacknumber", null, values);
        database.close();
    }

    public void delete(String number) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.delete("blacknumber", "number=?", new String[]{number});
        database.close();
    }

    public void update(String number, int mode) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", mode);
        database.update("blacknumber", values, "number=?", new String[]{number});
        database.close();
    }

    public boolean find(String number) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query("blacknumber", new String[]{"number,mode"}, "number=?",
                new String[]{number}, null, null, null);
        boolean exist = false;
        if (cursor.moveToFirst()) {
            exist = true;
        }
        cursor.close();
        database.close();
        return exist;

    }

    public int findMode(String number) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query("blacknumber", new String[]{"mode"}, "number=?",
                new String[]{number}, null, null, null);
        int mode = -1;
        if (cursor.moveToFirst()) {
            mode = cursor.getInt(0);
        }
        cursor.close();
        database.close();
        return mode;
    }

    public ArrayList<BlackNumberInfo> findall() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query("blacknumber", new String[]{"number", "mode"}, null,
                null, null, null, null);
        ArrayList<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            int mode = cursor.getInt(1);
            info.number = number;
            info.mode = mode;
            list.add(info);
        }

        cursor.close();
        database.close();
        return list;
    }

    public ArrayList<BlackNumberInfo> findpart(int index) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select number,mode from blacknumber order by _id desc limit ?,20",
                new String[]{index + ""});//分页查20条数据
        ArrayList<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            String number = cursor.getString(0);
            int mode = cursor.getInt(1);
            info.number = number;
            info.mode = mode;
            list.add(info);
        }

        cursor.close();
        database.close();
        return list;
    }

    /**
     * 查询数据库总条数
     *
     * @return
     */
    public int getTotalCount() {
        int count = -1;
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("select count(*) from blacknumber", null);
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        database.close();
        return count;
    }
}
