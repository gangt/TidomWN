package com.xiangpu.activity.person;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.widget.LockPatternView;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.bean.UserInfo;
import com.xiangpu.common.Constants;
import com.xiangpu.interfaces.LoginInterface;
import com.xiangpu.utils.ACache;
import com.xiangpu.utils.DialogUtil;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.SkipUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.ToastUtils;

import java.util.List;

/**
 * description: 身份验证界面
 * autour: Andy
 * date: 2018/2/2 15:25
 * update: 2018/2/2
 * version: 1.0
 */
public class IdentityCheckActivity extends BaseActivity {

    private int method;

    private RoundedImageView avatarIv;
    private TextView nameTv;

    private FingerprintManager fingerprintManager = null;
    private CancellationSignal mCancellationSignal = null;
    private ImageView fingerIv;

    private TextView spacingTv;

    private byte[] gesturePassword;
    private LockPatternView lockPatternView;
    private TextView loginBtn;

    private String userPwd;
    private String account;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identitycheck_layout);

        method = getIntent().getIntExtra("method", 0);

        account = SharedPrefUtils.getStringData(this, "userName", "");
        userPwd = SharedPrefUtils.getStringData(this, "psdWord", "");

        initView();
        initData();

    }

    private void initView() {

        avatarIv = (RoundedImageView) findViewById(R.id.avatar);
        nameTv = (TextView) findViewById(R.id.name);
        fingerIv = (ImageView) findViewById(R.id.finger_img);
        spacingTv = (TextView) findViewById(R.id.spacing_tv);
        lockPatternView = (LockPatternView) findViewById(R.id.lockpattern_view);
        lockPatternView.setSelectColor(true);
        loginBtn = (TextView) findViewById(R.id.account_login_btn);

        if (method == 1) {
            spacingTv.setVisibility(View.GONE);
            fingerIv.setVisibility(View.INVISIBLE);
            lockPatternView.setVisibility(View.VISIBLE);
            ACache aCache = ACache.get(this);
            // 得到当前用户的手势密码
            gesturePassword = aCache.getAsBinary(Constants.GESTURE_PASSWORD);
            lockPatternView.setOnPatternListener(patternListener);
        } else if (method == 2) {
            spacingTv.setVisibility(View.VISIBLE);
            fingerIv.setVisibility(View.VISIBLE);
            initFingerprint(false);
            lockPatternView.setVisibility(View.GONE);
        } else if (method == 3) {
            spacingTv.setVisibility(View.GONE);

            fingerIv.setVisibility(View.VISIBLE);
            initFingerprint(false);

            lockPatternView.setVisibility(View.VISIBLE);
            // 得到当前用户的手势密码
            gesturePassword = ACache.get(this).getAsBinary(Constants.GESTURE_PASSWORD);
            lockPatternView.setOnPatternListener(patternListener);
        }

    }

    private void initData() {
        UserInfo userInfo = SuneeeApplication.getInstance().getUser();
        if (!StringUtils.isEmpty(userInfo.getPhoto())) {
            ImageLoader.getInstance().displayImage(Constants.BASE_HEADIMG_URL + userInfo.getPhoto(), avatarIv);
        }
        if (!StringUtils.isEmpty(userInfo.getAliasName())) {
            nameTv.setText(userInfo.getAliasName() + "");
        } else if (!StringUtils.isEmpty(userInfo.getName())) {
            nameTv.setText(userInfo.getName() + "");
        } else {
            nameTv.setText("");
        }

        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.account_login_btn:
                Intent intent = new Intent(this, LoginActivity2.class);
                startActivity(intent);
                finish();
                break;

            default:
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
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
                Log.e("IdentityCheckActivity", "have not class FingerprintManager");
            }
        }
        mCancellationSignal = new CancellationSignal();
        //开启指纹识别
        fingerprintManager.authenticate(null, mCancellationSignal, 0, new MyAuthenticationCallback(), null);

    }

    @SuppressLint("NewApi")
    public class MyAuthenticationCallback extends FingerprintManager.AuthenticationCallback {
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            DialogUtil.dismissDialog();

            login(account, userPwd);
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
//            if (errorCode != 5) {
            ToastUtils.showCenterToast(IdentityCheckActivity.this, errString.toString());
//            }
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            ToastUtils.showCenterToast(IdentityCheckActivity.this, getString(R.string.login_finger_try_again));
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
        }
    }

    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if (pattern != null) {
                if (LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                    updateStatus(Status.CORRECT);
                } else {
                    updateStatus(Status.ERROR);
                }
            }
        }
    };

    /**
     * 更新状态
     *
     * @param status
     */
    private void updateStatus(Status status) {
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;

            case ERROR:
                ToastUtils.showCenterToast(this, getString(status.strId));
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(1000);
                break;

            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                login(account, userPwd);
                break;
        }
    }

    private enum Status {
        // 默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        // 密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        // 密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }

    private void login(String account, String userPwd) {
        showProgressDialog();
        LoginUtils.getLoginInfo(IdentityCheckActivity.this, Constants.WEI_LIAN_HAI, account, userPwd, new LoginInterface() {
            @Override
            public void loginSuccess(String type) {
                dismissProgressDialog();
                SkipUtils.getInstance().skipToByUserPower(IdentityCheckActivity.this, type);
            }

            @Override
            public void loginFailed(String errorMsg) {
                dismissProgressDialog();
                ToastUtils.showCenterToast(IdentityCheckActivity.this, errorMsg);
            }
        });
    }

}
