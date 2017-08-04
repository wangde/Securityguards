package com.hlju.wangde.securityguards.Acitivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
  * 闪屏页面
  * -展示logo,公司品牌
  * -检测版本更新
  * -项目初始化
  * -校验合法性
  * @author WangDe
  */
public class SplashActivity extends AppCompatActivity {

    private TextView tvName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvName = (TextView)findViewById(R.id.tv_name);
        tvName.setText("版本名："+ getVersionName());
        checkVersion();
    }
    /**
     * 检查版本权限
     */
    private void checkVersion(){
        new Thread(){
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection)new URL("http://").openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);

                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if(responseCode == 200){
                        InputStream in = conn.getInputStream();
                        String result = StreamUtils.stream2String(in);
                        System.out.println("result: "+result);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    /**
      * 获取版本名称
      */
    private String getVersionName(){
        PackageManager pm = getPackageManager();//包管理器
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            int versionCode = packageInfo.versionCode;

            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //包名未找到异常
            e.printStackTrace();
        }
        return "";
    }
}
