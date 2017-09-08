package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.domain.AppInfo;
import com.hlju.wangde.securityguards.engine.AppInfoProvider;

import java.util.ArrayList;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {


    private ArrayList<AppInfo> mList;
    private ArrayList<AppInfo> mUserList;
    private ArrayList<AppInfo> mSystemList;
    private ListView lvList;
    private AppInfoAdapter mAdapter;
    private LinearLayout llLoading;
    private TextView tvHeader;
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private AnimationSet mPopupAnimSet;
    private AppInfo mCurrenAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        String sdcardSpace = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        String romSpace = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());

        TextView tvSdcard = (TextView) findViewById(R.id.tv_sdcard_avail);
        TextView tvRom = (TextView) findViewById(R.id.tv_rom_avail);
        tvHeader = (TextView) findViewById(R.id.tv_header);
        tvSdcard.setText("sdcard可用：" + sdcardSpace);
        tvRom.setText("内部存储可用：" + romSpace);
        lvList = (ListView) findViewById(R.id.lv_list);

        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        lvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mUserList != null && mSystemList != null) {
                    if (firstVisibleItem <= mUserList.size()) {
                        tvHeader.setText("用户应用(" + mUserList.size() + ")");
                    } else {
                        tvHeader.setText("系统应用(" + mSystemList.size() + ")");
                    }
                }
            }
        });
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo info = mAdapter.getItem(position);
                if (info != null) {
                    mCurrenAppInfo = info;
                    showPopupWindow(view);
                }
            }
        });
        initData();

    }

    private void showPopupWindow(View view) {
        if (mPopupWindow == null) {
            mPopupView = View.inflate(this, R.layout.popup_item_appinfo, null);
            mPopupWindow = new PopupWindow(mPopupView,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());

            TextView tvUninstall = (TextView) mPopupView.findViewById(R.id.tv_uninstall);
            TextView tvLaunch = (TextView) mPopupView.findViewById(R.id.tv_launch);
            TextView tvShare = (TextView) mPopupView.findViewById(R.id.tv_share);

            tvUninstall.setOnClickListener(this);
            tvLaunch.setOnClickListener(this);
            tvShare.setOnClickListener(this);

            //弹窗动画效果
            //渐变动画
            AlphaAnimation animAlpha = new AlphaAnimation(0, 1);
            animAlpha.setDuration(500);

            //缩放动画
            ScaleAnimation animScale = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
            animScale.setDuration(500);

            mPopupAnimSet = new AnimationSet(true);
            mPopupAnimSet.addAnimation(animAlpha);
            mPopupAnimSet.addAnimation(animScale);
        }

        mPopupWindow.showAsDropDown(view, 100, -view.getHeight());
        mPopupView.setAnimation(mPopupAnimSet);

    }

    /**
     * 获取可用空间
     */
    private String getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        long availableBlocks;
        long blockSize;
        long availSize;
        if (Build.VERSION.SDK_INT >= 18) {
            availableBlocks = statFs.getAvailableBlocksLong();
            blockSize = statFs.getBlockSizeLong();
        } else {
            //获取可用存储块数量
            availableBlocks = statFs.getAvailableBlocks();
            blockSize = statFs.getBlockSize();//每个存储块大小
        }
        availSize = availableBlocks * blockSize;
        return Formatter.formatFileSize(this, availSize);//将字节转为带有相应单位的字符串
    }

    private void initData() {

        llLoading.setVisibility(View.VISIBLE);
        new Thread() {

            @Override
            public void run() {
                mList = AppInfoProvider.getInstalledApps(getApplicationContext());
                mUserList = new ArrayList<AppInfo>();
                mSystemList = new ArrayList<AppInfo>();
                for (AppInfo info : mList) {
                    if (info.isUser) {
                        mUserList.add(info);
                    } else {
                        mSystemList.add(info);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new AppInfoAdapter();
                        lvList.setAdapter(mAdapter);
                        llLoading.setVisibility(View.GONE);
                    }
                });
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        mPopupWindow.dismiss();
        switch (v.getId()) {
            case R.id.tv_uninstall:
                System.out.println("卸载" + mCurrenAppInfo.name);
                uninstall();
                break;
            case R.id.tv_launch:
                System.out.println("启动");
                launch();
                break;
            case R.id.tv_share:
                System.out.println("分享");
                share();
                break;
        }

    }

    /**
     * 调用系统所有可以分享的app列表，选择app分享
     */
    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("tex/plain");//分享纯文本
        intent.putExtra(Intent.EXTRA_TEXT, "分享一个很好的应用," +
                "下载地址：https://play.google.com/store/apps/details?id=" + mCurrenAppInfo.packageName);
        startActivity(intent);
    }

    private void launch() {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(mCurrenAppInfo.packageName);
        if (intent != null) {
            startActivity(intent);
        }
    }

    //卸载应用
    private void uninstall() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + mCurrenAppInfo.packageName));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //卸载后重新加载列表
        initData();
    }

    class AppInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUserList.size() + mSystemList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == mUserList.size() + 1) {
                return null;
            }
            if (position < mUserList.size() + 1) {
                return mUserList.get(position - 1);
            } else {
                return mSystemList.get(position - mUserList.size() - 2);
            }
//            return mList.get(position);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        //根据当前位置展示不同样式
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mUserList.size() + 1) {
                return 0;//标题类型
            } else {
                return 1;//普通类型
            }
//            return super.getItemViewType(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            int type = getItemViewType(position);
            switch (type) {
                case 0:
                    HeaderHolder headerHolder = null;
                    if (convertView == null) {
                        convertView = View.inflate(getApplicationContext(), R.layout.list_item_header, null);
                        headerHolder = new HeaderHolder();
                        headerHolder.tvHeader = (TextView) convertView.findViewById(R.id.tv_header);
                        convertView.setTag(headerHolder);
                    } else {
                        headerHolder = (HeaderHolder) convertView.getTag();
                    }
                    if (position == 0) {
                        headerHolder.tvHeader.setText("用户应用(" + mUserList.size() + ")");
                    } else {
                        headerHolder.tvHeader.setText("系统应用g(" + mSystemList.size() + ")");
                    }
                    break;
                case 1:
                    ViewHolder holder = null;
                    if (convertView == null) {
                        convertView = View.inflate(getApplicationContext(), R.layout.list_item_appinfo, null);
                        holder = new ViewHolder();
                        holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                        holder.tvLocation = (TextView) convertView.findViewById(R.id.tv_location);
                        holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);

                        convertView.setTag(holder);

                    } else {
//                view = convertView;
                        holder = (ViewHolder) convertView.getTag();
                    }
                    AppInfo info = getItem(position);
                    holder.tvName.setText(info.name);
                    holder.ivIcon.setImageDrawable(info.icon);
                    if (info.isRom) {
                        holder.tvLocation.setText("手机内存");
                    } else {
                        holder.tvLocation.setText("sdcard");
                    }
                    break;
            }
//            View view = null;

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tvName;
        public TextView tvLocation;
        public ImageView ivIcon;
    }

    static class HeaderHolder {
        public TextView tvHeader;
    }
}
