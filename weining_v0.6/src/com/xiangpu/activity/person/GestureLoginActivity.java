package com.xiangpu.activity.person;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.star.lockpattern.util.LockPatternUtil;
import com.star.lockpattern.widget.LockPatternView;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.interfaces.LoginInterface;
import com.xiangpu.interfaces.LogoutInterface;
import com.xiangpu.utils.ACache;
import com.xiangpu.utils.DialogUtil;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.SkipUtils;
import com.xiangpu.utils.ToastUtils;

import java.util.List;

/**
 * Created by Sym on 2015/12/24.
 */
public class GestureLoginActivity extends BaseActivity {

    private static final String TAG = GestureLoginActivity.class.getSimpleName();

    private LockPatternView lockPatternView;
    private TextView messageTv;
    private Button forgetGestureBtn;
    private FingerprintManager fingerprintManager;

    private ACache aCache;
    private static final long DELAYTIME = 600l;
    private byte[] gesturePassword;
    private Button bt_fingerprint_open_lock;

    private CancellationSignal mCancellationSignal = null;

    private String userPwd = "";
    private String account = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_login);

        account = SharedPrefUtils.getStringData(this, "userName", "");
        userPwd = SharedPrefUtils.getStringData(this, "psdWord", "");

        this.init();
    }

    @SuppressLint("NewApi")
    private void initFingerprint(boolean isShow) {
        boolean isOpenFingerprint = SharedPrefUtils.getBooleanData(this, Constants.IS_OPEN_FINGERPRINT, false);
        if (!isOpenFingerprint) {
            return;
        }
//        if (isShow) {
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
//        }
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

    @SuppressLint("NewApi")
    public class MyAuthenticationCallback extends FingerprintManager.AuthenticationCallback {
        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            LogUtil.i(TAG, "onAuthenticationSucceeded： " + "验证成功");
            DialogUtil.dismissDialog();

            showProgressDialog();
            loginGestureSuccess();
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            LogUtil.i(TAG, "onAuthenticationError：    " + errorCode);
            if (errorCode != 5) {
                ToastUtils.showCenterToast(GestureLoginActivity.this, errString.toString());
            }
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            LogUtil.i(TAG, "onAuthenticationFailed：  " + "验证失败");
            ToastUtils.showCenterToast(GestureLoginActivity.this, getString(R.string.login_finger_try_again));
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            LogUtil.i(TAG, "onAuthenticationHelp：  " + helpString);
        }

    }

    private void init() {
        messageTv = (TextView) findViewById(R.id.messageTv);
        forgetGestureBtn = (Button) findViewById(R.id.forgetGestureBtn);
        bt_fingerprint_open_lock = (Button) findViewById(R.id.bt_fingerprint_open_lock);
        lockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);

        forgetGestureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetGesturePasswrod();
            }
        });

        bt_fingerprint_open_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!SharedPrefUtils.getBooleanData(GestureLoginActivity.this, Constants.IS_OPEN_FINGERPRINT, false)) {
                    ToastUtils.showCenterToast(GestureLoginActivity.this, "未开启指纹验证");
                    return;
                }
                initFingerprint(true);
            }
        });

        aCache = ACache.get(GestureLoginActivity.this);
        // 得到当前用户的手势密码
        gesturePassword = aCache.getAsBinary(Constants.GESTURE_PASSWORD);
        lockPatternView.setOnPatternListener(patternListener);
        updateStatus(Status.DEFAULT);
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
        messageTv.setText(status.strId);
//        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;

            case ERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;

            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                showProgressDialog();
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势登录成功（去首页）
     */
    private void loginGestureSuccess() {
        login();
//        String strSessionId = SharedPrefUtils.getSessionId(this);
//        boolean logoutFirst = false;
//        if (!logoutFirst) {
//        } else {
//            LoginUtils.logout(this, strSessionId, false, Constants.DEVICE_TYPE, new LogoutInterface() {
//
//                @Override
//                public void logoutSuccess() {
//                    login();
//                }
//
//                @Override
//                public void logutFailed(String errorMsg) {
//                    dismissProgressDialog();
//                    ToastUtils.showCenterToast(GestureLoginActivity.this, errorMsg);
//                }
//            });
//        }
    }

    private void login() {
        LoginUtils.getLoginInfo(GestureLoginActivity.this, Constants.WEI_LIAN_HAI, account, userPwd, new LoginInterface() {
            @Override
            public void loginSuccess(String type) {
                dismissProgressDialog();
                SkipUtils.getInstance().skipToByUserPower(GestureLoginActivity.this, type);
            }

            @Override
            public void loginFailed(String errorMsg) {
                dismissProgressDialog();
                ToastUtils.showCenterToast(GestureLoginActivity.this, errorMsg);
            }
        });
    }

    /**
     * 忘记手势密码
     */
    void forgetGesturePasswrod() {
        Intent intent = new Intent(GestureLoginActivity.this, LoginActivity2.class);
        startActivity(intent);
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
}

