package com.hlju.wangde.securityguards.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.hlju.wangde.securityguards.utils.PrefUtils;

/**
 * 开启重启广播接受者
 */
public class BootCompleteReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String saveSim = PrefUtils.getString("bind_sim", null, context);
        if (!TextUtils.isEmpty(saveSim)) {
            //获取当前sim卡和保存的sim卡比对
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentSim = tm.getSimSerialNumber();
            if (saveSim.equals(currentSim)) {
                System.out.println("安全");
            } else {
                System.out.println("sim卡有变化");

            }
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
