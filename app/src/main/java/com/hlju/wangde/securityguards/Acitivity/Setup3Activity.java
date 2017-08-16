package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.PrefUtils;
import com.hlju.wangde.securityguards.utils.ToastUtils;

public class Setup3Activity extends BaseSetupActivity {

    EditText etPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        etPhone = (EditText) findViewById(R.id.et_phone);
        String phone = PrefUtils.getString("safe_phone", "", this);
        etPhone.setText(phone);
    }

    @Override
    public void showPrevious() {
        startActivity(new Intent(this,Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.anim_previous_in, R.anim.anim_previous_out);
    }

    @Override
    public void showNext() {
        //保存安全号码
        String phone = etPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            PrefUtils.putString("safe_phone", phone, this);
            startActivity(new Intent(this, Setup4Activity.class));
            finish();
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        } else {
            ToastUtils.showToast(this, "安全号码不能为空");
        }

    }

    /**
     * 选择联系人
     *
     * @param view
     */

    public void selectContact(View view) {
        startActivityForResult(new Intent(getApplicationContext(), ContactActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra("phone");
            phone = phone.replace("-", "").replace(" ", "");
            etPhone.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
