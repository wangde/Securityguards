package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.os.Bundle;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.PrefUtils;

public class Setup4Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }


    @Override
    public void showPrevious() {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.anim_previous_in, R.anim.anim_previous_out);
    }

    @Override
    public void showNext() {
        PrefUtils.putBoolean("configed",true,this);//表示已经设置过向导页
        startActivity(new Intent(this, AntitheftActivity.class));
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        finish();
    }
}
