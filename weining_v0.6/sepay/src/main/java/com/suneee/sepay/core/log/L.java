package com.suneee.sepay.core.log;

import android.text.TextUtils;
import android.util.Log;

import com.suneee.sepay.core.sepay.config.SEPayConfig;

/**
 * Created by suneee on 2016/7/8.
 */
public class L {

    public static void e(String tag, String message){

        if(SEPayConfig.getInstance().isDebug()){
            if(TextUtils.isEmpty(tag)){
                tag = SEPayConfig.getInstance().getDebugTag();
            }

            if(TextUtils.isEmpty(message)){
                message = "";
            }
            Log.e(tag, message);
        }
    }
}
