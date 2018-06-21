package com.suneee.sepay.core.sepay.model;

import android.text.TextUtils;

import com.suneee.sepay.core.http.ApiCallback;
import com.suneee.sepay.core.http.ApiResultBean;
import com.suneee.sepay.core.http.HttpCallback;
import com.suneee.sepay.core.http.SEHttpClient;
import com.suneee.sepay.core.sepay.bean.PayType;
import com.suneee.sepay.core.sepay.bean.PayTypeItem;
import com.suneee.sepay.core.sepay.bean.request.ALiPayReq;
import com.suneee.sepay.core.sepay.bean.request.WXPayReq;
import com.suneee.sepay.core.sepay.config.SEPayConfig;

import java.util.List;

/**
 * 对接OPG后台
 * Created by suneee on 2016/6/23.
 */
public class OPGModel {

    public  String DOMAIN = "http://opgdev.weilian.cn"; //debug "http://opgtest.weilian.cn";//
    /**请求支付参数接口*/
    private  String URL_PAY_PARAM = DOMAIN + "/payment/pay/pay_app";
    /**请求支付列表接口*/
    private  String URL_PAY_TYPE_LIST  = DOMAIN + "/payment/pay/pay_app";
    //private  String URL_PAY_RESULT_CALLBACK = DOMAIN + "/payment/pay/return_url ";

    private static OPGModel instance;

    private OPGModel(){
        if(SEPayConfig.getInstance().isTest()){
            DOMAIN = "http://pay.weilian.cn";
//            DOMAIN = "http://opgtest.weilian.cn";
        }else{
            DOMAIN = "http://opgdev.weilian.cn";
        }
        URL_PAY_PARAM = DOMAIN + "/payment/pay/pay_app";
        URL_PAY_TYPE_LIST  = DOMAIN + "/payment/pay/pay_app";
        //URL_PAY_RESULT_CALLBACK = DOMAIN + "/payment/pay/return_url ";
    }

    public static OPGModel getInstance(){
        if(instance == null){
            synchronized (OPGModel.class){
                if(instance == null){
                    instance = new OPGModel();
                }
            }
        }
        return instance;
    }

    /**
     * 请求支付宝的支付参数
     * @param listener
     */
    public void reqALiPayParam(String pay_no, final ApiCallback<ApiResultBean<ALiPayReq>> listener){

        final String url = URL_PAY_PARAM + "?payment_type_id=" + PayType.ALI +"&pay_no="+pay_no;
        //请求加入调度
        SEHttpClient.getInstance().httpGet(url, new HttpCallback<ApiResultBean<ALiPayReq>>() {
            @Override
            public void onFailure(Exception e) {
                listener.onFaile(e);
            }

            @Override
            public void onSuccess(ApiResultBean<ALiPayReq> result) {
                if(result.code == 200){
                    listener.onSuccess(result);
                }else{
                    listener.onFaile(new Exception("result.code = " + result.code));
                }
            }

        });
    }

    /**
     * 请求微信支付的支付参数
     * @param listener
     */
    public void reqWXPayParam(String pay_no, final ApiCallback<ApiResultBean<WXPayReq>> listener){

        final String url = URL_PAY_PARAM + "?payment_type_id="+ PayType.WX +"&pay_no="+pay_no;

        //请求加入调度
        SEHttpClient.getInstance().httpGet(url, new HttpCallback<ApiResultBean<WXPayReq>>() {
            @Override
            public void onFailure(Exception e) {
                listener.onFaile(e);
            }

            @Override
            public void onSuccess(ApiResultBean<WXPayReq> result) {
                if(result != null && result.code == 200){
                    listener.onSuccess(result);
                }else{
                    int code = -1;
                    if(result != null){
                        code = result.code;
                    }
                    listener.onFaile(new Exception("result.code = " + code));
                }
            }

        });
    }

    /**
     * 请求支付方式列表
     * @param listener
     */
    public void requestPayTypeList(String created_ts, String pay_no, String se_sign, final ApiCallback<ApiResultBean<List<PayTypeItem>>> listener){

        String url = URL_PAY_TYPE_LIST + "?created_ts="+created_ts + "&pay_no="+pay_no + "&se_sign="+se_sign;
        SEHttpClient.getInstance().httpGet(url, new HttpCallback<ApiResultBean<List<PayTypeItem>>>() {
            @Override
            public void onFailure(Exception e) {
                listener.onFaile(e);
            }

            @Override
            public void onSuccess(ApiResultBean<List<PayTypeItem>> result) {
                if(result.code == 200){
                    listener.onSuccess(result);
                }else{
                    listener.onFaile(new Exception("result.code = " + result.code));
                }

            }
        });
    }

    /**
     * 同步通知OPG支付结果
     * @param url
     * @param payresult
     */
    public void notifyOPGPayResult(String url, String payresult){
        if(!TextUtils.isEmpty(url) && !TextUtils.isEmpty(payresult)){
            url = url + "?" + payresult;
            SEHttpClient.getInstance().httpGet(url, null);
        }
    }

}
