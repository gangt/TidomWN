package com.xiangpu.utils;

import android.util.Log;

import com.xiangpu.common.Constants;

/**
 * 日志工具类
 *
 * @author huangda
 */
public class LogUtil {

    private static final boolean DEBUG = Constants.is_debug; //是否是测试环境
    private static final String TAG = "xiangpu_debug";

    public static void i(String TAG, String content) {
        if (DEBUG) {
            Log.i(TAG, content);
        }
    }

    public static void e(String content, Throwable throwable) {
        if (DEBUG) {
            Log.e(TAG, content, throwable);
        }
    }

    public static void e(String TAG, String content) {
        if (DEBUG) {
            Log.e(TAG, content);
        }
    }

    public static void d(String content) {
        if (DEBUG) {
            Log.d(TAG, content);
        }
    }

    public static void d(String tag, String content) {
        if (DEBUG) {
            Log.d(tag, content);
        }
    }

}
