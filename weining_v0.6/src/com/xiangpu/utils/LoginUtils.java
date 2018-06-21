package com.xiangpu.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.UcpWebViewActivity;
import com.xiangpu.activity.person.LoginActivity2;
import com.xiangpu.activity.usercenter.PersonCenterActivity;
import com.xiangpu.bean.DataPowerBean;
import com.xiangpu.bean.UserCompBean;
import com.xiangpu.bean.UserInfo;
import com.xiangpu.bean.UserInfoBean;
import com.xiangpu.common.Constants;
import com.xiangpu.interfaces.LoginInterface;
import com.xiangpu.interfaces.LogoutInterface;
import com.xiangpu.utils.WebServiceUtil.OnDataListener;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by fangfumin on 2017/8/24.
 */

public class LoginUtils {

    public static void getLoginInfo(final Context context, final String type, final String username, final String password, final LoginInterface loginInterface) {

        WebServiceUtil.request(Constants.moduleLogin, "json", new OnDataListener() {
            @Override
            public void onReceivedData(String mode, JSONObject result) {
                if (result == null) {
                    loginInterface.loginFailed(context.getString(R.string.login_default));
                    return;
                }

                try {
                    String status = result.getString("status");
                    String errcodeMsg = result.getString("message");

                    String strdata = result.getString("data");
//                    Log.e("strdata", "" + strdata);
                    if ("1".equals(status) && (!TextUtils.isEmpty(strdata)) && !"null".equals(strdata)) {
                        JSONObject data = result.getJSONObject("data");
                        UserInfoBean.DataBean dataBean = new Gson().fromJson(data.toString(), UserInfoBean.DataBean.class);
                        String userName = dataBean.getUser().getUserName();
                        String mobile = dataBean.getUser().getMobile();
                        String email = dataBean.getUser().getEmail();
                        String name = dataBean.getUser().getName();
                        String photo = dataBean.getUser().getPhoto();
                        String aliasName = dataBean.getUser().getAliasName();
                        String userId = dataBean.getUser().getUserId() + "";

                        UserInfo userInfo = new UserInfo();
                        userInfo.setName(name);
                        userInfo.setPhoto(photo);
                        userInfo.setAliasName(aliasName);
                        userInfo.setUserId(userId);
                        SuneeeApplication.setUser(userInfo);

                        UserCompBean userCompBean = SuneeeApplication.getUserCompBean();
                        userCompBean.setUserType(dataBean.getUserType());

                        // 人脸相关的返回值
                        String facktoken = dataBean.getFacktoken();
                        String facedisValue = dataBean.getFacedisValue();
                        SharedPrefUtils.saveStringData(context, Constants.FACE_TOKEN, facktoken);
                        SharedPrefUtils.saveStringData(context, Constants.FACE_DIS_VALUE, facedisValue);

                        SharedPrefUtils.saveStringData(context, "userName", username);
                        SharedPrefUtils.saveStringData(context, "psdWord", password);
                        SharedPrefUtils.saveStringData(context, "userName_CommandCenter", userName);
                        SharedPrefUtils.saveStringData(context, "mobile", mobile);
                        SharedPrefUtils.saveStringData(context, "email", email);
                        SharedPrefUtils.saveStringData(context, "userId", userId);
                        SharedPrefUtils.saveUserId(context, userId);

                        String sessionId = dataBean.getUser().getSessionId();
                        SharedPrefUtils.saveSessionId(context, sessionId);

                        // TODO 逻辑修改:根据userType判断是否集团员工，根据hasPower判断是否子企业员工。
                        // TODO 当用户是集团员工时，七星页停留；当用户是子企业用户时，跳转到第一个有权限的子企业主页
                        if (data.get("dataPower") instanceof JSONObject) {
                            DataPowerBean dataPowerBean = new Gson().fromJson(data.getJSONObject("dataPower").toString(), DataPowerBean.class);
                            List<String> codes = dataPowerBean.getPowerCompanyCode();
                            if ("CUSER".equals(dataBean.getUserType())) {
                                if (codes.size() <= 0) {
                                    loginInterface.loginFailed(context.getString(R.string.no_limit_visit));
                                    return;
                                } else {
                                    SuneeeApplication.user.affiliatedCompanyId = codes.get(0);
                                }
                            } else if ("BUSER".equals(dataBean.getUserType())) {
                                SuneeeApplication.user.affiliatedCompanyId = "";
                            }

                        } else if (data.get("dataPower") instanceof String) {
                            loginInterface.loginFailed(data.getString("dataPower"));
                            return;
                        } else {
                            loginInterface.loginFailed("dataPower未知类型处理异常");
                            return;
                        }
                        SuneeeApplication.setSpatial(dataBean.getSpatial());

                        SharedPrefUtils.saveStringData(context, "actId", null);
                        if (dataBean.getSpatial().isEmpty()) {
                            loginInterface.loginFailed(context.getString(R.string.no_limit_visit));
                            return;
                        }
                        loginInterface.loginSuccess(type);
                    } else {
                        String code = result.getString("code");
                        String errorMsgByCode = NoticeMessageUtils.getErrorMsgByCode(context, code);
                        if (TextUtils.isEmpty(errorMsgByCode)) {
                            loginInterface.loginFailed(errcodeMsg);
                        } else {
                            loginInterface.loginFailed(errorMsgByCode);
                        }
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    loginInterface.loginFailed("接口json格式返回有误，请检查");
                } catch (JSONException e) {
                    e.printStackTrace();
                    loginInterface.loginFailed("数据格式有误");
                }
            }

            @Override
            public List<NameValuePair> onGetParamData(String mode) {
                return null;
            }

            @Override
            public String onGetParamDataString(String mode) {
                JSONObject json = new JSONObject();
                try {

                    json.put("enterpriseCode", context.getString(R.string.api_compcode));
                    json.put("clientIp", "127.0.0.1");
                    json.put("appCode", context.getString(R.string.api_appcode));
                    json.put("serverIp", "127.0.0.1");
                    json.put("encryptCode", "1234567899876543");
                    if (Constants.WEI_LIAN_HAI.equals(type)) {
                        json.put("entrance", Constants.HAI);
                    } else if (Constants.WEI_LIAN_BAO.equals(type)) {
                        json.put("entrance", Constants.BAO);
                    } else if (Constants.WEI_LIAN_WA.equals(type)) {
                        json.put("entrance", Constants.WA);
                    }

                    json.put("account", username);
                    String strPwd = "";
                    try {
                        strPwd = MD5Encoder.encode(password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    json.put("password", strPwd);
                    json.put("deviceNum", PushServiceFactory.getCloudPushService().getDeviceId());
                    json.put("deviceType", Constants.DEVICE_TYPE);
                    json.put("sign", VerifyUtils.signValueEncryption(json.toString(), context.getString(R.string.api_encryptCode)));
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return json.toString().replace("\\/", "/").replace("\\n", "");
            }
        });
    }

    public static void logout(final Context context, final String sessionId, final boolean syn, final String deviceType, final LogoutInterface logoutInterface) {
        WebServiceUtil.request(Constants.moduleLogout, "json", new OnDataListener() {
            @Override
            public void onReceivedData(String mode, JSONObject result) {
                if (result == null) {
                    logoutInterface.logutFailed(context.getString(R.string.login_default));
                    return;
                }
                SharedPrefUtils.saveSessionId(context, null);
                SuneeeApplication.getInstance().removeActivity_(PersonCenterActivity.class);
                SuneeeApplication.getInstance().removeActivity_(UcpWebViewActivity.class);
                // 退出登录清空个人信息的facetoken
                SharedPrefUtils.saveStringData(context, Constants.FACE_TOKEN, "");
                logoutInterface.logoutSuccess();
            }

            @Override
            public List<NameValuePair> onGetParamData(String mode) {
                return null;
            }

            @Override
            public String onGetParamDataString(String mode) {
                JSONObject json = new JSONObject();
                try {
                    json.put("sessionId", sessionId);
                    json.put("syn", syn);
                    json.put("deviceType", deviceType);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                return json.toString();
            }
        });
    }

    /**
     * Sessionid失效时退出登录逻辑
     *
     * @param context
     */
    public static void logoutSessionIdIsInvalid(Context context) {
//        SharedPrefUtils.saveBooleanData(context, Constants.SWITCH_GESTURE_FINGERPRINT, false);
//        SharedPrefUtils.saveBooleanData(context, Constants.IS_OPEN_FINGERPRINT, false);
        SharedPrefUtils.saveStringData(context, Constants.PSDWORD_SP, null);
        SharedPrefUtils.saveSessionId(context, null);
        SuneeeApplication.getInstance().finishAllActivities();

        Intent intent = new Intent(context, LoginActivity2.class);
        context.startActivity(intent);
    }

}
