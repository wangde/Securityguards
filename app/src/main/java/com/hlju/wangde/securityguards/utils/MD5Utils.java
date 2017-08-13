package com.hlju.wangde.securityguards.utils;

import android.content.Intent;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by XiaoDe on 2017/8/12 10:05.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class MD5Utils {

    public static String encode(String password){
        String md5="";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(password.getBytes());

            StringBuffer sb = new StringBuffer();
            for (byte b:bytes){
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if(hexString.length()==1){
                    hexString ="0"+hexString;
                }
                sb.append(hexString);
                md5 = sb.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }finally {
            return md5;
        }

    }
}
