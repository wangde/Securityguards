package com.hlju.wangde.securityguards.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.hlju.wangde.securityguards.engine.ProcessInfoProvider;

import java.util.Timer;
import java.util.TimerTask;

public class AutoKillService extends Service {

    private InnerScreenOffReceiver mReceiver;
    private Timer mTimer;

    public AutoKillService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new InnerScreenOffReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

        //定时清理
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("自动更新");
            }
        }, 0, 5000);//每隔五秒执行一次
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mReceiver = null;
        mTimer.cancel();
        mTimer = null;
    }

    class InnerScreenOffReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProcessInfoProvider.killAll(context);
        }
    }
}
