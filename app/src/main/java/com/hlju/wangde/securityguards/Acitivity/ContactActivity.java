package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import com.hlju.wangde.securityguards.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ContactActivity extends AppCompatActivity {

    private ListView lvContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        lvContact = (ListView) findViewById(R.id.lv_contact);
        final ArrayList<HashMap<String, String>> list = readContact();
        lvContact.setAdapter(new SimpleAdapter(
                this, list, R.layout.list_item_contact,
                new String[]{"name", "phone"}, new int[]{R.id.tv_name, R.id.tv_phone}));
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> map = list.get(position);
                String phone = map.get("phone");

                Intent data = new Intent();
                data.putExtra("phone", phone);
                setResult(0, data);
                finish();
            }
        });

    }

    /**
     * 获取联系人姓名和电话
     *
     * @return
     */
    private ArrayList<HashMap<String, String>> readContact() {
        //查询联系人的contact_id
        Cursor rawContactCursor = getContentResolver().query(
                Uri.parse("content://com.android.contacts/raw_contacts"),
                new String[]{"contact_id"}, null, null, null);
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        while (rawContactCursor.moveToNext()) {
            String contactId = rawContactCursor.getString(0);

            Cursor dataCursor = getContentResolver().query(
                    Uri.parse("content://com.android.contacts/data"),
                    new String[]{"data1", "mimetype"}, "raw_contact_id=?",
                    new String[]{contactId}, null);
            HashMap<String, String> map = new HashMap<String, String>();//保存某个联系人信息

            while (dataCursor.moveToNext()) {
                String data = dataCursor.getString(0);
                String type = dataCursor.getString(1);
                if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                    map.put("phone", data);
                } else if ("vnd.android.cursor.item/name".equals(type)) {
                    map.put("name", data);
                }
            }
            dataCursor.close();
            if (!TextUtils.isEmpty(map.get("name")) && !TextUtils.isEmpty(map.get("phone"))) {
                list.add(map);
            }
        }
        rawContactCursor.close();
        return list;
    }
}
