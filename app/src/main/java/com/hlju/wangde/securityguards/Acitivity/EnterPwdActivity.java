package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.ToastUtils;

public class EnterPwdActivity extends AppCompatActivity {

    private TextView tvName;
    private ImageView ivIcon;
    private EditText etPwd;
    private Button btnOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        btnOK = (Button) findViewById(R.id.btn_ok);

        Intent intent = getIntent();
        final String packageName = intent.getStringExtra("package");//获得当前包名

        //根据包名获取信息
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            String name = applicationInfo.loadLabel(pm).toString();
            Drawable icon = applicationInfo.loadIcon(pm);

            tvName.setText(name);
            ivIcon.setImageDrawable(icon);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = etPwd.getText().toString().trim();
                if (!TextUtils.isEmpty(pwd)) {
                    if (pwd.equals("123")) {
                        //通知看门狗，跳过当前包名验证
                        Intent intent = new Intent();
                        intent.setAction("securityguards.SKIP_CHECK");
                        intent.putExtra("package", packageName);
                        sendBroadcast(intent);

                        finish();
                    } else {
                        ToastUtils.showToast(getApplicationContext(), "密码错误");
                    }
                } else {
                    ToastUtils.showToast(getApplicationContext(), "输入密码不能为空");
                }
            }
        });
    }

    //拦截物理返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //跳到桌面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

        finish();
    }

    //拦截物理按键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
