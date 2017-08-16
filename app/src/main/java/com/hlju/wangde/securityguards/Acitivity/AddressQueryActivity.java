package com.hlju.wangde.securityguards.Acitivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.db.dao.AddressDao;
import com.hlju.wangde.securityguards.utils.ToastUtils;

public class AddressQueryActivity extends AppCompatActivity {

    private Button btnQuery;
    private EditText etNumber;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_query);

        etNumber = (EditText) findViewById(R.id.et_number);
        btnQuery = (Button) findViewById(R.id.btn_query);
        tvResult = (TextView) findViewById(R.id.tv_result);

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = etNumber.getText().toString().trim();
                if (!TextUtils.isEmpty(number)) {
                    String address = AddressDao.getAddress(number);
                    tvResult.setText(address);
                } else {
                    ToastUtils.showToast(getApplicationContext(), "号码不能为空");
                }
            }
        });
    }
}
