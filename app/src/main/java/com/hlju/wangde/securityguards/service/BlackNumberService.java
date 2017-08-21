package com.hlju.wangde.securityguards.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.hlju.wangde.securityguards.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 黑名单拦截
 */
public class BlackNumberService extends Service {

    private InnerSmsReceive mReceiver;
    private BlackNumberDao mDao;
    private TelephonyManager mTM;
    private MyListener mListener;
    private MyObserver mObserver;

    public BlackNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mDao = BlackNumberDao.getInstance(this);
        mReceiver = new InnerSmsReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mReceiver, filter);
        mListener = new MyListener();

        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话响铃
                    int mode = mDao.findMode(incomingNumber);
                    if (mode == 1 || mode == 3) {
                        endCall();
                        //系统添加通话记录是异步操作，在此处删除时可能记录还没添加出来
                        //需要系统将记录添加完成后再删除
//                        delecteCalllog(incomingNumber);
                        mObserver = new MyObserver(new Handler(), incomingNumber);
                        getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, mObserver);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态，正在通话
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //电话空闲
                    break;
            }
        }
    }

    class MyObserver extends ContentObserver {

        private String incomingNumber;

        public MyObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        //数据发生变化时
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            System.out.println("通话记录发生变化");
            delecteCalllog(incomingNumber);

            //注销监听
            getContentResolver().unregisterContentObserver(mObserver);

        }
    }

    /**
     * 删除通话记录
     * <uses-permission android:name="android.permission.WRITE_CALL_LOG"/>
     * <uses-permission android:name="android.permission.WRITE_CONTACTS"/>
     *
     * @param number
     */

    private void delecteCalllog(String number) {
        getContentResolver().delete(Uri.parse("content://call_log/calls"), "number=?", new String[]{number});
    }

    private void endCall() {
        System.out.println("挂断电话");
        try {
            Class clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getMethod("getService", String.class);
            IBinder b = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony service = ITelephony.Stub.asInterface(b);
            service.endCall();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mReceiver = null;

        mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);
        mListener = null;
    }

    class InnerSmsReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            String format = intent.getStringExtra("format");
            SmsMessage sms;
            for (Object obj : objs) {
                if (Build.VERSION.SDK_INT < 23) {
                    sms = SmsMessage.createFromPdu((byte[]) obj);
                } else {
                    sms = SmsMessage.createFromPdu((byte[]) obj, format);
                }
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                System.out.println("短信号码：" + originatingAddress + ";短信内容：" + messageBody);

                if (mDao.find(originatingAddress)) {
                    int mode = mDao.findMode(originatingAddress);
                    if (mode > 1) {
                        abortBroadcast();
                    }
                }

            }
        }
    }
}
