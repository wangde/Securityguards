package com.hlju.wangde.securityguards.utils;

import android.content.Intent;

import java.io.FileInputStream;
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

    /**
     * 计算文件md5
     *
     * @param filePath
     * @return
     */
    public static String encodeFile(String filePath) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");

            FileInputStream in = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }

            byte[] bytes = digest.digest();

            StringBuffer sb = new StringBuffer();
            for (byte b : bytes) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                // System.out.println(hexString);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }

                sb.append(hexString);
            }

            String md5 = sb.toString();

            return md5;
        } catch (Exception e) {
            // 没有此算法异常
            e.printStackTrace();
        }

        return null;
    }

}
