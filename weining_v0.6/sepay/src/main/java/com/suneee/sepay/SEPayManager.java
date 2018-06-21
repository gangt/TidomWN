package com.suneee.sepay;

import android.app.Activity;
import android.content.Intent;

/**
 * 启动入口
 * 支付结果通过setPayResultCallback来获取。
 * Created by suneee on 2016/7/8.
 */
public class SEPayManager {


    private PayResultCallback payResultCallback;
    private static SEPayManager instance;
    private SEPayManager(){}

    public static SEPayManager getInstance(){
        if(instance == null){
            synchronized (SEPayManager.class){
                if(instance == null){
                    instance = new SEPayManager();
                }
            }
        }
        return instance;
    }

    public PayResultCallback getPayResultCallback() {
        return payResultCallback;
    }

    public void setPayResultCallback(PayResultCallback payResultCallback) {
        this.payResultCallback = payResultCallback;
    }

    public void startPayPage(Activity activity, String created_ts, String pay_no, String se_sign, String orderMoney){

        Intent intent = new Intent(activity, com.suneee.sepay.SEPayMainActivity.class);
        intent.putExtra(SEPayMainActivity.INTENT_CREATE_TS, created_ts);
        intent.putExtra(SEPayMainActivity.INTENT_PAY_NO, pay_no);
        intent.putExtra(SEPayMainActivity.INTENT_SE_SIGN, se_sign);
        intent.putExtra(SEPayMainActivity.INTENT_ORDERMONEY, orderMoney);
        activity.startActivity(intent);
    }


    public void destory(){
        payResultCallback = null;
        instance = null;
    }


}
