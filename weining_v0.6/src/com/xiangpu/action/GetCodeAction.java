package com.xiangpu.action;

import android.content.Context;
import android.util.Log;

import com.konecty.rocket.chat.R;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.MD5Encoder;
import com.xiangpu.utils.WebServiceUtil;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by fangfumin on 2017/9/1.
 */

public class GetCodeAction {

    public static void getCodeByMobile(final Context context, final String username, final GetCodeInterface getCodeInterface) {

        WebServiceUtil.request(Constants.moduleGetMobileCheckCode, "json", new WebServiceUtil.OnDataListener() {
            @Override
            public void onReceivedData(String mode, JSONObject result) {
                if (result == null) {
                    getCodeInterface.failed(context.getString(R.string.failed_get_telphone_checkcode));
                    return;
                }

                try {
                    String status = result.getString("status");
                    String errcodeMsg = result.getString("message");
                    if ("1".equalsIgnoreCase(status)) {
                        getCodeInterface.success(result.getString("data"));
                    } else {
                        getCodeInterface.failed(errcodeMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public List<NameValuePair> onGetParamData(String mode) {
                return null;
            }

            @Override
            public String onGetParamDataString(String mode) {
                JSONObject json = new JSONObject();
                try {
                    json.put("mobile", username);
                    json.put("enterpriseCode", context.getString(R.string.api_compcode));
                    json.put("clientIp", "127.0.0.1");
                    json.put("appCode", context.getString(R.string.api_appcode));
                    json.put("serverIp", "127.0.0.1");
                    json.put("encryptCode", "1234567899876543");
                    json.put("prefix", context.getString(R.string.app_prefix));

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return json.toString();
            }
        });
    }

    public static void getCodeByEmail(final Context context, final String username, final GetCodeInterface getCodeInterface) {

        WebServiceUtil.request(Constants.moduleGetEmailCheckCode, "json", new WebServiceUtil.OnDataListener() {
            @Override
            public void onReceivedData(String mode, JSONObject result) {
                if (result == null) {
                    getCodeInterface.failed(context.getString(R.string.failed_get_email_checkcode));
                    return;
                }

                try {
                    String status = result.getString("status");
                    String errcodeMsg = result.getString("message");
                    if ("1".equalsIgnoreCase(status)) {
                        getCodeInterface.success(result.getString("data"));
                    } else {
                        getCodeInterface.failed(errcodeMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public List<NameValuePair> onGetParamData(String mode) {
                return null;
            }

            @Override
            public String onGetParamDataString(String mode) {
                JSONObject json = new JSONObject();
                try {
                    json.put("email", username);
                    json.put("enterpriseCode", context.getString(R.string.api_compcode));
                    json.put("clientIp", "127.0.0.1");
                    json.put("appCode", context.getString(R.string.api_appcode));
                    json.put("serverIp", "127.0.0.1");
                    json.put("encryptCode", "1234567899876543");
                    json.put("prefix", context.getString(R.string.app_prefix));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return json.toString();
            }
        });
    }

}
