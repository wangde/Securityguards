package com.hlju.wangde.securityguards.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by XiaoDe on 2017/8/23 10:15.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class ProcessInfo {

    public String name;
    public String packageName;
    public Drawable icon;
    public long memory;
    public boolean isUser = true;//true代表用户进程
    public boolean isChecked = false;//表示是否勾选
}
