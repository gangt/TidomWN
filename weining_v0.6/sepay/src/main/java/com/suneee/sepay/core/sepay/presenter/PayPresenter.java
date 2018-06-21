package com.suneee.sepay.core.sepay.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.suneee.sepay.core.exception.PayTypeException;
import com.suneee.sepay.core.http.ApiCallback;
import com.suneee.sepay.core.http.ApiResultBean;
import com.suneee.sepay.core.log.L;
import com.suneee.sepay.core.sepay.bean.PayType;
import com.suneee.sepay.core.sepay.bean.PayTypeItem;
import com.suneee.sepay.core.sepay.bean.SEPayResult;
import com.suneee.sepay.core.sepay.bean.request.ALiPayReq;
import com.suneee.sepay.core.sepay.bean.request.WXPayReq;
import com.suneee.sepay.core.sepay.callback.PayCallback;
import com.suneee.sepay.core.sepay.config.WXConfig;
import com.suneee.sepay.core.sepay.contract.PayContract;
import com.suneee.sepay.core.sepay.model.ALiModel;
import com.suneee.sepay.core.sepay.model.OPGModel;
import com.suneee.sepay.core.sepay.model.WXModel;

import java.util.List;

/**
 * 支付业务类，链接sdk和应用上层。主要用于对外提供SEPaySDK具有的功能
 * 需要绑定PayView
 * Created by suneee on 2016/6/24.
 */
public class PayPresenter implements PayContract.Presenter{

    private static final String TAG = PayPresenter.class.getSimpleName();
    private PayContract.View payView;
    private String notifyUrl;

    public PayPresenter(PayContract.View payview){
        this.payView = payview;
    }

    /**
     * 解除绑定的view
     */
    public void detachView() {
        //注销WXConfig对paycallback对引用，防止内存泄露。
        WXConfig.getInstance().setCallback(null);
        payView = null;
    }

    public boolean isViewAttached() {
        return payView != null;
    }

    public void initWXPay(Context context, String wxAppid) {
        // 如果用到微信支付，在用到微信支付的Activity的onCreate函数里调用以下函数.
        // 第二个参数需要换成你自己的微信AppID.
        String initInfo = WXModel.getInstance().initWechatPay(context, wxAppid);
        if (initInfo != null) {
            if (isViewAttached()) {
                payView.showErrorMsg("微信初始化失败：" + initInfo);
            }
        }
    }

    /**
     * 支付结果处理
     */
    private PayCallback payCallback = new PayCallback() {
        @Override
        public void onResponse(SEPayResult result) {

            String toastMsg = "";
            String payResult = result.getResult();

            if (payResult.equals(SEPayResult.RESULT_SUCCESS)) {

                //toastMsg = "用户支付成功";
                OPGModel.getInstance().notifyOPGPayResult(notifyUrl, result.getDetailInfo());

                if(isViewAttached()){
                    payView.showPayResult();
                }
                return;
            } else if (payResult.equals(SEPayResult.RESULT_CANCEL)) {
                toastMsg = "用户取消支付";
            } else if (payResult.equals(SEPayResult.RESULT_FAIL)) {
                toastMsg = "支付失败, 原因: " + result.getErrCode() +
                        " # " + result.getErrMsg() +
                        " # " + result.getDetailInfo();

                L.e(TAG, toastMsg);

            } else if (payResult.equals(SEPayResult.RESULT_UNKNOWN)) {
                //可能出现在支付宝8000返回状态
                toastMsg = "订单状态未知";
            } else {
                toastMsg = "invalid return";
            }

            /**
             * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
             * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
             * docType=1) 建议商户依赖异步通知
             */
            if (isViewAttached()) {
                payView.showErrorMsg(toastMsg);
            }
        }
    };


