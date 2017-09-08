package com.hlju.wangde.securityguards.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.hlju.wangde.securityguards.Acitivity.EnterPwdActivity;
import com.hlju.wangde.securityguards.db.dao.AppLockDao;

import java.util.ArrayList;
import java.util.List;

public class WatchDogService extends Service {

    private ActivityManager mAM;
    private boolean isRunning = true;
    private AppLockDao mDao;
    private MyReceiver mReceiver;
    private String mSkipPackage;
    private ArrayList<String> mLocklist;
    private MyContentObserver mObserver;

    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mAM = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        mDao = AppLockDao.getInstance(this);
        mLocklist = mDao.findall();
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isRunning) {
                    //获取当前屏幕展示页面
                    List<ActivityManager.RunningTaskInfo> runningTasks = mAM.getRunningTasks(1);
                    String packageName = runningTasks.get(0).topActivity.getPackageName();//获取栈顶activity所在的包名
//                    System.out.println(packageName);
//                    if (mDao.find(packageName)
                    if (mLocklist.contains(packageName)//直接从内存中查找避免频繁查询数据库
                            && !packageName.equals(mSkipPackage)) {
                        //跳入输入密码页
                        Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("package", packageName);//将包名传递给密码页
                        startActivity(intent);
                    }
                    SystemClock.sleep(100);

                }
            }
        }.start();
        mReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("securityguards.SKIP_CHECK");
        registerReceiver(mReceiver, filter);

        //监听程序锁数据库变化
        mObserver = new MyContentObserver(null);
        getContentResolver().registerContentObserver(
                Uri.parse("content://com.securityguards.APP_LOCK_CHANGED"), true, mObserver);
    }

    class MyContentObserver extends ContentObserver {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //重新拉取数据库数据
            mLocklist = mDao.findall();
        }

        public MyContentObserver(Handler handler) {
            super(handler);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;//停止线程

        unregisterReceiver(mReceiver);
        mReceiver = null;

        getContentResolver().unregisterContentObserver(mObserver);
        mObserver = null;
    }

    class MyReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            mSkipPackage = intent.getStringExtra("package");
        }
    }
}
