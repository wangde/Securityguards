package com.hlju.wangde.securityguards.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Debug;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.domain.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiaoDe on 2017/8/22 22:23.
 * 进程信息提供者
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class ProcessInfoProvider {

    /**
     * 获取正在运行进程数量
     */
    public static int getRunningProcessNum(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    /**
     * 获取空闲内存大小
     *
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;

    }

    /**
     * 获取总内存大小
     *
     * @param context
     * @return
     */
    public static long getTotalMemory(Context context) {
        if (Build.VERSION.SDK_INT >= 16) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
            am.getMemoryInfo(outInfo);
            return outInfo.totalMem;
        } else {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("/proc/meminfo"));
                String readLine = reader.readLine();
                char[] charArray = readLine.toCharArray();
                StringBuffer sb = new StringBuffer();
                for (char c : charArray) {
                    if (c >= '0' && c <= '9') {
                        sb.append(c);
                    }
                }
                String total = sb.toString();
                return Long.parseLong(total) * 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }


    }

    /**
     * 返回正在运行进程列表
     *
     * @param context
     * @return
     */
    public static ArrayList<ProcessInfo> getRunningProcess(Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();
        ArrayList<ProcessInfo> list = new ArrayList<ProcessInfo>();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            ProcessInfo info = new ProcessInfo();
            String processName = runningAppProcessInfo.processName;
            int pid = runningAppProcessInfo.pid;
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{pid});//根据Pid返回内存信息
            long memory = processMemoryInfo[0].getTotalPrivateDirty() * 1024;//占用内存大小 单位kb
            info.memory = memory;
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processName, 0);
                String name = applicationInfo.loadLabel(pm).toString();
                Drawable icon = applicationInfo.loadIcon(pm);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    info.isUser = false;
                } else {
                    info.isUser = true;
                }
                info.packageName = processName;
                info.name = name;
                info.icon = icon;

            } catch (PackageManager.NameNotFoundException e) {
                //某些进程没有名称和图标
                info.name = processName;
                info.packageName = processName;
                info.icon = context.getResources().getDrawable(R.mipmap.ic_launcher);
                info.isUser = false;
                e.printStackTrace();
            }
            list.add(info);
        }
        return list;

    }

    /**
     * 清理所有后台进程
     *
     * @param context
     */
    public static void killAll(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningAppProcess : runningAppProcesses) {
            String processName = runningAppProcess.processName;
            if (processName.equals(context.getPackageName())) {
                continue;
            }
            am.killBackgroundProcesses(runningAppProcess.processName);
        }

    }

}
