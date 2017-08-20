package com.hlju.wangde.securityguards.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by XiaoDe on 2017/8/16 22:32.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class ServiceStatusUtils {

    /**
     * 判断服务是否运行
     *
     * @param serviceName
     * @param context
     * @return
     */
    public static boolean isServiceRuning(String serviceName, Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(serviceName)) {
                return true;//服务正在运行
            }
//            System.out.println(className);
        }

        return false;
    }
}
