package com.suneee.sepay.core.sepay.bean.request;

import java.io.Serializable;

/**
 * 微信支付参数
 * Created by suneee on 2016/6/23.
 */
public class WXPayReq implements Serializable {

    public String appid;
    public String partnerid;
    public String package_value;
    public String noncestr;
    public String timestamp;
    public String prepayid;
    public String sign;

    @Override
    public String toString() {
        return "appid='" + appid + '\'' +
                ", partnerid='" + partnerid + '\'' +
                ", package_value='" + package_value + '\'' +
                ", noncestr='" + noncestr + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", prepayid='" + prepayid + '\'' +
                ", sign='" + sign + '\'';
    }
}
