//package com.xiangpu.bean;
//
//import com.xiangpu.utils.SharedPrefUtils;
//
//import android.content.Context;
//
//
//public class SessionClient {
//
//    public Context mContext;
//    public String cid;//个推CID
//
//    public SessionClient(Context context) {
//        this.mContext = context;
//    }
//
//    public void saveQiNiuToken(String token, String qiNiuDomainName) {
//        SharedPrefUtils.saveStringData(mContext, "suneee_qiniuToken", token);
//        SharedPrefUtils.saveStringData(mContext, "suneee_qiniuDomain", qiNiuDomainName);
//    }
//
//    public String getQiNiuToken() {
//        return SharedPrefUtils.getStringData(mContext, "suneee_qiniuToken", "");
//    }
//
//    public String getQiNiuDomain() {
//        return SharedPrefUtils.getStringData(mContext, "suneee_qiniuDomain", "");
//    }
//
//    public void saveAppToken(String appToken) {
//        SharedPrefUtils.saveStringData(mContext, "suneee_appToken", appToken);
//    }
//
//    public String getAppToken() {
//        return SharedPrefUtils.getStringData(mContext, "suneee_appToken", "");
//    }
//
//    public String getSessionId(String tag) {
//        return SharedPrefUtils.getStringData(mContext, "suneee_appSessionId" + tag, "");
//    }
//
//    public void saveSessionId(String tag, String sessionId) {
//        SharedPrefUtils.saveStringData(mContext, "suneee_appSessionId" + tag, sessionId);
//    }
//
//    public void savePushToken(String token) {
//        SharedPrefUtils.saveStringData(mContext, "suneee_pushToken", token);
//    }
//
//    public String getPushToken() {
//        return SharedPrefUtils.getStringData(mContext, "suneee_pushToken", "");
//    }
//
//}
