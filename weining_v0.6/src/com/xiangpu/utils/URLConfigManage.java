package com.xiangpu.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiangpu.bean.UrlConfigBean;
import com.xiangpu.common.Constants;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class URLConfigManage implements WebServiceUtil.OnDataListener {

    private static final String TAG = "URLConfigManage";
    public Map<String, Integer> POSMap = new HashMap<>();
    public static Map<Integer, UrlConfigBean> configBeanMap = new HashMap();

    public String weilianType = null;
    public String compId = null;
    public Handler handler = null;
    public boolean doFinish = true;

    public URLConfigManage() {

        POSMap.put("sdg", 1);//视点官
        POSMap.put("cjg", 2);//场景官
        POSMap.put("hlg", 3);//活流官
        POSMap.put("syzx", 4);//商业中心

        POSMap.put("jdq", 5);//焦点圈
        POSMap.put("jdq_url", 14);//焦点圈url

        POSMap.put("dzl", 6);//定子链
        POSMap.put("zzl", 7);//转子链
        POSMap.put("lzl", 8);//粒子链
        POSMap.put("xxzx", 9);//学习中心

        POSMap.put("skq", 10);//时空圈

        POSMap.put("hnq", 11);//汇能器
        POSMap.put("jnq", 12);//聚能器
        POSMap.put("fnq", 13);//赋能器
    }

    public void loadComplayConfig(boolean doFinish, String weilianType, String compId, Handler handler) {

        this.doFinish = doFinish;
        this.weilianType = weilianType;
        this.compId = compId;
        this.handler = handler;

        WebServiceUtil.request(Constants.URL_CONFIG_PATH, "json", this);

    }

    private UrlConfigBean getConfig(int index) {
        if (!configBeanMap.isEmpty()) {
            return configBeanMap.get(index);
        }
        return null;
    }

    public String getFunctionName(String key) {
        int index = POSMap.get(key);
        if (getConfig(index) == null) return null;
        return getConfig(index).getBtnIconName();
    }

    public String getFunctionUrl(String key) {
        int index = POSMap.get(key);
        if (getConfig(index) == null) return null;
        return getConfig(index).getRedirUrl();
    }

    public String getFunctionIcon(String key) {
        int index = POSMap.get(key);
        if (getConfig(index) == null) return null;
        return getImageIcon(getConfig(index).getBtnIconDir());
    }

    public String getUserAuth(String key) {
        int index = POSMap.get(key);
        if (getConfig(index) == null) return null;
        return getConfig(index).getUserAuth();
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {

        configBeanMap.clear();

        if (result == null) {
            if (mode.equals(Constants.URL_CONFIG_PATH)) {
                initData();
                if (configBeanMap.size() > 0 && handler != null) {
                    handler.sendEmptyMessage(1);
                    return;
                }
                errorProc(-1, "请求主页接口超时");
                return;
            }
        }

        if (mode.equals(Constants.URL_CONFIG_PATH)) {
            try {
                if (!result.getString("code").equals("1")) {
                    errorProc(0, result.getString("msg"));
                    return;
                }

                JSONArray array = result.getJSONArray("data");
                if (array == null || array.length() < 13) {
                    errorProc(0, "配置信息不完整");
                    return;
                }
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    UrlConfigBean onLinebean = new Gson().fromJson(jsonObject.toString(), UrlConfigBean.class);
                    UrlConfigBean databaseBean = null;
                    if (onLinebean.getSubCompCode() == null) {
                        databaseBean = DataSupport.where("entrance = ? and compCode = ? and subCompCode is null and btnPosId = ?", weilianType.toLowerCase(), onLinebean.getCompCode(), onLinebean.getBtnPosId() + "").findFirst(UrlConfigBean.class);
                    } else {
                        databaseBean = DataSupport.where("entrance = ? and compCode = ? and subCompCode = ? and btnPosId = ?", weilianType.toLowerCase(), onLinebean.getCompCode(), onLinebean.getSubCompCode(), onLinebean.getBtnPosId() + "").findFirst(UrlConfigBean.class);
                    }
                    if (databaseBean == null) {
                        onLinebean.save();
                    } else {
                        boolean update = TimeUtils.getTimeCompare(databaseBean.getUpdateTime(), onLinebean.getUpdateTime());
                        if (update) {
                            if (onLinebean.getSubCompCode() == null) {
                                onLinebean.updateAll("entrance =? and compCode = ? and subCompCode is null and btnPosId = ?", weilianType.toLowerCase(), onLinebean.getCompCode(), onLinebean.getBtnPosId() + "");
                            } else {
                                onLinebean.updateAll("entrance =? and compCode = ? and subCompCode = ? and btnPosId = ?", weilianType.toLowerCase(), onLinebean.getCompCode(), onLinebean.getSubCompCode(), onLinebean.getBtnPosId() + "");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            initData();

            if (handler != null) {
                handler.sendEmptyMessage(1);
            }
        }
    }

    private void initData() {
        List<UrlConfigBean> urlConfigBeanList = null;
        if (SuneeeApplication.getInstance().getApplicationContext().getString(R.string.api_compcode).equals(compId)) {
            urlConfigBeanList = DataSupport.where("entrance =? and compCode = ? and subCompCode is null ", weilianType.toLowerCase(), SuneeeApplication.getInstance().getApplicationContext().getString(R.string.api_compcode)).order("btnPosId asc").find(UrlConfigBean.class);
        } else {
            if (compId == null) {
                urlConfigBeanList = DataSupport.where("entrance =? and compCode = ? and subCompCode is null", weilianType.toLowerCase(), SuneeeApplication.getInstance().getApplicationContext().getString(R.string.api_compcode)).order("btnPosId asc").find(UrlConfigBean.class);
            } else {
                urlConfigBeanList = DataSupport.where("entrance =? and compCode = ? and subCompCode = ?", weilianType.toLowerCase(), SuneeeApplication.getInstance().getApplicationContext().getString(R.string.api_compcode), compId).order("btnPosId asc").find(UrlConfigBean.class);
            }
        }
        for (UrlConfigBean urlConfigBean : urlConfigBeanList) {
            configBeanMap.put(urlConfigBean.getBtnPosId(), urlConfigBean);
        }
        loadImage();
    }

    @Override
    public List<NameValuePair> onGetParamData(String mode) {
        return null;
    }

    @Override
    public String onGetParamDataString(String mode) {
        JSONObject json = new JSONObject();

        try {
            json.put("compCode", SuneeeApplication.getInstance().getApplicationContext().getString(R.string.api_compcode));
            if (Constants.WEI_LIAN_HAI.equals(weilianType)) {
                if (compId != null && !SuneeeApplication.getInstance().getApplicationContext().getString(R.string.api_compcode).equals(compId)) {
                    json.put("subCompCode", compId);
                }
            }
            json.put("entrance", weilianType);
            json.put("appVersion", SuneeeApplication.getInstance().getApplicationContext().getString(R.string.api_version));
            json.put("appEnvName", SuneeeApplication.getInstance().getApplicationContext().getString(R.string.api_env));
            json.put("isActived", 1);
            Log.e("主页配置参数", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private void loadImage() {
        for (int i = 1; i <= configBeanMap.size(); i++) {
            UrlConfigBean bean = configBeanMap.get(i);
            if (bean != null && bean.getBtnIconDir() != null && bean.getBtnIconDir().length() > 0) {
                String imageUrl = getImageIcon(bean.getBtnIconDir());
                ImageLoader.getInstance().loadImage(imageUrl, null);
            }
        }
    }

    private String getImageIcon(String imgUrl) {
        if (imgUrl != null && imgUrl.length() > 0) {
            return Constants.getConfigLogoPath(imgUrl);
        }
        return null;
    }

    private void errorProc(int type, String msg) {
        if (handler != null) {
            Message message;
            if (doFinish) { // -1 关闭当前页面
                message = handler.obtainMessage(-1, type, 0, msg);
            } else { // -2 保留当前页面
                message = handler.obtainMessage(-2, type, 0, msg);
            }
            handler.sendMessage(message);
        }
    }

}
