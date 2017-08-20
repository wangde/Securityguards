package com.hlju.wangde.securityguards.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.db.dao.AddressDao;
import com.hlju.wangde.securityguards.utils.PrefUtils;

/**
 * 归属地显示服务
 */
public class AddressService extends Service {
    private TelephonyManager mTM;
    private MyListener mListener;
    private InnerReceive mReceive;
    private WindowManager mWM;
    private View mView;
    private int startY;
    private int startX;
    private int mScreenHeight;
    private int mScreenWidth;

    public AddressService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //监听来电
        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new MyListener();//电话状态监听器
        mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);

        //去电监听
        //动态注册广播，在服务开启时注册，销毁时注销

        mReceive = new InnerReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mReceive, filter);
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话响铃
                    String address = AddressDao.getAddress(incomingNumber);
//                    ToastUtils.showToast(getApplicationContext(),address);
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态，正在通话
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //电话空闲
                    if (mWM != null && mView != null) {
                        mWM.removeView(mView);
                    }
                    break;
            }
        }
    }

    class InnerReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            String address = AddressDao.getAddress(number);
//            ToastUtils.showToast(getApplicationContext(),address);
            showToast(address);
        }
    }

    /**
     * 为保证可触摸删掉FLAG_NO_TOUCHABLE
     * 加权限"android.permission.SYSTEM_ALERT_WINDOW"
     *
     * @param text
     */
    private void showToast(String text) {
        //初始化布局参数
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;//修改参数类型为TYPE_PHONE
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;


        params.gravity = Gravity.LEFT + Gravity.TOP;//设定中心为左上方，坐标体系以左上方为准
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth = mWM.getDefaultDisplay().getWidth();
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
//        mView = new TextView(this);
//        mView.setText(text);
//        mView.setTextColor(Color.RED);
//        mView.setTextSize(22);
        mView = View.inflate(this, R.layout.custom_toast, null);
        TextView tvAddress = (TextView) mView.findViewById(R.id.tv_address);
        int style = PrefUtils.getInt("address_style", 0, this);
        int[] bgIds = new int[]{R.mipmap.call_locate_white, R.mipmap.call_locate_orange,
                R.mipmap.call_locate_blue, R.mipmap.call_locate_gray, R.mipmap.call_locate_green};
        tvAddress.setBackgroundResource(bgIds[style]);
        tvAddress.setText(text);
        int lastX = PrefUtils.getInt("lastX", 0, this);
        int lastY = PrefUtils.getInt("lastY", 0, this);
        params.x = lastX;
        params.y = lastY;

        mView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //按下
                            System.out.println("按下");
                            startX = (int) event.getRawX();
                            startY = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            //移动
                            int endX = (int) event.getRawX();
                            int endY = (int) event.getRawY();

                            //计算偏移量
                            int dx = endX - startX;
                            int dy = endY - startY;
                            //根据偏移量更新位置
                            params.x = params.x + dx;
                            params.y = params.y + dy;

                            //防止xy坐标超出边界
                            if (params.x < 0) {
                                params.x = 0;
                            } else if (params.x > mScreenWidth - mView.getWidth()) {
                                params.x = mScreenWidth - mView.getWidth();
                            }
                            if (params.y < 0) {
                                params.y = 0;
                            } else if (params.y > mScreenHeight - mView.getHeight() - 25) {
                                params.y = mScreenHeight - mView.getHeight() - 25;
                            }

                            mWM.updateViewLayout(mView, params);

                            System.out.println("移动");

                            //重新初始化起点坐标
                            startX = (int) event.getRawX();
                            startY = (int) event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            //抬起
                            //保存当前位置
                            PrefUtils.putInt("lastX", params.x, getApplicationContext());
                            PrefUtils.putInt("lastY", params.y, getApplicationContext());
                            System.out.println("抬起");
                    }
                    return true;//true表示自身处理该事件
                }
            }
        });
        mWM.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);
        mListener = null;
        unregisterReceiver(mReceive);
        mListener = null;
    }
}
