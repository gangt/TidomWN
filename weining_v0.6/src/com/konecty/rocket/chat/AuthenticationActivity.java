/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.konecty.rocket.chat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lssl.activity.SuneeeApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.activity.WebMainActivity;
import com.xiangpu.activity.person.GestureLoginActivity;
import com.xiangpu.activity.person.LoginActivity2;
import com.xiangpu.appversion.CheckVersionService;
import com.xiangpu.bean.AuthenticationBean;
import com.xiangpu.common.Constants;
import com.xiangpu.dialog.CustomerDialog;
import com.xiangpu.interfaces.LoginInterface;
import com.xiangpu.interfaces.LogoutInterface;
import com.xiangpu.listener.NoDoubleClickListener;
import com.xiangpu.utils.ACache;
import com.xiangpu.utils.DialogUtil;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.SkipUtils;
import com.xiangpu.utils.TimeUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.Utils;
import com.xiangpu.utils.WebServiceUtil;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

public class AuthenticationActivity extends BaseActivity implements WebServiceUtil.OnDataListener {

    public static final String TAG = AuthenticationActivity.class.getSimpleName();

    private FingerprintManager fingerprintManager = null;
    private CancellationSignal mCancellationSignal = null;

    private ImageView logoImg;
    private AuthenticationBean bean;

    private CheckBox weilianHaiBtn;
    private CheckBox weilianBaoBtn;
    private CheckBox weilianWaBtn;

    private ImageButton websiteBtn;
    private ImageButton ivShop;

    /**
     * 记录点击次数，显示项目信息
     **/
    private int clickTitleTimes = 0;
    private long startTime;//第一次点击按钮的时间

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SuneeeApplication.getInstance().addActivity_(this);
        setContentView(R.layout.activity_authentication);

        initView();
        initNetBroadcastReceiver();
    }

    @Override
    protected void netBrResult(boolean netResult) {
        super.netBrResult(netResult);
        if (netResult) {
            websiteBtn.setClickable(true);
            ivShop.setClickable(true);
            showAuthenticationPageAndInitEvents();
        } else {
            websiteBtn.setClickable(false);
            ivShop.setClickable(false);
        }
    }

    /**
     * 初始化view
     */
    private void initView() {
        ImageView ivPassword = (ImageView) findViewById(R.id.auth_password);
        ivPassword.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (!weilianHaiBtn.isChecked() && !weilianBaoBtn.isChecked() && !weilianWaBtn.isChecked()) {
                    ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.choose_space));
                    return;
                }
                SuneeeApplication.weilianType = weilianHaiBtn.isChecked() ? Constants.WEI_LIAN_HAI : (weilianBaoBtn.isChecked() ? Constants.WEI_LIAN_BAO : Constants.WEI_LIAN_WA);
                Utils.startActivity(AuthenticationActivity.this, LoginActivity2.class);
            }
        });
        ImageView ivGesture = (ImageView) findViewById(R.id.auth_gesture);
        ivGesture.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (!weilianHaiBtn.isChecked() && !weilianBaoBtn.isChecked() && !weilianWaBtn.isChecked()) {
                    ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.choose_space));
                    return;
                }
                ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.gesture_can_not_use));
//                if (!SharedPrefUtils.getBooleanData(AuthenticationActivity.this, Constants.SWITCH_GESTURE_FINGERPRINT, false)) {
//                    ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.gestures_not_open));
//                    return;
//                }
//                ACache aCache = ACache.get(AuthenticationActivity.this);
//                // 得到当前用户的手势密码
//                byte[] gesturePassword = aCache.getAsBinary(Constants.GESTURE_PASSWORD);
//                if (gesturePassword == null) {
//                    ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.gestures_not_set));
//                    return;
//                }
//                SuneeeApplication.weilianType = weilianHaiBtn.isChecked() ? Constants.WEI_LIAN_HAI : (weilianBaoBtn.isChecked() ? Constants.WEI_LIAN_BAO : Constants.WEI_LIAN_WA);
//                Utils.startActivity(AuthenticationActivity.this, GestureLoginActivity.class);
            }
        });
        ImageView ivFinger = (ImageView) findViewById(R.id.auth_finger);
        ivFinger.post(new Runnable() {
            @Override
            public void run() {
                if (!weilianHaiBtn.isChecked() && !weilianBaoBtn.isChecked() && !weilianWaBtn.isChecked()) {
                    ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.choose_space));
                    return;
                }
                SuneeeApplication.weilianType = weilianHaiBtn.isChecked() ? Constants.WEI_LIAN_HAI : (weilianBaoBtn.isChecked() ? Constants.WEI_LIAN_BAO : Constants.WEI_LIAN_WA);
                if (!SharedPrefUtils.getBooleanData(AuthenticationActivity.this, Constants.IS_OPEN_FINGERPRINT, false)) {
                    Log.e(AuthenticationActivity.class.getSimpleName(), getString(R.string.fingerprint_not_open));
                    return;
                }
                initFingerprint(false);
            }
        });
        ivFinger.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (!weilianHaiBtn.isChecked() && !weilianBaoBtn.isChecked() && !weilianWaBtn.isChecked()) {
                    ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.choose_space));
                    return;
                }
                ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.fingerprint_can_not_use));
