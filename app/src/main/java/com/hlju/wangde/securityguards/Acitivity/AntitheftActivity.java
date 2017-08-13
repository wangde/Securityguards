package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.PrefUtils;

/**
 * 手机防盗页面
 */

public class AntitheftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断是否第一次进入
        boolean configed = PrefUtils.getBoolean("configed",false,this);
        if(!configed){
            //进入设置向导页面
            startActivity(new Intent(this,Setup1Activity.class));
            finish();
        }else {
            setContentView(R.layout.activity_antitheft);
        }
    }
}
