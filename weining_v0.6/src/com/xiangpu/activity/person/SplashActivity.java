package com.xiangpu.activity.person;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.konecty.rocket.chat.AuthenticationActivity;
import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.action.GetAuthenticationInfoAction;
import com.xiangpu.action.GetAuthenticationInfoInterface;
import com.xiangpu.action.IdentityCheckAction;
import com.xiangpu.activity.AutoLoginActivity;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.interfaces.LoginInterface;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.NetWorkUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.SkipUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.Utils;

import chat.rocket.android.activity.ChatMainActivity;

/**
 * Created by huangda on 2017/9/8.
 */

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);

        final String type = SharedPrefUtils.getStringData(this, Constants.WEI_LIAN_TYPE, null);
        final String userPwd = SharedPrefUtils.getStringData(this, "psdWord", "");

        if (null == PushServiceFactory.getCloudPushService()) {
            LogUtil.e(TAG, "阿里云SDk环境初始化失败");
        } else {
            LogUtil.e(TAG, "Ali Push DeviceId:" + PushServiceFactory.getCloudPushService().getDeviceId());
        }

        SharedPrefUtils.saveSessionId(this, null);
        SharedPrefUtils.saveUserId(this, null);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!StringUtils.isEmpty(type) && !StringUtils.isEmpty(userPwd)) {
                    boolean isOpened = IdentityCheckAction.isOpened(SplashActivity.this);
                    int state = IdentityCheckAction.checkMethods(SplashActivity.this);
                    if (isOpened) {
                        Intent intent = new Intent(SplashActivity.this, IdentityCheckActivity.class);
                        intent.putExtra("method", state);
                        startActivityForResult(intent, 100);
                    } else {
                        doResquest(type);
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 3000);
    }

    private void doResquest(String type) {
        if (!NetWorkUtils.isNetworkConnected(this)) {
            Utils.startActivity(SplashActivity.this, AutoLoginActivity.class);
            finish();
            return;
        }
        GetAuthenticationInfoAction.getAuthenticationInfo(this, new GetAuthenticationInfoInterface() {
            @Override
            public void success(String data) {

            }

            @Override
            public void failed(String error) {
            }
        });
        String account = SharedPrefUtils.getStringData(this, "userName", "");
        String userPwd = SharedPrefUtils.getStringData(this, "psdWord", "");
        LoginUtils.getLoginInfo(this, type, account, userPwd, new LoginInterface() {

            @Override
            public void loginSuccess(String type) {
                dismissProgressDialog();
                SuneeeApplication.weilianType = type;
                SharedPrefUtils.saveStringData(context, Constants.WEI_LIAN_TYPE, type);
                // 点击消息不跳转页面
                if (SuneeeApplication.clickMsg) {
                    finish();
                    return;
                }
                SkipUtils.getInstance().autoLoadToMainActivity(true);
                SkipUtils.getInstance().skipByEntranceForAuto(SplashActivity.this, type);
                finish();
            }

            @Override
            public void loginFailed(String errorMsg) {
                dismissProgressDialog();
                LogUtil.e(TAG, "loginFailed errorMsg:" + errorMsg);
                //自动登录失败，就清掉本地保存的密码
                SharedPrefUtils.saveStringData(SplashActivity.this, "psdWord", null);
                if (errorMsg.equals(getString(R.string.login_timeout))) {
                    Utils.startActivity(SplashActivity.this, AutoLoginActivity.class);
                    finish();
                } else {
                    //如果登录返回的错误信息不是登录接口超时，就跳转到账号密码登录页
                    Utils.startActivity(SplashActivity.this, LoginActivity2.class);
                    finish();
                }
            }
        });
    }

}
