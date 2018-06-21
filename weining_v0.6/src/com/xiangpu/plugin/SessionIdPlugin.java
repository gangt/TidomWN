package com.xiangpu.plugin;

import org.apache.cordova.CordovaArgs;
import org.json.JSONException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;


import com.lssl.activity.SuneeeApplication;
import com.xiangpu.utils.SharedPrefUtils;

public class SessionIdPlugin extends CordovaPlugin {

    static String TAG = "AudioSearchPlugin";
    public CallbackContext mCallbackContext;
    
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;
        
    	if (action.equals("start")) {
            
            mCallbackContext.success(SharedPrefUtils.getStringData(SuneeeApplication.getContext(), "sessionId",""));
            
            return true;
        }
        return false;
    }
  
    @Override
    public Boolean shouldAllowRequest(String url) {
        return true;
    }
    
    @Override
    public Boolean shouldAllowBridgeAccess(String url) {
    	return true;
    }
   
}
