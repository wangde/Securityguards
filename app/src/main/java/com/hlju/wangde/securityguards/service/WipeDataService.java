package com.hlju.wangde.securityguards.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import com.hlju.wangde.securityguards.receiver.AdminReceiver;

public class WipeDataService extends Service {
    private DevicePolicyManager mDPM;
    private ComponentName mDeviceAdminSample;

    public WipeDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        mDeviceAdminSample = new ComponentName(this, AdminReceiver.class);
        wipeData();
        stopSelf();
    }

    public void wipeData() {
        if (mDPM.isAdminActive(mDeviceAdminSample)) {
            mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//清除SD卡 0表示清除手机内存
        } else {
            Toast.makeText(this, "没有获得超级管理器权限", Toast.LENGTH_SHORT).show();
        }
    }
}
