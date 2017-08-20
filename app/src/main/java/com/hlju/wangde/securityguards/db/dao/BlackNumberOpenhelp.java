package com.hlju.wangde.securityguards.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by XiaoDe on 2017/8/18 19:38.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class BlackNumberOpenhelp extends SQLiteOpenHelper {

    public BlackNumberOpenhelp(Context context) {
        super(context, "blacknumber.db", null, 1);
    }
//    public BlackNumberOpenhelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //字段：_id,number(电话号码) ,mode(拦截模式)
        //mode 1:拦截电话 2拦截短信 3拦截所有
        try {
            String sql = "create table blacknumber (_id integer primary key autoincrement, number varchar(20), mode integer)";
            db.execSQL(sql);
            System.out.println("创建数据库");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //数据库更新时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
