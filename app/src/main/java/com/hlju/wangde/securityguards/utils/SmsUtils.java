package com.hlju.wangde.securityguards.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by XiaoDe on 2017/8/20 20:01.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class SmsUtils {

    public static void smsBackup(Context context, File output, SmsCallback smsCallback) {


        try {
            Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);
            smsCallback.preSmsBack(cursor.getCount());
            XmlSerializer xml = Xml.newSerializer();
            xml.setOutput(new FileOutputStream(output), "utf-8");
            xml.startDocument("utf-8", false);
            xml.startTag(null, "smss");

            int progress = 0;


            while (cursor.moveToNext()) {
                xml.startTag(null, "sms");

                xml.startTag(null, "address");
                String address = cursor.getString(cursor.getColumnIndex("address"));
                xml.text(address);
                xml.endTag(null, "address");

                xml.startTag(null, "date");
                String date = cursor.getString(cursor.getColumnIndex("date"));
                xml.text(date);
                xml.endTag(null, "date");

                xml.startTag(null, "type");
                String type = cursor.getString(cursor.getColumnIndex("type"));
                xml.text(type);
                xml.endTag(null, "type");

                xml.startTag(null, "body");
                String body = cursor.getString(cursor.getColumnIndex("body"));
                xml.text(body);
                xml.endTag(null, "body");

                xml.endTag(null, "sms");
                progress++;
                smsCallback.onSmsBack(progress);
//                dialog.setProgress(progress);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            xml.endTag(null, "smss");
            xml.endDocument();
            System.out.println("备份完成");
            cursor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public interface SmsCallback {
        public void preSmsBack(int count);

        public void onSmsBack(int progress);
    }
}
