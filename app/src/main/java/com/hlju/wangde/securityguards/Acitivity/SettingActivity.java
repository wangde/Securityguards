package com.hlju.wangde.securityguards.Acitivity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.service.AddressService;
import com.hlju.wangde.securityguards.service.BlackNumberService;
import com.hlju.wangde.securityguards.service.WatchDogService;
import com.hlju.wangde.securityguards.utils.PrefUtils;
import com.hlju.wangde.securityguards.utils.ServiceStatusUtils;
import com.hlju.wangde.securityguards.view.SettingItemClickView;
import com.hlju.wangde.securityguards.view.SettingItemView;

import java.io.InputStream;

public class SettingActivity extends AppCompatActivity {

    private SettingItemView sivUpdate;
    private SettingItemView sivAddress;
    private SettingItemClickView sicStyle;
    private SettingItemClickView sicLocation;
    private SettingItemView sivBlackNumber;
    private SettingItemView sivAppLock;
    private String[] mItem = new String[]{"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};


    //    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        sp = getSharedPreferences("config", MODE_PRIVATE);
        initUpdate();
        initAddress();
        initAddressStyle();
        initAddressLocation();
        initBlackNumber();
        initAppLock();

    }

    /**
     * 程序锁，看门狗
     */
    private void initAppLock() {
        sivAppLock = (SettingItemView) findViewById(R.id.siv_app_lock);
        boolean serviceRunning = ServiceStatusUtils.isServiceRuning
                ("com.hlju.wangde.securityguards.service.WatchDogService", this);
        sivAppLock.setChecked(serviceRunning);
        sivAppLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getApplicationContext(), WatchDogService.class);

                if (sivAppLock.isChecked()) {
                    sivAppLock.setChecked(false);
                    stopService(service);//关闭开门狗
                } else {
                    sivAppLock.setChecked(true);
                    startService(service);//开启程序锁
                }
            }
        });

    }

    /**
     * 初始化自动更新
     */
    private void initUpdate() {
        boolean autoUpdate = PrefUtils.getBoolean("auto_update", true, this);

        sivUpdate = (SettingItemView) findViewById(R.id.siv_update);
//        sivUpdate.setTitle("自动更新设置");
//        if(autoUpdate){
//            sivUpdate.setChecked(true);
//            sivUpdate.setDesc("自动更新已开启");
//        }else {
//            sivUpdate.setChecked(false);
//            sivUpdate.setDesc("自动更新已关闭");
//        }
        sivUpdate.setChecked(autoUpdate);

        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivUpdate.isChecked()) {
                    sivUpdate.setChecked(false);
//                    sivUpdate.setDesc("自动更新已关闭");
                    PrefUtils.putBoolean("auto_update", false, getApplicationContext());
                } else {
                    sivUpdate.setChecked(true);
//                    sivUpdate.setDesc("自动更新已开启");
                    PrefUtils.putBoolean("auto_update", true, getApplicationContext());
                }
            }
        });
    }

    /**
     * 归属地显示设置
     */
    private void initAddress() {
        sivAddress = (SettingItemView) findViewById(R.id.siv_address);

        boolean serviceRunning = ServiceStatusUtils.isServiceRuning("com.hlju.wangde.securityguards.service.AddressService", getApplicationContext());
        sivAddress.setChecked(serviceRunning);
        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getApplicationContext(), AddressService.class);

                if (sivAddress.isChecked()) {
                    sivAddress.setChecked(false);
                    stopService(service);
                } else {
                    sivAddress.setChecked(true);
                    startService(service);//开启归属地显示
                }
            }
        });
    }

    /**
     * 初始化归属地显示
     */
    private void initAddressStyle() {

        sicStyle = (SettingItemClickView) findViewById(R.id.sic_style);
        int style = PrefUtils.getInt("address_style", 0, getApplicationContext());
        sicStyle.setTitle("归属地显示风格");
        sicStyle.setDesc(mItem[style]);
        sicStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseDialog();
            }
        });
    }

    private void showChooseDialog() {

        int style = PrefUtils.getInt("address_style", 0, getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");
//        sicStyle.setDesc(mItem[style]);
        builder.setSingleChoiceItems(mItem, style, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrefUtils.putInt("address_style", which, getApplicationContext());
                dialog.dismiss();
                sicStyle.setDesc(mItem[which]);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void initAddressLocation() {
        sicLocation = (SettingItemClickView) findViewById(R.id.sic_location);
        sicLocation.setTitle("设置归属地提示框位置");
        sicLocation.setDesc("设置归属提示框显示位置");
        sicLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到位置修改页面
                startActivity(new Intent(getApplicationContext(), DragViewActivity.class));
            }
        });
    }

    private void initBlackNumber() {
        sivBlackNumber = (SettingItemView) findViewById(R.id.siv_black_number);
        boolean serviceRunning = ServiceStatusUtils.isServiceRuning
                ("com.hlju.wangde.securityguards.service.BlackNumberService", getApplicationContext());
        sivBlackNumber.setChecked(serviceRunning);
        sivBlackNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getApplicationContext(), BlackNumberService.class);

                if (sivBlackNumber.isChecked()) {
                    sivBlackNumber.setChecked(false);
                    stopService(service);
                } else {
                    sivBlackNumber.setChecked(true);
                    startService(service);//开启归属地显示
                }
            }
        });
    }
}