    /**
     * 请求支付参数
     * @param payment_type_id
     * @param pay_no
     * @throws PayTypeException
     */
    public void requestPayParam(String payment_type_id, String pay_no) throws PayTypeException {

        if (isViewAttached()) {
            payView.showDialog();

            if (PayType.WX.equals(payment_type_id)) {
                OPGModel.getInstance().reqWXPayParam(pay_no, new ApiCallback<ApiResultBean<WXPayReq>>() {
                    @Override
                    public void onSuccess(ApiResultBean<WXPayReq> wxPayParamApiResultBean) {
                        payView.hideDialog();
                        WXPayReq wxPayParam = wxPayParamApiResultBean.data;
                        //notifyUrl = wxPayParam.
                        if (wxPayParam != null) {

                            if(TextUtils.isEmpty(WXConfig.APP_ID)
                                    || !WXConfig.APP_ID.equals(wxPayParam.appid)
                                    || !WXModel.getInstance().hasRegist()){
                                WXConfig.APP_ID = wxPayParam.appid;
                                //注册
                                initWXPay((Activity)payView, WXConfig.APP_ID);
                            }

                            if(WXModel.getInstance().hasRegist()){
                                //调起微信
                                wxPay(wxPayParamApiResultBean.data);
                            }
                        }
                    }

                    @Override
                    public void onFaile(Throwable e) {
                        payView.hideDialog();
                        payView.showErrorMsg(e.getMessage());
                    }
                });
            }else if(PayType.ALI.equals(payment_type_id)){
                OPGModel.getInstance().reqALiPayParam(pay_no, new ApiCallback<ApiResultBean<ALiPayReq>>() {
                    @Override
                    public void onSuccess(ApiResultBean<ALiPayReq> aLiPayParamApiResultBean) {
                        payView.hideDialog();
                        notifyUrl = aLiPayParamApiResultBean.data.notify_url;
                        //调起支付宝sdk
                        //拼装数据
                        String payInfo = aLiPayParamApiResultBean.data.toString();
                        L.e(TAG, "payInfo = " + payInfo);
                        aliPay(payInfo,  (Activity) payView);
                    }

                    @Override
                    public void onFaile(Throwable e) {
                        payView.hideDialog();
                        payView.showErrorMsg(e.getMessage());
                    }
                });
            }else if(PayType.YL.equals(payment_type_id)){
                //银联


            }else{
                payView.hideDialog();
                throw new PayTypeException("payType: " + payment_type_id + " 该支付方式不支持...");
            }
        } else {
            //throw new ViewAttachException("please attachview for presenter...");
        }
    }

    /**
     * 调起微信sdk支付
     *
     * @param wxPayParam
     */
    public void wxPay(WXPayReq wxPayParam) {

        if (WXModel.getInstance().isWXAppInstalledAndSupported() && WXModel.getInstance().isWXPaySupported()) {
            //对于微信支付, 手机内存太小会有OutOfResourcesException造成的卡顿, 以致无法完成支付
            //这个是微信自身存在的问题
            WXModel.getInstance().wxPay(wxPayParam, payCallback);
        } else {
            if (isViewAttached()) {
                payView.showErrorMsg("您尚未安装微信或者安装的微信版本不支持");
            }
        }

    }

    public void aliPay(String payInfo, Activity activity) {

        ALiModel.aliPay(payInfo, activity, payCallback);
    }

    /**
     * 请求支付列表
     * @param created_ts 翌支付交易号
     * @param pay_no 签名时间
     * @param se_sign 翌支付签名
     */
    public void requestPayTypeList(String created_ts, String pay_no, String se_sign) {
        if (isViewAttached()) {
            payView.showDialog();
        }
        OPGModel.getInstance().requestPayTypeList(created_ts, pay_no, se_sign, new ApiCallback<ApiResultBean<List<PayTypeItem>>>() {
            @Override
            public void onSuccess(ApiResultBean<List<PayTypeItem>> listApiResultBean) {
                if (isViewAttached()) {
                    payView.hideDialog();
                    payView.showPayList(listApiResultBean.data);
                }
            }

            @Override
            public void onFaile(Throwable e) {
                if (isViewAttached()) {
                    payView.hideDialog();
                    payView.showErrorMsg(e.getMessage());
                    payView.showPayList(null);
                }
            }
        });
    }
}
