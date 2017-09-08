package com.hlju.wangde.securityguards.Acitivity;

import android.media.tv.TvContentRating;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.db.dao.AppLockDao;
import com.hlju.wangde.securityguards.domain.AppInfo;
import com.hlju.wangde.securityguards.engine.AppInfoProvider;

import java.util.ArrayList;

public class AppLockActivity extends AppCompatActivity implements OnClickListener {

    private Button btnUnlock;
    private Button btnLock;
    private LinearLayout llLock;
    private LinearLayout llUnlock;
    private ArrayList<AppInfo> mUnlockList;
    private ArrayList<AppInfo> mLockList;
    private AppLockAdapter mUnlockAdapter;
    private ListView lvLock;
    private ListView lvUnlock;
    private AppLockDao mDao;
    private AppLockAdapter mLockAdapter;
    private TextView tvLock;
    private TextView tvUnlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);
        mDao = AppLockDao.getInstance(this);
        btnUnlock = (Button) findViewById(R.id.btn_unlock);

        btnLock = (Button) findViewById(R.id.btn_lock);
        btnLock.setOnClickListener(this);
        btnUnlock.setOnClickListener(this);
        llUnlock = (LinearLayout) findViewById(R.id.ll_unlock);
        llLock = (LinearLayout) findViewById(R.id.ll_lock);
        lvUnlock = (ListView) findViewById(R.id.lv_unlock);
        lvLock = (ListView) findViewById(R.id.lv_lock);
        tvUnlock = (TextView) findViewById(R.id.tv_unlock);
        tvLock = (TextView) findViewById(R.id.tv_lock);
        initData();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                ArrayList<AppInfo> list = AppInfoProvider.getInstalledApps(getApplicationContext());
                mLockList = new ArrayList<AppInfo>();
                mUnlockList = new ArrayList<AppInfo>();
                for (AppInfo appInfo : list) {
                    if (mDao.find(appInfo.packageName)) {
                        //已加锁
                        mLockList.add(appInfo);
                    } else {
                        mUnlockList.add(appInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        mUnlockAdapter = new AppLockAdapter(false);
                        lvUnlock.setAdapter(mUnlockAdapter);

                        mLockAdapter = new AppLockAdapter(true);
                        lvLock.setAdapter(mLockAdapter);
                    }
                });
            }
        }.start();
    }

    /**
     * 更新已加锁未加锁数量
     */
    private void updateLockNum() {
        tvUnlock.setText("未加锁软件:" + mUnlockList.size() + "个");
        tvLock.setText("已加锁软件:" + mLockList.size() + "个");
    }


    class AppLockAdapter extends BaseAdapter {

        private boolean isLock;
        private final TranslateAnimation animaLeft;
        private final TranslateAnimation animaRight;

        public AppLockAdapter(boolean isLock) {
            this.isLock = isLock;
            //左移动画
            animaLeft = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, -1,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);
            animaLeft.setDuration(500);


            //右移动画
            animaRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0);
            animaRight.setDuration(500);
        }

        @Override
        public int getCount() {
            updateLockNum();
            if (isLock) {
                return mLockList.size();
            } else {
                return mUnlockList.size();
            }
        }

        @Override
        public AppInfo getItem(int position) {
            if (isLock) {
                return mLockList.get(position);
            } else {
                return mUnlockList.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.list_item_applock, null);
                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.ivLock = (ImageView) convertView.findViewById(R.id.iv_lock);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final View view = convertView;
            final AppInfo info = getItem(position);
            holder.tvName.setText(info.name);
            holder.ivIcon.setImageDrawable(info.icon);
            holder.ivLock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isLock) {


                        animaLeft.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                mDao.delete(info.packageName);
                                mUnlockList.add(info);
                                mLockList.remove(info);
                                mUnlockAdapter.notifyDataSetChanged();
                                mLockAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        view.startAnimation(animaLeft);


                    } else {


                        animaRight.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                //动画开始
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                //1.给数据库增加已加锁数据
                                //2.已加锁集合增加对象
                                //3.未加锁集合减少对象
                                //4.刷新listView
                                mDao.add(info.packageName);
                                mLockList.add(info);
                                mUnlockList.remove(info);
                                mUnlockAdapter.notifyDataSetChanged();
                                mLockAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                //动画重复
                            }
                        });
                        //异步执行
                        view.startAnimation(animaRight);


                    }

                }
            });

            if (isLock) {
                holder.ivLock.setImageResource(R.mipmap.unlock);
            } else {
                holder.ivLock.setImageResource(R.mipmap.lock);
            }
            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tvName;
        public ImageView ivIcon;
        public ImageView ivLock;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_unlock:
                System.out.println("unlock");
                llUnlock.setVisibility(View.VISIBLE);
                llLock.setVisibility(View.GONE);
                btnUnlock.setBackgroundResource(R.mipmap.tab_left_pressed);
                btnLock.setBackgroundResource(R.mipmap.tab_right_default);
                break;
            case R.id.btn_lock:
                System.out.println("lock");
                llLock.setVisibility(View.VISIBLE);
                llUnlock.setVisibility(View.GONE);
                btnUnlock.setBackgroundResource(R.mipmap.tab_left_default);
                btnLock.setBackgroundResource(R.mipmap.tab_right_pressed);
                break;
            default:
                break;
        }
    }
}
