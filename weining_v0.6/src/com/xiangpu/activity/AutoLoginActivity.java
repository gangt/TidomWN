package com.xiangpu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.action.GetAuthenticationInfoAction;
import com.xiangpu.action.GetAuthenticationInfoInterface;
import com.xiangpu.activity.person.LoginActivity2;
import com.xiangpu.common.Constants;
import com.xiangpu.interfaces.LoginInterface;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.NetWorkUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.SkipUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.Utils;

/**
 * Created by fangfumin on 2017/10/12.
 */

public class AutoLoginActivity extends BaseActivity {

    private static final String TAG = "AutoLoginActivity";
    private LinearLayout autoLayout;
    private LinearLayout noticeLayout;
    private ImageView noticeImg;
    private ImageButton retryBtn;

    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);

        type = SharedPrefUtils.getStringData(this, Constants.WEI_LIAN_TYPE, null);
        String userPwd = SharedPrefUtils.getStringData(this, "psdWord", "");

        initView();

        if (!StringUtils.isEmpty(type) && !StringUtils.isEmpty(userPwd)) {
            autoLayout.setBackgroundColor(getResources().getColor(R.color.bg_color_1));
            noticeLayout.setVisibility(View.VISIBLE);
            noticeImg.setImageResource(R.drawable.no_net_image);
        } else {
            Intent intent = new Intent(this, LoginActivity2.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        autoLayout = (LinearLayout) findViewById(R.id.auto_layout);
        noticeLayout = (LinearLayout) findViewById(R.id.notice_layout);
        noticeImg = (ImageView) findViewById(R.id.notice_img);
        retryBtn = (ImageButton) findViewById(R.id.retry_btn);
        retryBtn.setOnClickListener(this);
    }

    private void doResquest(String type) {
        if (!NetWorkUtils.isNetworkConnected(this)) {
            autoLayout.setBackgroundColor(getResources().getColor(R.color.bg_color_1));
            noticeLayout.setVisibility(View.VISIBLE);
            noticeImg.setImageResource(R.drawable.no_net_image);
            ToastUtils.showCenterToast(AutoLoginActivity.this, "网络连接已断开");
            return;
        }
        showProgressDialog();
        GetAuthenticationInfoAction.getAuthenticationInfo(this, new GetAuthenticationInfoInterface() {
            @Override
            public void success(String data) {

            }

            @Override
            public void failed(String error) {
                ToastUtils.showCenterToast(AutoLoginActivity.this, error + "");
            }
        });
        String account = SharedPrefUtils.getStringData(this, "userName", "");
        String userPwd = SharedPrefUtils.getStringData(this, "psdWord", "");
        LoginUtils.getLoginInfo(this, type, account, userPwd, new LoginInterface() {

            @Override
            public void loginSuccess(String type) {
                dismissProgressDialog();
                SuneeeApplication.weilianType = type;
                SkipUtils.getInstance().skipByEntranceForAuto(AutoLoginActivity.this, type);
                finish();
            }

            @Override
            public void loginFailed(String errorMsg) {
                dismissProgressDialog();
                ToastUtils.showCenterToast(AutoLoginActivity.this, errorMsg);
                if (errorMsg.equals(getString(R.string.login_timeout))) {
                    autoLayout.setBackgroundColor(getResources().getColor(R.color.bg_color_1));
                    noticeLayout.setVisibility(View.VISIBLE);
                    noticeImg.setImageResource(R.drawable.weak_net_image);
                } else {
                    //如果登录返回的错误信息不是登录接口超时，就跳转到账号密码页面
                    Utils.startActivity(AutoLoginActivity.this, LoginActivity2.class);
                    finish();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retry_btn:
                noticeLayout.setVisibility(View.GONE);
                doResquest(type);
                break;

            default:
                super.onClick(v);
                break;
        }
    }
}
