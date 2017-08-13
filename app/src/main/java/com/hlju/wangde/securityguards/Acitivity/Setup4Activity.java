package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.PrefUtils;

public class Setup4Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }

    /**
     * 上一页
     * @param view
     */
    public void previous(View view) {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
    }

    /**
     * 完成
     * @param view
     */
    public void next(View view) {
        PrefUtils.putBoolean("configed",true,this);//表示已经设置过向导页
        startActivity(new Intent(this, AntitheftActivity.class));
        finish();
    }
}
