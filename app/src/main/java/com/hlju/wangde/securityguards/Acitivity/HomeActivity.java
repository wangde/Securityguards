package com.hlju.wangde.securityguards.Acitivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;

/**
 * 主页面
 */
public class HomeActivity extends AppCompatActivity {

    private GridView gvHome;
    private String[] mHomeNames = new String[]{"手机防盗", "通讯卫士", "软件管理", "进程管理", "流量统计",
            "手机杀毒", "缓存清理", "高级工具", "设置中心"};
    private int[] mImageIds = new int[]{R.drawable.home_safe, R.drawable.home_callmsgsafe,
            R.drawable.home_apps, R.drawable.home_taskmanager, R.drawable.home_netmanager,
            R.drawable.home_trojan, R.drawable.home_sysoptimize,R.drawable.home_tools,
            R.drawable.home_settings};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gvHome = (GridView)findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //手机防盗
                        break;
                    case 8:
                        //设置中心
                        startActivity(new Intent(getApplicationContext(),SettingActivity.class));
                        break;
                }
            }
        });
    }

    /**
     * 手机防盗弹窗
     */
    protected void showSafeDialog(){

    }

    /**
     * 设置密码弹窗
     */
    private void showSetPwdDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.create();
    }
    class HomeAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.list_item_home, null);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);

            tvName.setText(mHomeNames[position]);
            ivIcon.setImageResource(mImageIds[position]);
            return view;
        }

        @Override
        public int getCount() {
            return mHomeNames.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
