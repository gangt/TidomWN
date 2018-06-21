package com.xiangpu.plugin;

import java.util.Iterator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.LOG;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiangpu.activity.AudioSearchActivity;
import com.xiangpu.activity.RecordPlayerActivity;

public class CapturePhotoPlugin extends CordovaPlugin {

    public static String TAG = "CapturePhotoPlugin";

    public CallbackContext mCallbackContext;

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;

        if (action.equals("start")) {
//	    		String className = args.isNull(0) ? MainDroidGapActivity.class.getCanonicalName() : args.getString(0);
//	            String startUrl = args.getString(1);
//	            JSONObject extraPrefs = args.getJSONObject(2);
//	            this.startActivity1(className, startUrl, extraPrefs);
//
            return true;
        }
        return false;
    }

    public void startActivity1(String className, String url, JSONObject extraPrefs) throws JSONException {
        try {

            if (!url.contains(":")) {
                url = "file:///android_asset/www/" + url;
            }

            if (url.contains("caputre")) {
                Intent intent = new Intent(this.cordova.getActivity(), Class.forName(className));
                intent.putExtra("testStartUrl", url);
                //cordova.getActivity().startActivityForResult(intent,CHOOSE_BIG_PICTURE);
                this.cordova.startActivityForResult(this, intent, 1);
            } else if (url.contains("photo")) {
                Intent intent = new Intent(cordova.getActivity(), Class.forName(className));
                intent.putExtra("testStartUrl", url);
                //cordova.getActivity().startActivityForResult(intent,CHOOSE_BIG_PICTURE);
                //cordova.setActivityResultCallback(this);
                cordova.startActivityForResult((CordovaPlugin) this, intent, 1);

//	 		   	   PluginResult mPlugin = new PluginResult(PluginResult.Status.NO_RESULT);  
//	 		   	   mPlugin.setKeepCallback(true);  
//	 		   	   mCallbackContext.sendPluginResult(mPlugin);
//	 		   	   mCallbackContext.success("success"); 
            } else {
                Intent intent = new Intent(this.cordova.getActivity(), Class.forName(className));
                intent.putExtra("testStartUrl", url);
                Iterator<String> iter = extraPrefs.keys();

                while (iter.hasNext()) {
                    String key = iter.next();
                    intent.putExtra(key, extraPrefs.getString(key));
                }

                LOG.d(TAG, "Starting activity %s", className);
                cordova.getActivity().startActivity(intent);
                mCallbackContext.success();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            LOG.e(TAG, "Error starting activity %s", className);
        }
    }

    @Override
    public Boolean shouldAllowRequest(String url) {
        return true;
    }

    @Override
    public Boolean shouldAllowBridgeAccess(String url) {
        return true;
    }

    public static final int TAKE_BIG_PICTURE = 1;
    public static final int CHOOSE_BIG_PICTURE = 2;
    private static final int SCAN_SKIP = 22;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) return;
        String data = intent.getStringExtra("data");

        switch (requestCode) {
            case SCAN_SKIP:
                if (resultCode == this.cordova.getActivity().RESULT_OK) {
                    Bundle bundle = intent.getExtras();
                    String scanResult = bundle.getString("result");
                }
            case CHOOSE_BIG_PICTURE:
                if (null != intent && !TextUtils.isEmpty(intent.getData().getPath())) {

                } else {

                }
                break;
            case TAKE_BIG_PICTURE:
                break;
        }
    }

}
