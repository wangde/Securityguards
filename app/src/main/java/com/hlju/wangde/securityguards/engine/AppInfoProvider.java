package com.hlju.wangde.securityguards.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.hlju.wangde.securityguards.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiaoDe on 2017/8/21 14:52.
 * 应用信息提供者
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class AppInfoProvider {

    /**
     * 获取已安装应用
     */
    public static ArrayList<AppInfo> getInstalledApps(Context context) {

        PackageManager pm = context.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);//获取已安装的包
        ArrayList<AppInfo> list = new ArrayList<AppInfo>();
        for (PackageInfo packageInfo : installedPackages) {
            AppInfo info = new AppInfo();
            String packageName = packageInfo.packageName;//包名
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;//应用信息
            String name = applicationInfo.loadLabel(pm).toString();//应用名称
            Drawable icon = applicationInfo.loadIcon(pm);//应用名称

            if (name == null) {
                info.name = packageName;
            } else {
                info.name = name;
            }
            info.packageName = packageName;
            info.icon = icon;

            int flags = applicationInfo.flags;//获取应用标记
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                info.isRom = false;
            } else {
                info.isRom = true;
            }
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                info.isUser = false;
            } else {
                info.isUser = true;
            }

            list.add(info);

        }
        return list;

    }
}
