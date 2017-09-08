package com.hlju.wangde.securityguards.Acitivity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.db.dao.BlackNumberDao;
import com.hlju.wangde.securityguards.domain.BlackNumberInfo;
import com.hlju.wangde.securityguards.utils.ToastUtils;

import java.util.ArrayList;

/**
 * 黑名单管理
 *
 * @author XiaoDe
 */
public class BlackNumberActivity extends AppCompatActivity {

    private ListView listView;
    private BlackNumberDao mDao;
    private ArrayList<BlackNumberInfo> mList;
    private BlackNumberAdapter mAdapter;
    private ProgressBar pbLoading;
    private int mIndex;
    private boolean isLoading;//标记当前是否正在加载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);
        mDao = BlackNumberDao.getInstance(this);

        listView = (ListView) findViewById(R.id.iv_black_number);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    //判断是否到底部
                    int lastVisiblePosition = listView.getLastVisiblePosition();
                    if (lastVisiblePosition >= mList.size() - 1 && isLoading == false) {
                        System.out.println("到达底部");
                        int totalCount = mDao.getTotalCount();
                        if (mList.size() < totalCount) {
                            initData();
                        } else {
                            ToastUtils.showToast(getApplicationContext(), "已经没有更多数据");
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        initData();

    }

    private void initData() {
        isLoading = true;

        pbLoading.setVisibility(View.VISIBLE);

        new Thread() {
            @Override
            public void run() {
//                mList = mDao.findall();
                if (mList == null) {
                    mList = mDao.findpart(mIndex);
                } else {
                    ArrayList<BlackNumberInfo> findpart = mDao.findpart(mIndex);
                    mList.addAll(findpart);//追加集合
                }

                runOnUiThread(new Runnable() {//运行在主线程
                    @Override
                    public void run() {
                        //第一页数据初始化
                        if (mAdapter == null) {
                            mAdapter = new BlackNumberAdapter();
                            listView.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();//基于当前数据进行刷新
                        }

                        mIndex = mList.size();
                        pbLoading.setVisibility(View.GONE);
                        isLoading = false;

                    }
                });


            }
        }.start();


    }

    public void addBlackNumber(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_add_black_number, null);
        dialog.setView(view, 0, 0, 0, 0);//去掉黑边兼容2.x版本

        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

        final EditText etBlackNumber = (EditText) view.findViewById(R.id.et_black_number);
        final RadioGroup rgGroup = (RadioGroup) view.findViewById(R.id.rg_group);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = etBlackNumber.getText().toString().trim();

                if (!TextUtils.isEmpty(phone)) {
                    int id = rgGroup.getCheckedRadioButtonId();
                    int mode = 1;
                    switch (id) {
                        case R.id.rb_phone:
                            mode = 1;
                            break;
                        case R.id.rb_sms:
                            mode = 2;
                            break;
                        case R.id.rb_all:
                            mode = 3;
                            break;
                        default:
                            break;
                    }
                    mDao.add(phone, mode);
                    BlackNumberInfo addInfo = new BlackNumberInfo();
                    addInfo.number = phone;
                    addInfo.mode = mode;
                    mList.add(0, addInfo);
                    mAdapter.notifyDataSetChanged();
                    dialog.dismiss();

                } else {
                    ToastUtils.showToast(getApplicationContext(), "输入内容不能为空");
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    class BlackNumberAdapter extends BaseAdapter {


        public BlackNumberAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public BlackNumberInfo getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //listview重用防止内存溢出
        //convertView 保证view不会被创建多次，导致内存溢出
        //使用ViewHold减少findviewbyid次数
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder = null;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.list_item_black, null);
                TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);
                TextView tvMode = (TextView) view.findViewById(R.id.tv_mode);
                ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete);
                holder = new ViewHolder();
                holder.tvNumber = tvNumber;
                holder.tvMode = tvMode;
                holder.ivDelete = ivDelete;

                view.setTag(holder);//将hodler对象，通过打标机繁琐保存在View中和View绑定

            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }


            final BlackNumberInfo info = getItem(position);
            holder.tvNumber.setText(info.number);
            switch (info.mode) {
                case 1:
                    holder.tvMode.setText("拦截电话");
                    break;
                case 2:
                    holder.tvMode.setText("拦截短信");
                    break;
                case 3:
                    holder.tvMode.setText("拦截全部");
                    break;
                default:
                    break;
            }

            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //从数据库中删除
                    //从集合中删除
                    //刷新Listview
                    mDao.delete(info.number);
                    mList.remove(info);
                    mAdapter.notifyDataSetChanged();
                }
            });
            return view;
        }
    }

    static class ViewHolder {
        public TextView tvNumber;
        public TextView tvMode;
        public ImageView ivDelete;
    }
}
