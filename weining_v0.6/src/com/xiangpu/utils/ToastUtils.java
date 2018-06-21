package com.xiangpu.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.konecty.rocket.chat.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * description: 提示框
 * autour: Andy
 * date: 2017/11/29 10:36
 * update: 2017/11/29
 * version: 1.0
 */
public class ToastUtils {

    private static final int DEFAULT_TIME = 1000;

    /**
     * toast居中显示
     *
     * @param context 上下文
     * @param str     提示语
     */
    public static void showCenterToast(Context context, String str) {
        showToast(context, Gravity.CENTER, str);
    }

//    /**
//     * 自定义toast
//     *
//     * @param context
//     * @param text
//     */
//    public static void customCenterToast(Context context, CharSequence text) {
//        Toast toast = new Toast(context);
//        View view = LayoutInflater.from(context).inflate(R.layout.title_notice_layout, null);
//        TextView textView = (TextView) view.findViewById(R.id.notice_txt);
//        textView.setText(text);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setView(view);
//        toast.show();
//    }

    /**
     * toast自定义位置
     *
     * @param context 上下文
     * @param gravity 位置
     * @param str     提示语
     */
    public static void showToast(Context context, int gravity, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_LONG);
        View view = toast.getView();
        if (view != null) {
            view.setBackgroundResource(R.drawable.toast_bg);
            view.setPadding(SizeUtils.convertDp2Px(context, 24), SizeUtils.convertDp2Px(context, 18),
                    SizeUtils.convertDp2Px(context, 24), SizeUtils.convertDp2Px(context, 18));
            TextView message = ((TextView) view.findViewById(android.R.id.message));
            message.setTextColor(Color.WHITE);
            message.setTextSize(14);
        }
        toast.setGravity(gravity, 0, 0);
        toast.setText(str);
        showThemeToast(toast, DEFAULT_TIME);
    }

    /**
     * toast提示框（自定义显示时间）
     *
     * @param toast toast实例
     * @param cnt   显示时间（毫秒）
     */
    public static void showThemeToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt);
    }

}
