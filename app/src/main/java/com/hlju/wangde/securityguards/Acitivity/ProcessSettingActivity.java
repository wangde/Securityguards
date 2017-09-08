package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.service.AutoKillService;
import com.hlju.wangde.securityguards.utils.PrefUtils;
import com.hlju.wangde.securityguards.utils.ServiceStatusUtils;

import java.io.InputStream;

public class ProcessSettingActivity extends AppCompatActivity {

    private CheckBox cbShowSystem;
    private CheckBox cbAutKill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        cbShowSystem = (CheckBox) findViewById(R.id.cb_showsystem);
        cbAutKill = (CheckBox) findViewById(R.id.cb_auto_kill);
        boolean showSystem = PrefUtils.getBoolean("show_system", true, getApplicationContext());
        if (showSystem) {
            cbShowSystem.setChecked(true);
            cbShowSystem.setText("显示系统进程");
        } else {
            cbShowSystem.setChecked(false);
            cbShowSystem.setText("不显示系统进程");
        }
        cbShowSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cbShowSystem.setText("显示系统进程");
                    PrefUtils.putBoolean("show_system", true, getApplicationContext());
                } else {
                    cbShowSystem.setText("不显示系统进程");
                    PrefUtils.putBoolean("show_system", false, getApplicationContext());
                }
            }
        });
        boolean serviceRuning = ServiceStatusUtils.isServiceRuning("com.hlju.wangde.securityguards.service.AutoKillService", this);
        if (serviceRuning) {
            cbAutKill.setChecked(true);
            cbAutKill.setText("锁屏清理已开启");
        } else {
            cbAutKill.setChecked(false);
            cbAutKill.setText("锁屏清理已关闭");
        }
        cbAutKill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent service = new Intent(getApplicationContext(), AutoKillService.class);
                if (isChecked) {
                    cbAutKill.setText("锁屏清理已开启");
                    startService(service);
                } else {
                    cbAutKill.setText("锁屏清理已关闭");
                    stopService(service);
                }
            }
        });
    }
}
