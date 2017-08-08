package com.hlju.wangde.securityguards.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by XiaoDe on 2017/8/6 20:37.
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public class FocusedTextView extends android.support.v7.widget.AppCompatTextView {

    //从代码中new
    public FocusedTextView(Context context) {
        super(context);
    }
    //当布局文件有属性时调用
    public FocusedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    //当有属性和样式style时
    public FocusedTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //强制获取焦点
    @Override
    public boolean isFocused() {
        return true;
    }
}
