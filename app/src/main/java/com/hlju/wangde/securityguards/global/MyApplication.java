package com.hlju.wangde.securityguards.global;

import android.app.Application;
import android.os.Environment;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by XiaoDe on 2017/8/27 20:06.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new MyHandler());

    }

    class MyHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            System.out.println("发现一未处理异常");
            e.printStackTrace();

            //收集崩溃日志
            try {
                PrintWriter err = new PrintWriter(Environment.getExternalStorageDirectory() + "/err.log");
                e.printStackTrace(err);
                err.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }

            //停止当前进程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
