package com.hlju.wangde.securityguards.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hlju.wangde.securityguards.engine.ProcessInfoProvider;

public class KillReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        ProcessInfoProvider.killAll(context);
    }
}
