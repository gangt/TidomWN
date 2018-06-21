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

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.LOG;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;


import java.util.Collections;
import java.util.Iterator;

public class BluetoochPrinterPlugin extends CordovaPlugin {

    static String TAG = "BluetoochPrinterPlugin";
    public CallbackContext mCallbackContext;
    
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;
        
    	if (action.equals("start")) {
            
    	    String startUrl = args.getString(1);
            JSONObject extraPrefs = args.getJSONObject(2);
            this.startActivity( startUrl, extraPrefs);
            
            return true;
        }
        return false;
    }

    public void startActivity( String url, JSONObject extraPrefs) throws JSONException {
            
//           if (url.contains("BluetoochPrinter")) {
//        	  try {
//        		Intent intent = new Intent(this.cordova.getActivity(), Class.forName(className));
//	            intent.putExtra("testStartUrl", url);
//	               Iterator<String> iter = extraPrefs.keys();
//
//	               while (iter.hasNext()) {
//	                   String key = iter.next();
//	                   intent.putExtra(key, extraPrefs.getString(key));
//	               }
//
//	               LOG.d(TAG, "Starting activity %s", className);
//	               cordova.getActivity().startActivity(intent);
//
//	               mCallbackContext.success();
//
//
//           } catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//         }
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
   
}
