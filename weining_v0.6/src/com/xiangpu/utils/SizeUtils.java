package com.xiangpu.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;

import java.math.BigDecimal;

public class SizeUtils {

    /**
     * The absolute height of the display in pixels
     *
     * @param context Context
     * @return px(int)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * The absolute width of the display in pixels
     *
     * @param context Context
     * @return px(int)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Size tool to convert dp to px
     *
     * @param context Context
     * @param dpValue dp unit
     * @return The converted size in pixel
     */
    public static int convertDp2Px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Size tool to convert px to dp
     *
     * @param context Context
     * @param pxValue px unit
     * @return The converted size in pixel
     */
    public static int convertPx2Dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Size tool to convert px to dp
     *
     * @param context Context
     * @param spValue px unit
     * @return The converted size in pixel
     */
    public static int convertSp2Px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * Size tool to convert px to dp
     *
     * @param context Context
     * @param pxValue px unit
     * @return The converted size in pixel
     */
    public static int convertPx2Sp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Gain the width of the widget
     *
     * @param view The view to be measured
     * @return Get the width of the widget
     */
    public static int getWidgetWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredWidth();
    }

    /**
     * Gain the height of the widget
     *
     * @param view The view to be measured
     * @return Get the Height of the widget
     */
    public static int getWidgetHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return view.getMeasuredHeight();
    }

    public static int getWidgetHeightWithObv(final View view) {
        int height = 0;
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewTreeObserver.removeOnPreDrawListener(this);
//                h = view.getMeasuredHeight();
                return true;
            }
        });
        return height;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * java转换数字以万为单位
     *
     * @param num   要转化的数字
     * @param digit 保留的位数 可传null
     * @return
     */
    public static Object conversion(float num, Integer digit) {
        if (num < 100000) {
            return num;
        }
        String unit = "万";
        double newNum = num / 10000.0;
        if (digit != null) {
            BigDecimal bd = new BigDecimal(newNum);
            BigDecimal setScale = bd.setScale(digit, bd.ROUND_DOWN);
//            String numStr = String.format("%." + digit + "f", newNum);
            return setScale + unit;
        }
        return newNum + unit;
    }

}
