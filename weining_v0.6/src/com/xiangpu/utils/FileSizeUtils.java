package com.xiangpu.utils;

import android.content.Context;
import android.text.format.Formatter;

/**
 * description: $todo$
 * autour: Andy
 * date: 2017/12/13 9:30
 * update: 2017/12/13
 * version: 1.0
 */
public class FileSizeUtils {

    //工具类 根据文件大小自动转化为KB, MB, GB
    public static String formatSize(Context context, String target_size) {
        return Formatter.formatFileSize(context, Long.valueOf(target_size));
    }

}
