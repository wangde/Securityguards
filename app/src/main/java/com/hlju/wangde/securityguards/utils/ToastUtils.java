package com.hlju.wangde.securityguards.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by XiaoDe on 2017/8/8 20:12.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class ToastUtils {
    public static void showToast(Context context,String text){

        Toast.makeText(context,text,Toast.LENGTH_SHORT).show();
    }
}
