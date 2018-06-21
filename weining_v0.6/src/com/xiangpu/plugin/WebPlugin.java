package com.xiangpu.plugin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiangpu.activity.AudioSearchActivity;
import com.xiangpu.activity.RecordPlayerActivity;
import com.xiangpu.activity.WebMainActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;
import org.json.JSONObject;

public class WebPlugin extends CordovaPlugin {

    static String TAG = "ActivityPlugin";
    public static final String BACKBUTTONMULTIPAGE_URL = "file:///android_asset/www/backbuttonmultipage/index.html";

    public CallbackContext mCallbackContext;

    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        mCallbackContext = callbackContext;

        if (action.equals("start")) {

            String startUrl = args.getString(1);
            JSONObject extraPrefs = args.getJSONObject(2);
            this.startActivity(startUrl, extraPrefs);

            return true;
        }
        return false;
    }

    public void startActivity(String url, JSONObject extraPrefs) throws JSONException {
        if (url.contains("control_center")) {

            String payParam = url.substring(14, url.length());

            JSONObject result = new JSONObject(payParam);

            String IP = result.getString("IP");
            String ipPort = result.getString("ipPort");
            String username = result.getString("username");
            String pwd = result.getString("pwd");
            String devicePort = result.getString("devicePort");

            Intent intent = new Intent(this.cordova.getActivity(), WebMainActivity.class);
            cordova.getActivity().startActivity(intent);
        }
    }

    public void startActivity1(String url, JSONObject extraPrefs) throws JSONException {
        //try {

        if (!url.contains(":")) {
            url = "file:///android_asset/www/" + url;
        }

        if (url.contains("http://www.xfyun.com")) {
            Intent intent = new Intent(this.cordova.getActivity(), AudioSearchActivity.class);
            cordova.getActivity().startActivity(intent);
        } else if (url.contains("http://www.recordplayer.com")) {
            Intent intent = new Intent(this.cordova.getActivity(), RecordPlayerActivity.class);
            cordova.getActivity().startActivity(intent);
        } else if (url.contains("caputre")) {
            //Intent intent = new Intent(this.cordova.getActivity(), Class.forName(className));
            // intent.putExtra("testStartUrl", url);
            //cordova.getActivity().startActivityForResult(intent,CHOOSE_BIG_PICTURE);
            // this.cordova.startActivityForResult(this, intent, 1);
        } else if (url.contains("photo")) {
//     		  	Intent intent = new Intent(cordova.getActivity(), Class.forName(className));
//   		   		intent.putExtra("testStartUrl", url);
//
//   		   		cordova.startActivityForResult((CordovaPlugin)this, intent, 1);
        } else {

//             Intent intent = new Intent(this.cordova.getActivity(), Class.forName(className));
//             intent.putExtra("testStartUrl", url);
//             Iterator<String> iter = extraPrefs.keys();
//
//             while (iter.hasNext()) {
//                 String key = iter.next();
//                 intent.putExtra(key, extraPrefs.getString(key));
//             }
//
//             LOG.d(TAG, "Starting activity %s", className);
//             cordova.getActivity().startActivity(intent);
//
//             mCallbackContext.success();

        }

//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
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


    public static final int TAKE_BIG_PICTURE = 1; // ����
    public static final int CHOOSE_BIG_PICTURE = 2; // ���
    private static final int SCAN_SKIP = 22;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        super.onActivityResult(requestCode, resultCode, intent);

        if (intent == null) return;
        String data = intent.getStringExtra("data");
        // callbackContext.success(data);

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
