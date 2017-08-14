package com.hlju.wangde.securityguards.Acitivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by XiaoDe on 2017/8/13 19:36.
 * 设置向导基类
 *
 * @author XiaoDe.
 *         Project Name is Securityguards.
 */

public abstract class BaseSetupActivity extends AppCompatActivity {

    private GestureDetector mDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            /**
             * @param e1        起点坐标
             * @param e2        终点坐标
             * @param velocityX 水平滑动速度
             * @param velocityY 竖直滑动速度
             * @return
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //判断向左滑还是向右
                //e1.getX();//相对父控件x坐标
                //e1.getRawX();//屏幕的绝对坐标
                if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    return true;
                }
                if (Math.abs(velocityX) < 100) {
                    return true;
                }
                if (e2.getRawX() - e1.getRawX() > 200) {//向右滑，上一页
                    showPrevious();
                }
                if (e1.getRawX() - e2.getRawX() > 200) {//向左滑，下一页
                    showNext();
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    /**
     * 按钮上一页
     *
     * @param view
     */
    public void previous(View view) {
        showPrevious();
    }

    public abstract void showPrevious();

    /**
     * 下一页
     *
     * @param view
     */
    public void next(View view) {
        showNext();

    }

    public abstract void showNext();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);//将事件给手势识别器处理
        return super.onTouchEvent(event);
    }
}
