package com.hlju.wangde.securityguards.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.hlju.wangde.securityguards.Acitivity.HomeActivity;
import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.engine.ProcessInfoProvider;
import com.hlju.wangde.securityguards.receiver.MyWidget;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {

    private Timer mTimer;
    private AppWidgetManager mAWM;
    private InnerScreenReceiver mReceiver;

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化Widget管理器
        mAWM = AppWidgetManager.getInstance(this);
        startTimer();

        //监听开启关闭广播
        mReceiver = new InnerScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateWidget();
            }
        }, 0, 5000);
    }

    class InnerScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                startTimer();
            } else {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }

    private void updateWidget() {
        System.out.println("更新widget");
        //初始化组件
        ComponentName provider = new ComponentName(this, MyWidget.class);
        //初始化远程view对象
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.my_widget);
        //更新文字
        views.setTextViewText(R.id.tv_running_num, "正在运行：" +
                ProcessInfoProvider.getRunningProcessNum(this));
        views.setTextViewText(R.id.tv_avail_memory, "可用内存：" +
                Formatter.formatFileSize(this, ProcessInfoProvider.getAvailMemory(this)));
        Intent intent = new Intent(this, HomeActivity.class);
        //延时intent，是对intent的包装，不确定合适执行的Intent
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.ll_root, pendingIntent);

        Intent clearIntent = new Intent();
        clearIntent.setAction("com.hlju.wangde.securityguards.KILL");
        PendingIntent clearPendingIntent = PendingIntent.getBroadcast(this, 0, clearIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_clear, clearPendingIntent);
        mAWM.updateAppWidget(provider, views);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer = null;

        unregisterReceiver(mReceiver);
        mReceiver = null;
    }
}
