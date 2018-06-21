package com.suneee.sepay.core.sepay.model;

import android.app.Activity;

import com.unionpay.UPPayAssistEx;

/**
 * 对接银联sdk
 * Created by suneee on 2016/8/1.
 */
public class UPModel {

    private static final String TAG = UPModel.class.getSimpleName();

    public static void uppay(Activity activity, String tn, String mode){
        UPPayAssistEx.startPay(activity, null, null, tn, mode);
    }
}
