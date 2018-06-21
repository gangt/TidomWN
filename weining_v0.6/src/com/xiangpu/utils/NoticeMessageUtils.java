package com.xiangpu.utils;

import android.content.Context;
import android.text.TextUtils;

import com.konecty.rocket.chat.R;
import com.xiangpu.bean.ErrorCode;

/**
 * 错误提示语管理类
 * Created by huangda on 2018/1/28.
 */

public class NoticeMessageUtils {
    /**
     * 根据用户中心的返回的错误编码获得提示语
     * @param context
     * @param code  错误编码
     * @return
     */
    public static String getErrorMsgByCode(Context context, String code) {
        if (TextUtils.isEmpty(code)){
            return context.getString(R.string.app_error);
        }
        switch (code) {
            case ErrorCode.APP_NO_FIND:
                return context.getString(R.string.login_app_no_find);
            case ErrorCode.UNAUTHORIZED_ACCESS_APP:
                return context.getString(R.string.login_no_exists_account);
            case ErrorCode.ENTERPRISE_NOT_ENABLED:
                return context.getString(R.string.login_enterprise_not_enabled);
            case ErrorCode.NO_EXISTS_ACCOUNT:
                return context.getString(R.string.login_no_exists_account);
            case ErrorCode.PLEASE_MOBILE_EMAIL_LOGIN:
                return context.getString(R.string.login_please_mobile_email_login);
            case ErrorCode.ILLEGAL_ARGUMENT:
                return context.getString(R.string.get_user_info_fail);
            case ErrorCode.CONNECTION_FAIL:
                return context.getString(R.string.user_had_logout);
            case ErrorCode.USER_DISABLED:
                return context.getString(R.string.login_user_disabled);
            default:
                break;
        }
        return "";
    }
}
