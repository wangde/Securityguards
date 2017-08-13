package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.hlju.wangde.securityguards.R;

/**
 * 设置向导1
 * @author XiaoDe
 */

public class Setup1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    /**
     * 下一页
     * @param view
     */
    public void next(View view){
        startActivity(new Intent(this,Setup2Activity.class));
        finish();
    }
}
