package com.xiangpu.listener;

import android.view.View;

import java.util.Calendar;

/**
 * description: 防止btn点击过快，多次响应
 * autour: Andy
 * date: 2017/11/30 15:35
 * update: 2017/11/30
 * version: 1.0
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;

    public abstract void onNoDoubleClick(View v);

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

}
