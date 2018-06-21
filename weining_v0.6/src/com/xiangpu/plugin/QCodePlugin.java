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

import android.content.Intent;

import com.suneee.demo.scan.ScanQRActivity;
import com.xiangpu.utils.LogUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class QCodePlugin extends CordovaPlugin implements ScanQRActivity.QScanResultCallBackInterface{


    static String TAG = "QCodePlugin";
    public CallbackContext mCallbackContext;
    
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;
        LogUtil.e(TAG,"action:"+action);
    	if (action.equals("start")) {
            
    		//String className = args.isNull(0) ? MainDroidGapActivity.class.getCanonicalName() : args.getString(0);
            //String startUrl = args.getString(1);
            //JSONObject extraPrefs = args.getJSONObject(2);
            //this.startActivity(className, startUrl, extraPrefs);
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    ScanQRActivity.setQScanResultCallBackInterface(QCodePlugin.this);
                    Intent intent = new Intent(cordova.getActivity(), ScanQRActivity.class);
                    cordova.getActivity().startActivity(intent);
                }
            });
            return true;
        }
        return false;
    }

    public void startActivity(String className, String url, JSONObject extraPrefs) throws JSONException {
            
           if (url.contains("QCode")) {
        	   //AddDeviceScanActivity.setQcodeResultCallback(this);
        	   //Intent intent = new Intent(cordova.getActivity(), ScanQRActivity.class);
     		   //cordova.getActivity().startActivity(intent);
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

//	@Override
//	public void onSuccess() {
//		// TODO Auto-generated method stub
//		mCallbackContext.success();
//	}

//	@Override
//	public void onSuccess(String sid) {
//		// TODO Auto-generated method stub
//		mCallbackContext.success(sid);
//		Toast.makeText(SuneeeApplication.getContext(),sid, Toast.LENGTH_LONG).show();
//	}
//
//	@Override
//	public void onFaile(String errorMsg) {
//		// TODO Auto-generated method stub
//		mCallbackContext.error(errorMsg);
//		Toast.makeText(SuneeeApplication.getContext(),errorMsg, Toast.LENGTH_LONG).show();
//	}

    @Override
    public void QScanResultCallBack(String result){
        if(mCallbackContext != null){
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
            pluginResult.setKeepCallback(true);
            mCallbackContext.sendPluginResult(pluginResult);
        }
    }
}
