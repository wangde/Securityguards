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

public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPrevious() {

    }

    @Override
    public void showNext() {
        startActivity(new Intent(this,Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }
}
