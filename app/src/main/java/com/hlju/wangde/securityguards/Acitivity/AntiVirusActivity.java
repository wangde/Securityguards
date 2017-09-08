package com.hlju.wangde.securityguards.Acitivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.db.dao.AntiVirusDao;
import com.hlju.wangde.securityguards.utils.MD5Utils;
import com.hlju.wangde.securityguards.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * android:screenOrientation="portrait"强制竖屏
 * android:configChanges="orientation|screenSize|keyboardHidden 可以横屏但不会再onCreate
 */
public class AntiVirusActivity extends AppCompatActivity {

    private static final int STATE_UPDATE_STATUS = 1;
    private static final int STATE_SCAN_FINISH = 2;

    private ProgressBar pbProgress;
    private TextView tvStatus;
    private ImageView ivSacnning;
    private LinearLayout llContainer;

    // 病毒集合
    private ArrayList<ScanInfo> mVirusList = new ArrayList<AntiVirusActivity.ScanInfo>();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STATE_UPDATE_STATUS:
                    ScanInfo info = (ScanInfo) msg.obj;
                    tvStatus.setText("正在扫描:" + info.name);

                    TextView view = new TextView(getApplicationContext());
                    if (info.isVirus) {
                        view.setText("发现病毒：" + info.name);
                        view.setTextColor(Color.RED);
                    } else {
                        view.setText("扫描安全：" + info.name);
                        view.setTextColor(Color.BLACK);
                    }

//                    llContainer.addView(view);
                    llContainer.addView(view, 0);
                    break;
                case STATE_SCAN_FINISH:
                    tvStatus.setText("扫描完毕");
                    ivSacnning.clearAnimation();
                    if (!mVirusList.isEmpty()) {
                        showAlertDialog();
                    } else {
                        ToastUtils.showToast(getApplicationContext(),
                                "您的手机很安全,请放心使用!");
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("严重警告!");
        builder.setMessage("发现" + mVirusList.size() + "个病毒, 建议立即处理!!!");
        builder.setCancelable(false);
        builder.setPositiveButton("立即处理",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 卸载病毒
                        for (ScanInfo info : mVirusList) {
                            Intent intent = new Intent(Intent.ACTION_DELETE);
                            intent.setData(Uri.parse("package:"
                                    + info.packageName));
                            startActivity(intent);
                        }
                    }
                });

        builder.setNegativeButton("以后再说", null);
        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        ivSacnning = (ImageView) findViewById(R.id.iv_scanning);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
        llContainer = (LinearLayout) findViewById(R.id.ll_container);

        RotateAnimation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2000);
        animation.setInterpolator(new LinearInterpolator());//线性插补器
        animation.setRepeatCount(Animation.INFINITE);//无线循环
        ivSacnning.startAnimation(animation);

        new Thread() {
            @Override
            public void run() {

//                for(int i = 0;i<100;i++){
//                    pbProgress.setProgress(i);
//                    SystemClock.sleep(100);
//                }
                SystemClock.sleep(2000);//展示正在初始化引擎文字

                PackageManager pm = getPackageManager();
                //获取已安装app，某些app卸载后仍残留data/data目录数据，也要加载
                List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
                pbProgress.setMax(installedPackages.size());
                int progress = 0;
//                Random random = new Random();
                for (PackageInfo packageInfo : installedPackages) {
                    ScanInfo info = new ScanInfo();
                    String packageName = packageInfo.packageName;
                    String name = packageInfo.applicationInfo.loadLabel(pm).toString();

                    info.packageName = packageName;
                    info.name = name;
                    //计算文件md5
                    String apkPath = packageInfo.applicationInfo.sourceDir;
                    String md5 = MD5Utils.encodeFile(apkPath);
                    if (AntiVirusDao.isVirus(md5)) {
                        System.out.println("发现病毒:" + name);
                        info.isVirus = true;
                        mVirusList.add(info);
                    } else {
                        System.out.println("扫描安全:" + name);
                        info.isVirus = false;
                    }
                    progress++;
                    pbProgress.setProgress(progress);

                    Message msg = Message.obtain();
                    msg.what = STATE_UPDATE_STATUS;
                    msg.obj = info;

                    mHandler.sendMessage(msg);
//                    SystemClock.sleep(50 + random.nextInt(50));
                }
                mHandler.sendEmptyMessage(STATE_SCAN_FINISH);
            }
        }.start();


    }

    class ScanInfo {
        public boolean isVirus;
        public String name;
        public String packageName;
    }
}
