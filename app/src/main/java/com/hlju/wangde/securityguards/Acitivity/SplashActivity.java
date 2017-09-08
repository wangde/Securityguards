package com.hlju.wangde.securityguards.Acitivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.PrefUtils;
import com.hlju.wangde.securityguards.utils.StreamUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
  * 闪屏页面
  * -展示logo,公司品牌
  * -检测版本更新
  * -项目初始化
  * -校验合法性
  * @author WangDe
  */
public class SplashActivity extends AppCompatActivity {

    private static final int CODE_UPDATE_DIALOG = 1;
    private static final int CODE_ENTER_HOME = 2;
    private static final int CODE_URL_ERROR = 3;
    private static final int CODE_NETWORK_ERROR = 4;
    private static final int CODE_JSON_ERROR = 5;

    private TextView tvName;
    private TextView tvProgress;
    private ConstraintLayout rlRoot;

    //服务器返回的更新信息
    private String mVersionName;//版本名
    private int mVersionCode;//版本号
    private String mDes;//版本描述
    private String mUrl;//下载链接

    private Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "URL异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_NETWORK_ERROR:
                    Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JSON_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvName = (TextView)findViewById(R.id.tv_name);
        tvName.setText("版本名："+ getVersionName());
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        rlRoot = (ConstraintLayout) findViewById(R.id.rl_root);
//        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean autoUpdate = PrefUtils.getBoolean("auto_update", true,this);
        if (autoUpdate) {
            checkVersion();
        } else {
            mHander.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);//发送延时两秒的消息再跳转主页面
        }

        //渐变动画
        AlphaAnimation animation = new AlphaAnimation(0.2f,1);
        animation.setDuration(2000);
        rlRoot.startAnimation(animation);

        copyDb("address.db");//拷贝电话归属地查询数据库
        copyDb("antivirus.db");

    }
    /**
     * 检查版本权限
     */
    private void checkVersion(){
        new Thread(){
            @Override
            public void run() {
                Message msg = Message.obtain();
                HttpURLConnection conn = null;
                long startTime = System.currentTimeMillis();
                try {

                    conn = (HttpURLConnection) new URL("http://").openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);

                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if(responseCode == 200){
                        InputStream in = conn.getInputStream();
                        String result = StreamUtils.stream2String(in);
                        System.out.println("result: "+result);

                        JSONObject jo = new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDes = jo.getString("des");
                        mUrl = jo.getString("url");

                        if (getVersionCode() < mVersionCode) {
                            System.out.println("有更新");
                            msg.what = CODE_UPDATE_DIALOG;
                        } else {
                            System.out.println("无更新");
                            msg.what = CODE_ENTER_HOME;
                        }

                    }

                } catch (MalformedURLException e) {
                    //URL异常
                    e.printStackTrace();
                    msg.what = CODE_URL_ERROR;
                } catch (IOException e) {
                    //网络异常
                    e.printStackTrace();
                    msg.what = CODE_NETWORK_ERROR;
                } catch (JSONException e) {
                    //json解析异常
                    e.printStackTrace();
                    msg.what = CODE_JSON_ERROR;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    long endTime = System.currentTimeMillis();
                    long timeUsed = endTime - startTime;
                    try {
                        if (timeUsed < 2000) {
                            Thread.sleep(2000 - timeUsed);//强制等待两秒
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHander.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 下载APK
     * 权限<uses-permission android:name="android.permission.INTERNET" />
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
     */
    protected void downloadApk() {


        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            tvProgress.setVisibility(View.VISIBLE);
            final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/securityguards.apk";
            HttpUtils utils = new HttpUtils();

            //Xutils
            utils.download(mUrl, path, new RequestCallBack<File>() {
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    //下载进度
                    int percent = (int) (100 * current / total);
//                    System.out.println("下载进度" + percent);
                    tvProgress.setText("下载进度" + percent + "%");
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    String p = responseInfo.result.getAbsolutePath();
                    System.out.println("下载成功" + p);
                    //跳转系统安装页面，安装apk
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),"application/vnd.android.package-archive");
                    startActivity(intent);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            Toast.makeText(this,"没有找到sdcard",Toast.LENGTH_SHORT).show();
        }

    }

    /**
      * 获取版本名称
      */
    private String getVersionName(){
        PackageManager pm = getPackageManager();//包管理器
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;

            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //包名未找到异常
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本号
     *
     * @return 本地版本号
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();//包管理器
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;

            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            //包名未找到异常
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 显示升级弹窗
     */
    protected void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);//必须传一个activity对象
        builder.setTitle("发现新版本：" + mVersionName);
        builder.setMessage(mDes);
//        builder.setCancelable(false);//点返回键弹窗不消失
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();

            }
        });
        builder.show();
        //取消弹窗监听，eg：点返回键
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
    }

    /**
     * 跳转到主页面
     */
    private void enterHome() {

        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
    }

    /**
     * 拷贝号码归属地查询数据库
     */
    public void copyDb(String dbName) {
        //判断文件是否存在如果存在无需拷贝
        InputStream in = null;
        FileOutputStream out = null;
        File filesDir = getFilesDir();
//        System.out.println("filesDir"+filesDir.getAbsolutePath());
        File targetFile = new File(filesDir, dbName);
        if (targetFile.exists()) {
            System.out.println("数据库" + dbName + "已存在无需拷贝");
            return;
        }
        AssetManager assets = getAssets();
        try {

            in = assets.open(dbName);
            //data/data/包名


            out = new FileOutputStream(targetFile);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("数据库:" + dbName + "拷贝成功");

    }
}
