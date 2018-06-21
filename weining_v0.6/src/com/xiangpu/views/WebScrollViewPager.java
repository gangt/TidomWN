package com.xiangpu.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.ScreenUtil;

/**
 * Created by Administrator on 2017/10/12 0012.
 * Info：重写的viewpager，在特定区域响应viewpager的自身滑动
 */

public class WebScrollViewPager extends ViewPager {
    private int screenWidth = 0; // 手机屏幕的宽
    private int screenHeight = 0; // 手机屏幕的高
    private int startX = 0;
    private int startY = 0;
    private boolean eventFlag = false;

    public WebScrollViewPager(Context context) {
        this(context, null);
    }

    public WebScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenWidth = ScreenUtil.getScreenWidth(context);
        screenHeight = ScreenUtil.getScreenHeight(context);
        LogUtil.d("screenWidth: " + screenWidth + " screenHeight: " + screenHeight);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        switch (arg0.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取手指刚按屏幕的初始位置
                startX = (int) arg0.getRawX();
                startY = (int) arg0.getRawY();
                LogUtil.d("startX: " + startX + " startY: " + startY);
                // 在特定区域拦截事件
                if (startY < screenHeight * 3 / 4) {
                    if ((startX > 0 && startX < 50) || (startX > screenWidth - 50 && startX < screenWidth)) {
                        eventFlag = true;
                    } else {
                        eventFlag = false;
                    }
                } else {
                    eventFlag = false;
                }
        }
        return eventFlag;
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        super.onTouchEvent(arg0);
        return true;
    }

}