//                SuneeeApplication.weilianType = weilianHaiBtn.isChecked() ? Constants.WEI_LIAN_HAI : (weilianBaoBtn.isChecked() ? Constants.WEI_LIAN_BAO : Constants.WEI_LIAN_WA);
//                if (!SharedPrefUtils.getBooleanData(AuthenticationActivity.this, Constants.IS_OPEN_FINGERPRINT, false)) {
//                    ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.fingerprint_not_open));
//                    return;
//                }
//                initFingerprint(true);
            }
        });

        logoImg = (ImageView) findViewById(R.id.imageView7);
        logoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showAppInfo();
            }
        });
        weilianHaiBtn = (CheckBox) findViewById(R.id.weilian_hai_button);
        weilianHaiBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weilianBaoBtn.setChecked(false);
                    weilianWaBtn.setChecked(false);
                    SharedPrefUtils.saveStringData(AuthenticationActivity.this, Constants.WEI_LIAN_TYPE, Constants.WEI_LIAN_HAI);
                } else {
                    SharedPrefUtils.saveStringData(AuthenticationActivity.this, Constants.WEI_LIAN_TYPE, null);
                }
            }
        });
        weilianBaoBtn = (CheckBox) findViewById(R.id.weilian_bao_button);
        weilianBaoBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weilianHaiBtn.setChecked(false);
                    weilianWaBtn.setChecked(false);
                    SharedPrefUtils.saveStringData(AuthenticationActivity.this, Constants.WEI_LIAN_TYPE, Constants.WEI_LIAN_BAO);
                } else {
                    SharedPrefUtils.saveStringData(AuthenticationActivity.this, Constants.WEI_LIAN_TYPE, null);
                }
            }
        });
        weilianWaBtn = (CheckBox) findViewById(R.id.weilian_wa_button);
        weilianWaBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    weilianHaiBtn.setChecked(false);
                    weilianBaoBtn.setChecked(false);
                    SharedPrefUtils.saveStringData(AuthenticationActivity.this, Constants.WEI_LIAN_TYPE, Constants.WEI_LIAN_WA);
                } else {
                    SharedPrefUtils.saveStringData(AuthenticationActivity.this, Constants.WEI_LIAN_TYPE, null);
                }
            }
        });

        String weilianType = SharedPrefUtils.getStringData(this, Constants.WEI_LIAN_TYPE, null);
        if (Constants.WEI_LIAN_HAI.equals(weilianType)) {
            weilianHaiBtn.setChecked(true);
        } else if (Constants.WEI_LIAN_BAO.equals(weilianType)) {
            weilianBaoBtn.setChecked(true);
        } else if (Constants.WEI_LIAN_WA.equals(weilianType)) {
            weilianWaBtn.setChecked(true);
        }

        websiteBtn = (ImageButton) findViewById(R.id.website_button);
        websiteBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (bean != null && bean.getHomeUrl() != null) {
                    String url = bean.getHomeUrl();
                    goWebMainActivity(url, "官网", "");
                } else {
                    ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.unopened_function));
                }
            }
        });
        ivShop = (ImageButton) findViewById(R.id.shop_logout);
        ivShop.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (bean != null && bean.getMallUrl() != null) {
                    String title = "象谱商城";
                    String actId = SharedPrefUtils.getStringData(AuthenticationActivity.this, "actId", null);
                    if (actId == null) {
                        actId = ((int) ((Math.random() * 9 + 1) * 10000)) + "";
                        SharedPrefUtils.saveStringData(AuthenticationActivity.this, "actId", actId);
                    }
                    String url = bean.getMallUrl() + "?actId=" + actId;
                    goWebMainActivity(url, title, "ShoppingMall");
                } else {
                    ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.unopened_function));
                }

            }
        });
    }

    /**
     * 数据请求
     */
    private void showAuthenticationPageAndInitEvents() {
        bean = DataSupport.where("orgName = ? and version = ? and env = ? ", getString(R.string.api_compcode), getString(R.string.api_version), getString(R.string.api_env)).findFirst(AuthenticationBean.class);
        initData(bean);
        showProgressDialog();
        WebServiceUtil.request(Constants.URL_CONFIG_AUTHENTICATION, "json", this);

        if (!SuneeeApplication.checkVersion) {
            CheckVersionService versionService = new CheckVersionService(AuthenticationActivity.this);
            versionService.checkVersion();
        }
    }

    /**
     * 显示app基本信息
     */
    private void showAppInfo() {
        clickTitleTimes++;
        if (clickTitleTimes == 1) {
            startTime = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - startTime < 10000) {
            if (clickTitleTimes == 5) {
                String packageName = Utils.getAppProcessName(this);//包名
                String appEnv = getString(R.string.api_env).equals("Android1") ? "生产环境" : "测试环境";//项目环境
                String version = getString(R.string.api_version);//app版本
                String compCode = getString(R.string.api_compcode);//集团编码
                new AlertDialog.Builder(this)
                        .setMessage("包名:" + packageName + "\n"
                                + "项目环境:" + appEnv + "\n"
                                + "版本号：" + version + "\n"
                                + "集团编码:" + compCode)
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clickTitleTimes = 0;
                                dialog.dismiss();
                            }
                        }).show();
            }
        } else {
            clickTitleTimes = 1;
            startTime = System.currentTimeMillis();
        }

    }

    @SuppressLint("NewApi")
    private void initFingerprint(boolean isShow) {
        boolean isOpenFingerprint = SharedPrefUtils.getBooleanData(this, Constants.IS_OPEN_FINGERPRINT, false);
        if (!isOpenFingerprint) {
            return;
        }
        if (isShow) {
            DialogUtil.showDialog(this, "提示", "请使用指纹登录", "取消");
            DialogUtil.dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (mCancellationSignal != null) {
                        mCancellationSignal.cancel();
                        mCancellationSignal = null;
                    }
                }
            });
        }
        if (fingerprintManager == null) {
            try {
                fingerprintManager = (FingerprintManager) this.getSystemService(Context.FINGERPRINT_SERVICE);
            } catch (Throwable e) {
                LogUtil.i(TAG, "have not class FingerprintManager");
            }
        }
        mCancellationSignal = new CancellationSignal();
        //开启指纹识别
        fingerprintManager.authenticate(null, mCancellationSignal, 0, new MyAuthenticationCallback(), null);
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        dismissProgressDialog();
        if (result == null) {
            if (mode.equals(Constants.URL_CONFIG_AUTHENTICATION)) {
                final CustomerDialog dialog = new CustomerDialog(AuthenticationActivity.this);
                dialog.show();
                dialog.setMessageText(getString(R.string.data_error_notice));
                dialog.setButtonEvent(getString(R.string.sure_notice_txt), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showProgressDialog();
                        WebServiceUtil.request(Constants.URL_CONFIG_AUTHENTICATION, "json", AuthenticationActivity.this);
                    }
                });
                dialog.setCancelButtonEvent(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                return;
            }
        }

        try {
            if (mode.equals(Constants.URL_CONFIG_AUTHENTICATION)) {
                if (!result.getString("code").equals("1")) {
                    ToastUtils.showCenterToast(this, result.getString("msg"));
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
                    bean = DataSupport.where("orgName = ? and version = ? and env = ? ", getString(R.string.api_compcode), getString(R.string.api_version), getString(R.string.api_env)).findFirst(AuthenticationBean.class);
                    initData(bean);
                }
            }
        } catch (JSONException e) {
            Log.e("error", "json格式数据异常");
            e.printStackTrace();
        }
    }

    private void initData(AuthenticationBean bean) {
        if (bean == null) return;
        ImageLoader.getInstance().loadImage(Constants.getConfigLogoPath(bean.getIndexLogoDir()), null);
        ImageLoader.getInstance().loadImage(Constants.getConfigLogoPath(bean.getLoginLogoDir()), null);
        ImageLoader.getInstance().loadImage(Constants.getConfigLogoPath(bean.getAuthLogoDir()), null);
        ImageLoader.getInstance().loadImage(Constants.getConfigLogoPath(bean.getAboutLogoDir()), null);
        ImageLoader.getInstance().displayImage(Constants.getConfigLogoPath(bean.getAuthLogoDir()), logoImg);
    }

    @Override
    public List<NameValuePair> onGetParamData(String mode) {
        return null;
    }

    @Override
    public String onGetParamDataString(String mode) {

        JSONObject json = new JSONObject();

        if (mode.equals(Constants.URL_CONFIG_AUTHENTICATION)) {
            try {
                json.put("orgName", getString(R.string.api_compcode));
                json.put("version", getString(R.string.api_version));
                json.put("env", getString(R.string.api_env));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return json.toString();
    }

    @SuppressLint("NewApi")
    public class MyAuthenticationCallback extends FingerprintManager.AuthenticationCallback {
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            LogUtil.i(TAG, "onAuthenticationSucceeded： " + "验证成功");
            DialogUtil.dismissDialog();

            final String account = SharedPrefUtils.getStringData(AuthenticationActivity.this, "userName", "");
            final String userPwd = SharedPrefUtils.getStringData(AuthenticationActivity.this, "psdWord", "");
            login(account, userPwd);
//            String strSessionId = SharedPrefUtils.getSessionId(AuthenticationActivity.this);
//            boolean logoutFirst = false;
//            if (!logoutFirst) {
//            } else {
//                LoginUtils.logout(AuthenticationActivity.this, strSessionId, false, Constants.DEVICE_TYPE, new LogoutInterface() {
//
//                    @Override
//                    public void logoutSuccess() {
//                        login(account, userPwd);
//                    }
//
//                    @Override
//                    public void logutFailed(String errorMsg) {
//                        ToastUtils.showCenterToast(AuthenticationActivity.this, errorMsg);
//                    }
//                });
//            }
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            LogUtil.i(TAG, "onAuthenticationError：    " + errorCode);
            if (errorCode != 5) {
                ToastUtils.showCenterToast(AuthenticationActivity.this, errString.toString());
            }
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            LogUtil.i(TAG, "onAuthenticationFailed：  " + "验证失败");
            ToastUtils.showCenterToast(AuthenticationActivity.this, getString(R.string.login_finger_try_again));
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            LogUtil.i(TAG, "onAuthenticationHelp：  " + helpString);
        }
    }

    private void login(String account, String userPwd) {
        LoginUtils.getLoginInfo(AuthenticationActivity.this, Constants.WEI_LIAN_HAI, account, userPwd, new LoginInterface() {
            @Override
            public void loginSuccess(String type) {
                SkipUtils.getInstance().skipToByUserPower(AuthenticationActivity.this, type);
            }

            @Override
            public void loginFailed(String errorMsg) {
                ToastUtils.showCenterToast(AuthenticationActivity.this, errorMsg);
            }
        });
    }

    protected void goWebMainActivity(String link, String title, String tagPlugin) {
        Intent intent = new Intent(AuthenticationActivity.this, WebMainActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("link", link);
        intent.putExtra("TagPlugin", tagPlugin);
        this.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SuneeeApplication.getInstance().removeActivity_(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            int currentVersion = Build.VERSION.SDK_INT;
            if (currentVersion > Build.VERSION_CODES.ECLAIR_MR1) {  //android 版本高于2.2
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                this.finish();
                SuneeeApplication.getInstance().removeALLActivity_();
            } else {    //android版本低于2.2，android 2.2之后，restartPackage()不可以强制将整个APP退出。
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                am.restartPackage(getPackageName());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
