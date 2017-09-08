package com.hlju.wangde.securityguards.Acitivity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.domain.ProcessInfo;
import com.hlju.wangde.securityguards.engine.ProcessInfoProvider;
import com.hlju.wangde.securityguards.utils.PrefUtils;
import com.hlju.wangde.securityguards.utils.ToastUtils;

import java.util.ArrayList;

/**
 * 进程管理
 */
public class ProcessManagerActivity extends AppCompatActivity {

    private int mRunningProcessNum;
    private TextView tvRunningNum;
    private long mTotalMemory;
    private long mAvailMemory;
    private TextView tvMemoInfo;
    private ListView lvList;
    private TextView tvHeader;
    private LinearLayout llLoading;
    private ArrayList<ProcessInfo> mUserList;
    private ArrayList<ProcessInfo> mSystemList;
    private ProcessfoAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);
        mRunningProcessNum = ProcessInfoProvider.getRunningProcessNum(this);
        tvRunningNum = (TextView) findViewById(R.id.tv_running_num);
        tvMemoInfo = (TextView) findViewById(R.id.tv_memo_info);
        tvRunningNum.setText(String.format("运行中的进程:%d个", mRunningProcessNum));
        tvHeader = (TextView) findViewById(R.id.tv_header);
        lvList = (ListView) findViewById(R.id.lv_list);
        llLoading = (LinearLayout) findViewById(R.id.ll_loading);
        mAvailMemory = ProcessInfoProvider.getAvailMemory(this);
        mTotalMemory = ProcessInfoProvider.getTotalMemory(this);
        tvMemoInfo.setText(String.format("剩余/总内存:%s/%s",
                Formatter.formatFileSize(this, mAvailMemory),
                Formatter.formatFileSize(this, mTotalMemory)));
        lvList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mUserList != null && mSystemList != null) {
                    if (firstVisibleItem <= mUserList.size()) {
                        tvHeader.setText("用户进程(" + mUserList.size() + ")");
                    } else {
                        tvHeader.setText("系统进程(" + mSystemList.size() + ")");
                    }
                }
            }
        });
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProcessInfo info = mAdapter.getItem(position);
                if (info != null) {
                    if (info.packageName.equals(getPackageName())) {
                        //过滤手机卫士
                        return;
                    }
                    info.isChecked = !info.isChecked;
                    //局部更新checkbox
                    CheckBox cbCheck = (CheckBox) view.findViewById(R.id.cb_check);
                    cbCheck.setChecked(info.isChecked);
                }
            }
        });
        initdata();

    }


    private void initdata() {
        llLoading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                ArrayList<ProcessInfo> list = ProcessInfoProvider.getRunningProcess(getApplicationContext());
                mUserList = new ArrayList<ProcessInfo>();
                mSystemList = new ArrayList<ProcessInfo>();
                for (ProcessInfo processInfo : list) {
                    if (processInfo.isUser) {
                        mUserList.add(processInfo);
                    } else {
                        mSystemList.add(processInfo);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new ProcessfoAdapter();
                        lvList.setAdapter(mAdapter);
                        llLoading.setVisibility(View.GONE);
                    }
                });

            }
        }.start();
    }

    public void selectAll(View view) {
        for (ProcessInfo info : mUserList) {
            if (info.packageName.equals(getPackageName())) {
                //过滤手机卫士
                continue;
            }
            info.isChecked = true;
        }

        boolean showSystem = PrefUtils.getBoolean("show_system", true, this);
        if (showSystem) {//如果不展示系统进程就不选择系统进程
            for (ProcessInfo info : mSystemList) {
                info.isChecked = true;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void reverseSelect(View view) {
        for (ProcessInfo info : mUserList) {
            if (info.packageName.equals(getPackageName())) {
                //过滤手机卫士
                continue;
            }
            info.isChecked = !info.isChecked;
        }
        boolean showSystem = PrefUtils.getBoolean("show_system", true, this);
        if (showSystem) {//如果不展示系统进程就不选择系统进程
            for (ProcessInfo info : mSystemList) {
                info.isChecked = !info.isChecked;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public void killAll(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ArrayList<ProcessInfo> killedList = new ArrayList<ProcessInfo>();
        for (ProcessInfo info : mUserList) {
            if (info.isChecked) {
                am.killBackgroundProcesses(info.packageName);
//                mUserList.remove(info);
                killedList.add(info);
            }
        }
        boolean showSystem = PrefUtils.getBoolean("show_system", true, this);
        if (showSystem) {//如果不展示系统进程就不清理系统进程
            for (ProcessInfo info : mSystemList) {
                if (info.isChecked) {
                    am.killBackgroundProcesses(info.packageName);
//                mSystemList.remove(info);
                    killedList.add(info);
                }
            }
        }
        long savedMemory = 0;
        for (ProcessInfo processInfo : killedList) {
            if (processInfo.isUser) {
                mUserList.remove(processInfo);
            } else {
                mSystemList.remove(processInfo);
            }
            savedMemory += processInfo.memory;

        }
        mAdapter.notifyDataSetChanged();
        ToastUtils.showToast(this,
                String.format("帮您结束了%d个进程，共节省了%s空间",
                        killedList.size(), Formatter.formatFileSize(this, savedMemory)));
        //更新文本信息
        mRunningProcessNum -= killedList.size();
        mAvailMemory += savedMemory;
        tvRunningNum.setText(String.format("运行中的进程:%d个", mRunningProcessNum));
        tvMemoInfo.setText(String.format("剩余/总内存:%s/%s",
                Formatter.formatFileSize(this, mAvailMemory),
                Formatter.formatFileSize(this, mTotalMemory)));

    }

    public void setting(View view) {
        startActivityForResult(new Intent(this, ProcessSettingActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.notifyDataSetChanged();//重新走一次getcount方法
    }

    class ProcessfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            boolean showSystem = PrefUtils.getBoolean("show_system", true, getApplicationContext());
            if (showSystem) {
                return mUserList.size() + mSystemList.size() + 2;//显示系统进程
            } else {
                return mUserList.size() + 1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
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
                        headerHolder.tvHeader.setText("用户进程(" + mUserList.size() + ")");
                    } else {
                        headerHolder.tvHeader.setText("系统进程(" + mSystemList.size() + ")");
                    }
                    break;
                case 1:
                    ViewHolder holder = null;
                    if (convertView == null) {
                        convertView = View.inflate(getApplicationContext(), R.layout.list_item_processinfo, null);
                        holder = new ViewHolder();
                        holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                        holder.tvMemory = (TextView) convertView.findViewById(R.id.tv_memory);
                        holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                        holder.cbCheck = (CheckBox) convertView.findViewById(R.id.cb_check);

                        convertView.setTag(holder);

                    } else {
//                view = convertView;
                        holder = (ViewHolder) convertView.getTag();
                    }
                    ProcessInfo info = getItem(position);
                    holder.tvName.setText(info.name);
                    holder.ivIcon.setImageDrawable(info.icon);
                    holder.tvMemory.setText(Formatter.formatFileSize(getApplicationContext(), info.memory));
                    holder.cbCheck.setChecked(info.isChecked);
                    if (info.packageName.equals(getPackageName())) {
                        holder.cbCheck.setVisibility(View.INVISIBLE);
                    } else {
                        holder.cbCheck.setVisibility(View.VISIBLE);
                    }
                    break;
            }
//            View view = null;

            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tvName;
        public TextView tvMemory;
        public ImageView ivIcon;
        public CheckBox cbCheck;
    }

    static class HeaderHolder {
        public TextView tvHeader;
    }

}
