package com.hlju.wangde.securityguards.Acitivity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.PrefUtils;
import com.hlju.wangde.securityguards.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    private SettingItemView sivUpdate;

    //    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
//        sp = getSharedPreferences("config", MODE_PRIVATE);
        initUpdate();
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
}
