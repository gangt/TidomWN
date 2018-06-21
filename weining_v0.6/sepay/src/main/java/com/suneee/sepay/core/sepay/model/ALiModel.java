package com.suneee.sepay.core.sepay.model;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;

import com.alipay.sdk.app.PayTask;
import com.suneee.sepay.core.log.L;
import com.suneee.sepay.core.sepay.bean.SEPayResult;
import com.suneee.sepay.core.sepay.callback.PayCallback;
import com.suneee.sepay.core.sepay.config.SEPayConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对接支付宝接口
 * Created by suneee on 2016/6/24.
 */
public class ALiModel {

    private static final String TAG = ALiModel.class.getSimpleName();

    /**
     * 支付宝支付
     * @param payInfo
     * @param activity
     * @param callback
     */
    public static void aliPay(final String payInfo, final Activity activity, final PayCallback callback){

        final HandlerThread thread = new HandlerThread("sdkmodel");
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                L.e(TAG, "payInfo: "+ payInfo);
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                String aliResult = alipay.pay(payInfo, true);

                L.e(TAG, "aliResult = " + aliResult);

                //解析ali返回结果
                Pattern pattern = Pattern.compile("resultStatus=\\{(\\d+?)\\}");
                Matcher matcher = pattern.matcher(aliResult);
                String resCode = "";
                String detailResult="";
                if (matcher.find()){
                    resCode = matcher.group(1);
                }

                pattern = Pattern.compile("result=\\{(.*?)\\}");
                matcher = pattern.matcher(aliResult);
                if(matcher.find()){
                    detailResult = matcher.group(1);
                }

                String result;
                int errCode;
                String errMsg;

                //9000-订单支付成功, 8000-正在处理中, 4000-订单支付失败, 6001-用户中途取消, 6002-网络连接出错
                String errDetail;
                if (resCode.equals("9000")) {
                    result = SEPayResult.RESULT_SUCCESS;
                    errCode = SEPayResult.APP_PAY_SUCC_CODE;
                    errMsg = SEPayResult.RESULT_SUCCESS;
                    errDetail = errMsg;
                } else if (resCode.equals("6001")) {
                    result = SEPayResult.RESULT_CANCEL;
                    errCode = SEPayResult.APP_PAY_CANCEL_CODE;
                    errMsg = SEPayResult.RESULT_CANCEL;
                    errDetail = errMsg;
                } else if (resCode.equals("8000")) {
                    result = SEPayResult.RESULT_UNKNOWN;
                    errCode = SEPayResult.APP_INTERNAL_THIRD_CHANNEL_ERR_CODE;
                    errMsg = SEPayResult.RESULT_PAYING_UNCONFIRMED;
                    errDetail = "订单正在处理中，无法获取成功确认信息";
                } else {
                    result = SEPayResult.RESULT_FAIL;
                    errCode = SEPayResult.APP_INTERNAL_THIRD_CHANNEL_ERR_CODE;
                    errMsg = SEPayResult.FAIL_ERR_FROM_CHANNEL;

                    if (resCode.equals("4000"))
                        errDetail = "订单支付失败";
                    else
                        errDetail = "网络连接出错";
                }

                callback.onResponse(new SEPayResult(result, errCode, errDetail,
                        detailResult, SEPayConfig.getInstance().billID));
                thread.quit();
            }
        });

        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return null;
            }
        };
    }
}
