package com.xiangpu.plugin;

import android.app.Activity;
import android.content.Intent;

import com.xiangpu.activity.usercenter.PersonCenterActivity;
import com.xiangpu.activity.usercenter.PersonDataSecondActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

/**
 * Created by Administrator on 2017/12/13 0013.
 * Info：
 */

public class ShowTitlePlugin extends CordovaPlugin {
    public static final int PERSON_DATA_SECOND = 101;
    private CallbackContext callbackContext = null;
    private Activity activity = null;

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        this.activity = cordova.getActivity();
        if (action.equals("showTitle")) {
            String title = args.getString(2);
            LogUtil.d("action: " + action + " title: " + title);
            if (activity instanceof PersonCenterActivity) {
                if (!title.equals(Constants.PERSON_CONFIRM)) {
                    if (title.equals(Constants.PERSON_NICK_NAME)) {
                        title = "昵称";
                    } else if (title.equals(Constants.PERSON_SIGNATURE)) {
                        title = "个性签名";
                    } else if (title.equals(Constants.PERSON_AREA)) {
                        title = "地区";
                    } else if (title.equals(Constants.PERSON_MOBLIE)) {
                        title = "手机号";
                    } else if (title.equals(Constants.PERSON_PHONE)) {
                        title = "座机号";
                    }
                    intentActivity(title);
                }
            } else if (activity instanceof PersonDataSecondActivity) {
                activity.setResult(0, new Intent());
                activity.finish();
            }
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

    /**
     * 跳转到个人资料二级菜单页面
     *
     * @param title：
     */
    private void intentActivity(String title) {
        Intent intent = new Intent(activity, PersonDataSecondActivity.class);
        intent.putExtra(Constants.INTENT_ACTIVITY, title);
        activity.startActivityForResult(intent, PERSON_DATA_SECOND);
    }

}
