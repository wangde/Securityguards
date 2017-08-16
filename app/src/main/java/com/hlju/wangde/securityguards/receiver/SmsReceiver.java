package com.hlju.wangde.securityguards.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.telephony.SmsMessage;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.service.LocationService;
import com.hlju.wangde.securityguards.service.LockScreenService;
import com.hlju.wangde.securityguards.service.WipeDataService;

public class SmsReceiver extends BroadcastReceiver {

    /**
     * 拦截短信
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        String format = intent.getStringExtra("format");
        SmsMessage sms;
        for (Object obj : objs) {
            if (Build.VERSION.SDK_INT < 23) {
                sms = SmsMessage.createFromPdu((byte[]) obj);
            } else {
                sms = SmsMessage.createFromPdu((byte[]) obj, format);
            }
            String originatingAddress = sms.getOriginatingAddress();
            String messageBody = sms.getMessageBody();
            System.out.println("号码：" + originatingAddress + ";内容：" + messageBody);

            if ("#*alarm*#".equals(messageBody)) {
                System.out.println("播放报警音乐");
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);
                player.setLooping(true);//单曲循环
                player.start();
                //4.4以上无法拦截
                //操作短信数据库，删除数据库相关短信内容，间接达到删除短信目的
                abortBroadcast();//中断系统短信
            } else if ("#*location*#".equals(messageBody)) {
                System.out.println("手机定位");
                //启动位置监听活动
                context.startService(new Intent(context, LocationService.class));
                abortBroadcast();
            } else if ("#*lockscreen*#".equals(messageBody)) {
                context.startService(new Intent(context, LockScreenService.class));
                abortBroadcast();

            } else if ("#*location*#".equals(messageBody)) {
                context.startActivity(new Intent(context, WipeDataService.class));
                abortBroadcast();
            }
        }
    }
}
