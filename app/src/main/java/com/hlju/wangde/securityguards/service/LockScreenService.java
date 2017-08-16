package com.hlju.wangde.securityguards.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.hlju.wangde.securityguards.receiver.AdminReceiver;

public class LockScreenService extends Service {
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;

    public LockScreenService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //设备策略管理器
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);
        lockScreen();

        stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void activeAdmin() {
        //跳转到激活页面
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "超级管理员权限获得");
        startActivity(intent);
    }

    public void lockScreen() {
        if (mDPM.isAdminActive(mDeviceAdminSample)) {
            mDPM.lockNow();//立即锁屏
            mDPM.resetPassword("1234", 0);
        }
    }
}
