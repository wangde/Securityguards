package com.hlju.wangde.securityguards.Acitivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hlju.wangde.securityguards.R;
import com.hlju.wangde.securityguards.utils.PrefUtils;

public class DragViewActivity extends AppCompatActivity {

    private ImageView ivDrag;
    private int startY;
    private int startX;
    private int mScreenHeight;
    private int mScreenWidth;
    private TextView tvTop;
    private TextView tvBottom;
    long[] mHits = new long[2];

    /**
     * 归属地提示框修改位置页面
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();

        ivDrag = (ImageView) findViewById(R.id.iv_drag);
        tvTop = (TextView) findViewById(R.id.tv_top);
        tvBottom = (TextView) findViewById(R.id.tv_bottom);

        int lastX = PrefUtils.getInt("lastX", 0, this);
        int lastY = PrefUtils.getInt("lastY", 0, this);

        if (lastY > mScreenHeight / 2) {
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        } else {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        }

//        ivDrag.layout(lastX,lastY,lastX+ivDrag.getWidth(),lastY+ivDrag.getHeight());//不能再onCreate中执行

        //通过修改布局参数，设定位置
        ConstraintLayout.LayoutParams layoutParams =
                (ConstraintLayout.LayoutParams) ivDrag.getLayoutParams();//父控件是谁获取谁的布局参数
        layoutParams.topMargin = lastY;
        layoutParams.leftMargin = lastX;

        ivDrag.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下
                        System.out.println("按下");
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //移动
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();

                        //计算偏移量
                        int dx = endX - startX;
                        int dy = endY - startY;
                        //根据偏移量更新位置
                        int l = ivDrag.getLeft() + dx;//最新左边距
                        int r = ivDrag.getRight() + dx;
                        int t = ivDrag.getTop() + dy;
                        int b = ivDrag.getBottom() + dy;
                        //防止控件超出屏幕
                        if (l < 0 || r > mScreenWidth) {
                            return true;
                        }
                        if (t < 0 || b > mScreenHeight - 25) {
                            return true;
                        }
                        //根据当前位置显示文本框提示
                        if (t > mScreenHeight / 2) {
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        } else {
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        }
                        ivDrag.layout(l, t, r, b);
                        System.out.println("移动");

                        //重新初始化起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起
                        //保存当前位置
                        PrefUtils.putInt("lastX", ivDrag.getLeft(), getApplicationContext());
                        PrefUtils.putInt("lastY", ivDrag.getTop(), getApplicationContext());
                        System.out.println("抬起");
                }
//                return true;//true表示自身处理该事件
                return false;//同时设置onTouc和onClick保证同时响应
            }
        });
        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();//开机时间
                if (SystemClock.uptimeMillis() - mHits[0] <= 500) {
                    ivDrag.layout(mScreenWidth / 2 - ivDrag.getWidth() / 2, ivDrag.getTop(),
                            mScreenWidth / 2 + ivDrag.getWidth() / 2, ivDrag.getBottom());
                }
            }
        });
    }
}
