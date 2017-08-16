package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.PrefUtils;

import org.w3c.dom.Text;

/**
 * 手机防盗页面
 */

public class AntitheftActivity extends AppCompatActivity {

    private TextView tvPhone;
    private ImageView ivLock;
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
        tvPhone = (TextView) findViewById(R.id.tv_safe_phone);
        ivLock = (ImageView) findViewById(R.id.iv_lock);
        String phone = PrefUtils.getString("safe_phone", "", this);
        tvPhone.setText(phone);
        boolean protect = PrefUtils.getBoolean("protect", false, this);
        if (protect) {
            ivLock.setImageResource(R.drawable.lock);
        } else {
            ivLock.setImageResource(R.drawable.unlock);
        }
    }

    /**
     * 重新进入设置向导
     *
     * @param view
     */
    public void reSetup(View view) {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
    }
}
