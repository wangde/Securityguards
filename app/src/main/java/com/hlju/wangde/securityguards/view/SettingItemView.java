package com.hlju.wangde.securityguards.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;

/**
 * Created by XiaoDe on 2017/8/8 10:08.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class SettingItemView extends RelativeLayout {
    public static final String NAMESPACE = "http://com.hlju.wangde.securityguards";
    private TextView tvTitile;
    private TextView tvDesc;
    private CheckBox cbCheck;
    private String mDescOn;
    private String mDescOff;

    public SettingItemView(Context context) {
        super(context);
        initView();
    }


    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
//        int count = attrs.getAttributeCount();
//        for (int i = 0 ; i< count ; i++){
//            String attributeName = attrs.getAttributeName(i);
//            String attributeValue = attrs.getAttributeValue(i);
//            System.out.println(attributeName+":"+attributeValue);
//        }
        String title = attrs.getAttributeValue(NAMESPACE, "title");

        mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
        mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
        setTitle(title);
//        System.out.println(title+mDescOn+mDescOff);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        View child = View.inflate(getContext(), R.layout.setting_item_view, null);//初始化组合控件布局

        tvTitile = (TextView) child.findViewById(R.id.tv_title);
        tvDesc = (TextView) child.findViewById(R.id.tv_desc);
        cbCheck = (CheckBox) child.findViewById(R.id.cb_check);
        this.addView(child);//把当前布局添加给当前的RelativeLayout对象
    }

    public void setTitle(String titile) {
        tvTitile.setText(titile);
    }

    public void setDesc(String desc) {
        tvDesc.setText(desc);
    }

    public boolean isChecked() {
        return cbCheck.isChecked();
    }

    public void setChecked(boolean checked) {
        cbCheck.setChecked(checked);
        if(checked){
            setDesc(mDescOn);
        }else {
            setDesc(mDescOff);
        }
    }

}
