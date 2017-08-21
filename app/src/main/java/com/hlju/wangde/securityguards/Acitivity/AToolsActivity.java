package com.hlju.wangde.securityguards.Acitivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.SmsUtils;
import com.hlju.wangde.securityguards.utils.ToastUtils;

import java.io.File;

public class AToolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    public void addressQuery(View view) {
        startActivity(new Intent(this, AddressQueryActivity.class));
    }

    public void smsBackup(View view) {

        //从系统短信数据库读取短信内容
        //将短信序列化到xml文件中'
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在备份短信....");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//水平进度条
            dialog.show();
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String s = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smsBackup.xml";
                    File output = new File(s);
                    SmsUtils.smsBackup(getApplicationContext(), output, new SmsUtils.SmsCallback() {
                        @Override
                        public void preSmsBack(int count) {
                            dialog.setMax(count);
                        }

                        @Override
                        public void onSmsBack(int progress) {
                            dialog.setProgress(progress);
                        }
                    });
//                    SmsUtils.smsBackup(getApplicationContext(),output,dialog);
                    dialog.dismiss();
                }
            }.start();


        } else {
            ToastUtils.showToast(this, "sdcard不存在");
        }
    }
}
