/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package com.xiangpu.plugin;

import android.widget.Toast;

import com.lssl.activity.SuneeeApplication;
import com.suneee.sepay.PayResultCallback;
import com.suneee.sepay.SEPayManager;
import com.suneee.sepay.core.sepay.config.SEPayConfig;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class SEPayPlugin extends CordovaPlugin implements PayResultCallback {

    static String TAG = "SEPayPlugin";
    public CallbackContext mCallbackContext;

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;

        if (action.equals("start")) {
            PluginResult dataResult = new PluginResult(PluginResult.Status.OK, "");
            dataResult.setKeepCallback(true);
            mCallbackContext.sendPluginResult(dataResult);

            return true;
        } else if (action.equals("doPay")) {
            String startUrl = args.getString(1);
            JSONObject extraPrefs = args.getJSONObject(2);
            this.startActivity(startUrl, null);

            return true;
        }
        return false;
    }

    public void startActivity(String url, JSONObject extraPrefs) throws JSONException {

        if (url.contains("sePay")) {
            String payParam = url.substring(5, url.length());
            JSONObject result = new JSONObject(payParam);

            String created_ts = result.getString("created_ts");
            String pay_no = result.getString("pay_no");
            String se_sign = result.getString("se_sign");
            String order_money = result.getString("order_money");

            SEPayConfig.getInstance().setTest(true);
            SEPayConfig.getInstance().setDebug("sepaylog", true);

            SEPayManager.getInstance().setPayResultCallback(this);
            SEPayManager.getInstance().startPayPage(cordova.getActivity(), created_ts, pay_no, se_sign, order_money);

        }
    }

    //andi
    @Override
    public Boolean shouldAllowRequest(String url) {
        return true;
    }

    @Override
    public Boolean shouldAllowBridgeAccess(String url) {
        return true;
    }

    @Override
    public void onSuccess() {
        //mCallbackContext.success("pay ok!");
        Toast.makeText(cordova.getActivity(), "支付成功", Toast.LENGTH_LONG).show();
        mCallbackContext.success();
    }

    @Override
    public void onFaile(String errorMsg) {
        if (PayResultCallback.ERROR_RETURN.equals(errorMsg)) {
            Toast.makeText(cordova.getActivity(), "支付取消", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(cordova.getActivity(), errorMsg, Toast.LENGTH_LONG).show();
        }
        mCallbackContext.error("pay fail");
    }

}
