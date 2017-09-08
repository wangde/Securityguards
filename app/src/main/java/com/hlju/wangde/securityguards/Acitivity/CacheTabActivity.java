package com.hlju.wangde.securityguards.Acitivity;

import android.app.TabActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TabHost;

import com.hlju.wangde.securityguards.R;

public class CacheTabActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_tab);

        TabHost tabHost = getTabHost();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Clean_Cache");
        tab1.setIndicator("缓存清理");//系统默认样式
        tab1.setContent(new Intent(this, CleanCacheActivity.class));//页签点击后跳转到缓存清理页面

        TabHost.TabSpec tab2 = tabHost.newTabSpec("Clean_Cache");
        tab2.setIndicator("SDcard缓存清理");//系统默认样式
        tab2.setContent(new Intent(this, SdcardCacheActivity.class));//页签点击后跳转到缓存清理页面

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
    }
}
