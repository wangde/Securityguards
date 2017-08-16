package com.hlju.wangde.securityguards.Acitivity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.receiver.AdminReceiver;
import com.hlju.wangde.securityguards.utils.PrefUtils;

public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cbProtect;
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        cbProtect = (CheckBox) findViewById(R.id.cb_protect);
        boolean protect = PrefUtils.getBoolean("protect", false, this);
        if (protect) {
            cbProtect.setChecked(true);
            cbProtect.setText("防盗保护已经开启");
        } else {
            cbProtect.setChecked(false);
            cbProtect.setText("防盗保护已关闭");
        }
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbProtect.setText("防盗保护已经开启");
                    PrefUtils.putBoolean("protect", true, getApplicationContext());
                } else {
                    cbProtect.setText("防盗保护已关闭");
                    PrefUtils.putBoolean("protect", false, getApplicationContext());
                }
            }
        });
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
        activeAdmin();
        finish();
    }

    public void activeAdmin() {
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);
        //跳转到激活页面
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "超级管理员权限获得");
        startActivity(intent);
    }
}
