package com.xiangpu.plugin;

import org.apache.cordova.CordovaArgs;
import org.json.JSONException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;

public class SharePlugin extends CordovaPlugin {

    static String TAG = "SharePlugin";
    public CallbackContext mCallbackContext;
    
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;
        
    	if (action.equals("start")) {
            
            //mCallbackContext.success(SharedPrefUtils.getStringData(MyApplication.getContext(), "sessionId",""));
          
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
