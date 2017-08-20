package com.hlju.wangde.securityguards.Acitivity;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
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
                    Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    //自定义插补器
//                    shake.setInterpolator(new Interpolator() {
//                        @Override
//                        public float getInterpolation(float input) {
//                            float y = input;
//                            return y;
//
//                        }
//                    });
                    etNumber.startAnimation(shake);
                    vibrator();
                }
            }
        });
        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String address = AddressDao.getAddress(s.toString());
                tvResult.setText(address);
            }
        });
    }

    private void vibrator() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{1000, 2000, 2000, 3000}, -1);//arg1:震动模式，arg2：-1不重复，0表示从第0个位置开始重复
//        vibrator.vibrate(2000);
    }
}
