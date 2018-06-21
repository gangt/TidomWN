package com.xiangpu.action;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.konecty.rocket.chat.R;
import com.xiangpu.bean.AuthenticationBean;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.TimeUtils;
import com.xiangpu.utils.WebServiceUtil;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by fangfumin on 2017/10/12.
 */

public class GetAuthenticationInfoAction {

    public static void getAuthenticationInfo(final Context context, final GetAuthenticationInfoInterface infoInterface) {

        WebServiceUtil.request(Constants.URL_CONFIG_AUTHENTICATION, "json", new WebServiceUtil.OnDataListener() {
            @Override
            public void onReceivedData(String mode, JSONObject result) {
                if (result == null) {
                    infoInterface.failed("鉴权接口请求超时");
                    return;
                }
                if (mode.equals(Constants.URL_CONFIG_AUTHENTICATION)) {
                    try {
                        if (!result.getString("code").equals("1")) {
                            infoInterface.failed(result.getString("msg"));
                            return;
                        }
                        JSONArray array = result.getJSONArray("data");
                        Log.e("array", "" + array);
                        if (array != null && !array.isNull(0)) {
                            JSONObject json = array.getJSONObject(0);
                            AuthenticationBean onLinebean = new Gson().fromJson(json.toString(), AuthenticationBean.class);
                            AuthenticationBean databaseBean = DataSupport.where("orgName = ? and version = ? and env = ? ", onLinebean.getOrgName(), onLinebean.getVersion(), onLinebean.getEnv()).findFirst(AuthenticationBean.class);
                            if (databaseBean == null) {
                                onLinebean.save();
                            } else {
                                boolean update = TimeUtils.getTimeCompare(databaseBean.getUpdateTime(), onLinebean.getUpdateTime());
                                if (update) {
                                    onLinebean.updateAll("orgName = ? and version = ? and env = ? ", onLinebean.getOrgName(), onLinebean.getVersion(), onLinebean.getEnv());
                                }
                            }
                            infoInterface.success("");
//                            AuthenticationBean bean = DataSupport.where("orgName = ? and version = ? and env = ? ", context.getString(R.string.api_compcode), context.getString(R.string.api_version), context.getString(R.string.api_env)).findFirst(AuthenticationBean.class);
                        } else {
                            infoInterface.failed("鉴权接口data数据返回为空");
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                        infoInterface.failed("鉴权接口json数据格式有误");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        infoInterface.failed("鉴权接口json数据解析有误");
                    }
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
                    json.put("orgName", context.getString(R.string.api_compcode));
                    json.put("version", context.getString(R.string.api_version));
                    json.put("env", context.getString(R.string.api_env));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return json.toString();
            }
        });
    }

}
