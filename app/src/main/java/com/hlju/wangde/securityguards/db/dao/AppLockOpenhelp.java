package com.hlju.wangde.securityguards.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by XiaoDe on 2017/8/18 19:38.
 * 陈旭所数据库
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class AppLockOpenhelp extends SQLiteOpenHelper {

    public AppLockOpenhelp(Context context) {
        super(context, "applock.db", null, 1);
    }
//    public BlackNumberOpenhelp(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //字段：_id,package:已加锁包名
        //mode 1:拦截电话 2拦截短信 3拦截所有
        try {
            String sql = "create table applock (_id integer primary key autoincrement, package varchar(50))";
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
