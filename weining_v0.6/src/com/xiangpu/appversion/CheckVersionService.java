package com.xiangpu.appversion;

import android.app.Activity;

import com.lssl.activity.SuneeeApplication;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.WebServiceUtil;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/12/26 0026.
 * Info：
 */

public class CheckVersionService implements WebServiceUtil.OnDataListener {
    private Activity activity = null;
    private boolean showTag = false; // 默认不show提示语

    public CheckVersionService(Activity activity) {
        this.activity = activity;
    }

    public void checkVersion() {
        WebServiceUtil.request(Constants.moduleVersion, "json", this);
    }

    public void setShowTag(boolean tag) {
        this.showTag = tag;
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        LogUtil.d("result" + result);
        if (result == null) {
            ToastUtils.showCenterToast(activity, "数据异常，请重试");
            return;
        }
        try {
            if (mode != null && mode.equals(Constants.moduleVersion)) {
                if (result.getInt("code") == 1) {
                    JSONObject version = result.getJSONObject("data");
                    String sysUrl = version.getString("upgradeDir");
                    sysUrl = Constants.GET_APP_VERSION + "/" + sysUrl;
                    String verDst = version.getString("verDst");
                    String appVersion = version.getString("verNo");
                    SuneeeApplication.getInstance().getAppServerVersion(sysUrl, appVersion, verDst);
                    checkVersion(appVersion, sysUrl, verDst);
                } else {
					if (showTag) {
						ToastUtils.showCenterToast(activity, "当前为最新版本");
					}
				}
            }
        } catch (JSONException e) {
            LogUtil.e(e.getMessage(), e);
        }
    }

    @Override
    public List<NameValuePair> onGetParamData(String mode) {
        return null;
    }

    @Override
    public String onGetParamDataString(String mode) {
        JSONObject json = new JSONObject();
        if (mode.equals(Constants.moduleVersion)) {
            try {
                json.put("compCode", "WEINING");
                json.put("verNo", "1");
                json.put("env", "android");
            } catch (JSONException e) {
                LogUtil.e(e.getMessage(), e);
            }
        }
        return json.toString();
    }

    /**
     * appVersion 服务器上版本号， sysUrl app下载地址
     */
    public void checkVersion(String appVersion, String sysUrl, String des) {
        try {
            String strDes = "";
            String localVer = SuneeeApplication.getInstance().getAppVersion();
            LogUtil.d("appVersion: " + appVersion + "localVer: " + localVer);
            String local_ver[] = localVer.split("\\.");
            String remote_ver[] = appVersion.split("\\.");
            int v2 = 0;
            if (remote_ver.length == 1) {
                v2 = Integer.parseInt(remote_ver[0]) * 100;
            } else if (remote_ver.length == 2) {
                v2 = Integer.parseInt(remote_ver[0]) * 100 + Integer.parseInt(remote_ver[1]) * 10;
            } else if (remote_ver.length == 3) {
                v2 = Integer.parseInt(remote_ver[0]) * 100 + Integer.parseInt(remote_ver[1]) * 10 + Integer.parseInt(remote_ver[2]);
            }

            int v1 = Integer.parseInt(local_ver[0]) * 100 + Integer.parseInt(local_ver[1]) * 10 + Integer.parseInt(local_ver[2]);
            if (v1 < v2) {
                if (des == null || des.equals("")) {
                    strDes = "发现有新版本，是否升级？";
                } else {
                    strDes = des;
                }
                UpdataAppVersion updataVersion = new UpdataAppVersion(activity, sysUrl, appVersion, strDes, "appVersion.apk");
                new Thread(updataVersion).start();
            } else {
                if (showTag) {
                    ToastUtils.showCenterToast(activity, "当前为最新版本");
                }
            }
            SuneeeApplication.checkVersion = true;
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

}
