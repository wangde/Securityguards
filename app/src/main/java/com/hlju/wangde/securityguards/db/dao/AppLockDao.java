package com.hlju.wangde.securityguards.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.hlju.wangde.securityguards.domain.BlackNumberInfo;

import java.util.ArrayList;

/**
 * Created by XiaoDe on 2017/8/18 19:44.
 * 程序锁增删改查
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class AppLockDao {

    private static AppLockDao sInstance = null;
    private final AppLockOpenhelp mHelper;
    private Context mContext;

    private AppLockDao(Context context) {
        mHelper = new AppLockOpenhelp(context);
        mContext = context;
    }

    public static AppLockDao getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppLockDao.class) {
                if (sInstance == null) {
                    sInstance = new AppLockDao(context);
                }
            }
        }
        return sInstance;
    }

    /**
     * 增加已加锁应用
     *
     * @param packageName
     */
    public void add(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("package", packageName);
        database.insert("applock", null, values);
        database.close();

        //通知数据库发生变化
        mContext.getContentResolver().notifyChange(Uri.parse("content://com.securityguards.APP_LOCK_CHANGED"), null);
    }

    /**
     * 删除已加锁应用
     *
     * @param packageName
     */
    public void delete(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.delete("applock", "package=?", new String[]{packageName});
        database.close();
        mContext.getContentResolver().notifyChange(Uri.parse("content://com.securityguards.APP_LOCK_CHANGED"), null);
    }

    /**
     * 查询是否已加锁
     *
     * @param packageName
     * @return
     */
    public boolean find(String packageName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query("applock", new String[]{"package"}, "package=?",
                new String[]{packageName}, null, null, null);
        boolean exist = false;
        if (cursor.moveToFirst()) {
            exist = true;
        }
        cursor.close();
        database.close();
        return exist;

    }

    /**
     * 查询所有已加锁应用
     *
     * @return
     */
    public ArrayList<String> findall() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query("applock", new String[]{"package"}, null,
                null, null, null, null);
        ArrayList<String> list = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String packageName = cursor.getString(0);
            list.add(packageName);
        }
        cursor.close();
        database.close();
        return list;
    }
}
