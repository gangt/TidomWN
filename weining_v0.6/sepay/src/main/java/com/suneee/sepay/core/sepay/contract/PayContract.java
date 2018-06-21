package com.suneee.sepay.core.sepay.contract;

import android.app.Activity;

import com.suneee.sepay.core.exception.PayTypeException;
import com.suneee.sepay.core.sepay.bean.PayTypeItem;
import com.suneee.sepay.core.sepay.bean.request.WXPayReq;

import java.util.List;

/**
 * Created by suneee on 2016/7/13.
 */
public interface PayContract {

    interface View extends BaseView{

        void showPayList(List<PayTypeItem> payTYpeList);

        void showPayResult();
    }

    interface Presenter extends BasePresenter {
        void requestPayParam(String payment_type_id, String pay_no) throws PayTypeException;
        void wxPay(WXPayReq wxPayParam);
        void aliPay(String payInfo, Activity activity);
        void requestPayTypeList(String created_ts, String pay_no, String se_sign);
    }
}
