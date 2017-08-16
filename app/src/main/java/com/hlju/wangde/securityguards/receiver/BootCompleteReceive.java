package com.hlju.wangde.securityguards.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
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
        boolean protext = PrefUtils.getBoolean("protext", false, context);
        if (!protext) {//如果没有开启防盗保护，直接返回
            return;
        }
        String saveSim = PrefUtils.getString("bind_sim", null, context);
        if (!TextUtils.isEmpty(saveSim)) {
            //获取当前sim卡和保存的sim卡比对
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentSim = tm.getSimSerialNumber();
            if (saveSim.equals(currentSim)) {
                System.out.println("安全");
            } else {
                System.out.println("sim卡有变化");
                String safePhone = PrefUtils.getString("safe_phone", "", context);
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safePhone, null, "sim card changed!!", null, null);

            }
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
