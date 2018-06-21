package com.suneee.sepay.core.sepay.bean.request;

import android.util.Log;

import com.suneee.sepay.core.sepay.SignUtils;

import java.io.Serializable;

/**
 * 支付宝支付参数
 * Created by suneee on 2016/6/23.
 */
public class ALiPayReq implements Serializable{

    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALC7fdh8EhMKYTtp"+
            "vMxiabZx8r4xMJ8glPlo50Tbhn3PzXCGDlr3TqyMpZ7POu9iC5zGwI77y9LZ+39c"+
            "VMjzuIDTqgM/EQstL597H2eR/abx1n8dzaiJz81Na5beNUfUe2HdITjjbi7WQoWl"+
            "JdivnDDT9d1eUinWu2lyCYGW91MbAgMBAAECgYADkKSnO0l6DNmNQ0LFtK6BOac1"+
            "4vQiIZwfs9gGcY8y9oAkZu/fMeHqZSPqIjAEKCwny3KS781awX7rQMeNDi2IkyWC"+
            "5kcZPdo6Xx400IQ8RFcCE2so5k0pHKyJ31+yjiJvjQwNImQ0SkiFZKfxgaHUGeUw"+
            "qctQOGqeic/ix/zkMQJBAN4+5AGNHwbejOrSs3rq7ZHRXUZZU7CoixGbCx64fEWy"+
            "bgCWqUeSYlv+S+0DR/SuiZqSzzTqpeUoTQslQyYtSaUCQQDLkwEShroBPUb5h/jj"+
            "XChqnggynbXGjTroOiu3cAc7tmxo0WZ4WxX3+rPgJ5GWQ68GA6xfoTgWNG1DLbbp"+
            "sA2/AkEAq2wrBh9JzUyfuQioM6k9cXnhzj9cVCjMi9nhK+L0x/Wm74FGNNRuVbLB"+
            "8aDUQaDWtomClbhgGW+KbYxiTPIlmQJBALRxJuAu/yweDo0bkuakR0bJsOZ8mCTY"+
            "BkDVXu6HpEGvXsRB55wC0KQvWcT0Db2tXLPuCZnyIu67paWIHbthS60CQFdVZHhF"+
            "9MXOYpqvvO36DjpWpbY8AvVl7kzDtyTjzrGH7HJQwE2FgQnT2jpw1v9ISIISCYIf"+
            "uNau7/kXYb2aHmA=";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCwu33YfBITCmE7abzMYmm2cfK+"+
            "MTCfIJT5aOdE24Z9z81whg5a906sjKWezzrvYgucxsCO+8vS2ft/XFTI87iA06oD"+
            "PxELLS+fex9nkf2m8dZ/Hc2oic/NTWuW3jVH1Hth3SE4424u1kKFpSXYr5ww0/Xd"+
            "XlIp1rtpcgmBlvdTGwIDAQAB";


    public String service;
    public String partner;
    public String _input_charset;
    public String notify_url;
    public String out_trade_no;
    public String subject;
    public String payment_type;
    public String seller_id;
    public String total_fee;
    public String body;
    public String sign;
    public String sign_type;



    public String getOrderinfo(){
        String info = "partner=" + "\"" + partner + "\"" +
                "&seller_id=" + "\"" + seller_id + "\""  +
                "&out_trade_no=" + "\"" + out_trade_no + "\""  +
                "&subject=" + "\"" + subject + "\""  +
                "&body=" + "\"" + body + "\""  +
                "&total_fee=" + "\"" + total_fee + "\""  +
                "&notify_url=" + "\"" + notify_url + "\""  +
                "&service=" + "\"" + service + "\""  +
                "&payment_type=" + "\"" + payment_type + "\""  +
                "&_input_charset=" + "\"" + _input_charset + "\""  +
                "&it_b_pay=30m";


        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + partner + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + seller_id + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + out_trade_no + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + total_fee + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notify_url + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\""+service+"\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        //orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        //orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }



    @Override
    public String toString() {

        String orderinfo = getOrderinfo();
        /*String sign = sign();
        try {
            //仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        Log.e("sign",  sign);
        String info = orderinfo + "&sign=\"" + sign  + "\"&sign_type=" + "\"" + sign_type + "\"";
        return info;
    }


    /**
     * sign the order info. 对订单信息进行签名
     *
     *            待签名订单信息
     */
    public String sign() {
        return SignUtils.sign(getOrderinfo(), RSA_PRIVATE);
    }

}
