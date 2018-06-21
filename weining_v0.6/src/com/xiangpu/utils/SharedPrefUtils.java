package com.xiangpu.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.xiangpu.bean.AuthenticationBean;
import com.xiangpu.bean.UserInfo;

/**
 * 配置文件工具�?
 * Created by 20150604 on 2015/10/22.
 */
public class SharedPrefUtils {

    private static String CONFIG = "config";
    private static SharedPreferences sharedPreferences;

    /**
     * 保存字符�?
     */
    public static void saveStringData(Context context, String key, String value) {
        if (sharedPreferences == null && context != null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 获取字符�?
     */
    public static String getStringData(Context context, String key, String defValue) {
        if (sharedPreferences == null && null != context) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }

    /**
     * 获取AuthenticationBean对象数据
     */
    public static AuthenticationBean getAuthenticationData(Context context, String key, String defValue) {
        String data = getStringData(context, key, defValue);
        if (data == null) return null;
        return new Gson().fromJson(data, AuthenticationBean.class);
    }

    /**
     * 保存数�?�型数据
     */
    public static void saveintData(Context context, String key, int value) {
        if (sharedPreferences == null && context != null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * 获取数�?�型数据
     */
    public static int getIntData(Context context, String key, int defValue) {
        if (sharedPreferences == null && context != null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getInt(key, defValue);
    }

    /**
     * 保存布尔值数�?
     */
    public static boolean getBooleanData(Context context, String key, boolean defValue) {
        if (sharedPreferences == null && context != null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(key, defValue);
    }

    /**
     * 获取布尔值数�?
     */
    public static void saveBooleanData(Context context, String key, boolean value) {
        if (sharedPreferences == null && context != null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static void clearAllPreferences(Context context) {
        if (sharedPreferences == null && context != null) {
            sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().clear();
        sharedPreferences.edit().clear().commit();
    }

    /**
     * 存储用户数据
     *
     * @param context
     * @param userInfo
     */
    public static void saveUserInfoData(Context context, UserInfo userInfo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (userInfo.getName() != null) {
            editor.putString("user_name", userInfo.getName());
        }
        if (userInfo.getUserName() != null) {
            editor.putString("user_userName", userInfo.getUserName());
        }
        if (userInfo.getNick() != null) {
            editor.putString("user_nick", userInfo.getNick());
        }
        if (userInfo.getSex() != null) {
            editor.putString("user_sex", userInfo.getSex());
        }
        if (userInfo.getPhoto() != null) {
            editor.putString("user_photo", userInfo.getPhoto());
        }
        if (userInfo.getSignature() != null) {
            editor.putString("user_signature", userInfo.getSignature());
        }
        if (userInfo.getBackgroundImg() != null) {
            editor.putString("user_backgroundImg", userInfo.getBackgroundImg());
        }
        if (userInfo.getMobile() != null) {
            editor.putString("user_mobile", userInfo.getMobile());
        }
        if (userInfo.getAddress() != null) {
            editor.putString("user_address", userInfo.getAddress());
        }
        if (userInfo.getAliasName() != null) {
            editor.putString("user_aliasName", userInfo.getAliasName());
        }
        if (userInfo.getUserId() != null) {
            editor.putString("user_userId", userInfo.getUserId());
        }
        editor.commit();
    }

    /**
     * 获取用户数据
     *
     * @param context
     * @return
     */
    public static UserInfo getUserInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("user_name", null);
        String userName = sharedPreferences.getString("user_userName", null);
        String nick = sharedPreferences.getString("user_nick", null);
        String sex = sharedPreferences.getString("user_sex", null);
        String photo = sharedPreferences.getString("user_photo", null);
        String signature = sharedPreferences.getString("user_signature", null);
        String backgroundImg = sharedPreferences.getString("user_backgroundImg", null);
        String mobile = sharedPreferences.getString("user_mobile", null);
        String address = sharedPreferences.getString("user_address", null);
        String aliasName = sharedPreferences.getString("user_aliasName", null);
        String userId = sharedPreferences.getString("user_userId", null);
        UserInfo userInfo = new UserInfo();
        userInfo.setName(name);
        userInfo.setUserName(userName);
        userInfo.setNick(nick);
        userInfo.setSex(sex);
        userInfo.setPhoto(photo);
        userInfo.setSignature(signature);
        userInfo.setBackgroundImg(backgroundImg);
        userInfo.setMobile(mobile);
        userInfo.setAddress(address);
        userInfo.setAliasName(aliasName);
        userInfo.setUserId(userId);
        return userInfo;
    }

    /**
     * 保存userid数据
     *
     * @param context 上下文
     * @param userId  userid
     */
    public static void saveUserId(Context context, String userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("userId", userId).commit();
    }

    /**
     * 获取userid
     *
     * @param context 上下文
     * @return
     */
    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONFIG, Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", null);
    }

    /**
     * 保存Sessionid数据
     */
    public static void saveSessionId(Context context, String sessionId) {
        saveStringData(context, "suneee_appSessionId" + getUserId(context), sessionId);
    }

    /**
     * 获取Sessionid
     */
    public static String getSessionId(Context context) {
        return getStringData(context, "suneee_appSessionId" + getUserId(context), null);
    }

    /**
     * 保存SelectCompanyCode数据
     *
     * @param context  上下文
     * @param compCode 企业编码
     */
    public static void saveSelectCompanyCode(Context context, String compCode) {
        saveStringData(context, "select_company_code", compCode);
    }

    /**
     * 获取SelectCompanyCode
     *
     * @param context 上下文
     * @return
     */
    public static String getSelectCompanyCode(Context context) {
        return getStringData(context, "select_company_code", null);
    }

}
