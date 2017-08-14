package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.PrefUtils;
import com.hlju.wangde.securityguards.utils.ToastUtils;
import com.hlju.wangde.securityguards.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView sivBind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        sivBind = (SettingItemView) findViewById(R.id.siv_bind);
        String bindSim = PrefUtils.getString("bind_sim", null, this);
        if (TextUtils.isEmpty(bindSim)) {
            sivBind.setChecked(false);
        } else {
            sivBind.setChecked(true);
        }


        sivBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivBind.isChecked()) {
                    sivBind.setChecked(false);
                    PrefUtils.remove("bind_sim", getApplicationContext());
                } else {
                    sivBind.setChecked(true);
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();//获得sim卡序列号
                    PrefUtils.putString("bind_sim", simSerialNumber, getApplicationContext());
                }
            }
        });

    }

    @Override
    public void showPrevious() {
        startActivity(new Intent(this,Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.anim_previous_in, R.anim.anim_previous_out);
    }

    @Override
    public void showNext() {
        String bindSim = PrefUtils.getString("bind_sim", null, this);
        if (TextUtils.isEmpty(bindSim)) {
            ToastUtils.showToast(this, "必须绑定sim卡");
            return;
        }
        startActivity(new Intent(this,Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

}
