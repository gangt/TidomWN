package com.xiangpu.activity.person;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.megvii.facepp.sdk.Facepp;
import com.megvii.licensemanager.sdk.LicenseManager;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.ToastUtils;

import face.ConUtil;

/**
 * Created by fangfumin on 2017/5/13.
 * Info：
 */

public class GestureFingerprintLockSetActivity extends BaseActivity {
    protected static final String TAG = "GestureFingerprintLockSetActivity";
    private CheckBox cb_switch_gesture_fingerprint_lock;
    private CheckBox cb_switch_fingerprint_lock;
    private LinearLayout layout_gesture_fingerprint_set;
    private FingerprintManager fingerprintManager;
    private int currentAndroidVersion;

    private ImageView iv_title = null;
    private TextView tv_title = null;
    private LinearLayout layout_reset_gesture = null;
    private LinearLayout layout_my_face = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_fingerprint_set);
        initView();
        initEvent();
        loadPoint("", "个人中心");
        initNetBroadcastReceiver();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
        iv_title = (ImageView) findViewById(R.id.iv_title);
        iv_title.setImageResource(R.drawable.person_center_back);
        iv_title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("手势、指纹锁定");

        cb_switch_gesture_fingerprint_lock = (CheckBox) findViewById(R.id.cb_switch_gesture_fingerprint_lock);
        cb_switch_fingerprint_lock = (CheckBox) findViewById(R.id.cb_switch_fingerprint_lock);
        layout_gesture_fingerprint_set = (LinearLayout) findViewById(R.id.layout_gesture_fingerprint_set);
        layout_reset_gesture = (LinearLayout) findViewById(R.id.layout_reset_gesture);
        layout_reset_gesture.setOnClickListener(this);
        layout_my_face = (LinearLayout) findViewById(R.id.layout_my_face);
        layout_my_face.setOnClickListener(this);

        boolean isopenGestureAndFingerprint = SharedPrefUtils.getBooleanData(this, Constants.SWITCH_GESTURE_FINGERPRINT, false);
        if (isopenGestureAndFingerprint) {
            cb_switch_gesture_fingerprint_lock.setChecked(true);
            layout_gesture_fingerprint_set.setVisibility(View.VISIBLE);
        } else {
            cb_switch_gesture_fingerprint_lock.setChecked(false);
            layout_gesture_fingerprint_set.setVisibility(View.GONE);
        }
        boolean isOpenFingerprint = SharedPrefUtils.getBooleanData(this, Constants.IS_OPEN_FINGERPRINT, false);
        fingerprintManager = getSystemService(FingerprintManager.class);
        //如果手机录入了指纹，并且应用内指纹设置打开
        if (fingerprintManager.hasEnrolledFingerprints() && isOpenFingerprint) {
            cb_switch_fingerprint_lock.setChecked(true);
        } else {
            cb_switch_fingerprint_lock.setChecked(false);
        }
    }

    private void initEvent() {
        cb_switch_gesture_fingerprint_lock.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPrefUtils.saveBooleanData(GestureFingerprintLockSetActivity.this, Constants.SWITCH_GESTURE_FINGERPRINT, true);
                    layout_gesture_fingerprint_set.setVisibility(View.VISIBLE);
                } else {
                    SharedPrefUtils.saveBooleanData(GestureFingerprintLockSetActivity.this, Constants.SWITCH_GESTURE_FINGERPRINT, false);
                    SharedPrefUtils.saveBooleanData(GestureFingerprintLockSetActivity.this, Constants.IS_OPEN_GESTURE, false);
                    SharedPrefUtils.saveBooleanData(GestureFingerprintLockSetActivity.this, Constants.IS_OPEN_FINGERPRINT, false);
                    layout_gesture_fingerprint_set.setVisibility(View.GONE);
                    cb_switch_fingerprint_lock.setChecked(false);
                }
            }
        });
        cb_switch_fingerprint_lock.setOnClickListener(this);
        cb_switch_fingerprint_lock.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LogUtil.i(TAG, "开启指纹解锁");
                    SharedPrefUtils.saveBooleanData(GestureFingerprintLockSetActivity.this, Constants.IS_OPEN_FINGERPRINT, true);
                } else {
                    LogUtil.i(TAG, "关闭指纹解锁");
                    SharedPrefUtils.saveBooleanData(GestureFingerprintLockSetActivity.this, Constants.IS_OPEN_FINGERPRINT, false);

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_switch_fingerprint_lock:
                LogUtil.i(TAG, "点击");
                currentAndroidVersion = android.os.Build.VERSION.SDK_INT;
                if (currentAndroidVersion < 23) {
                    cb_switch_fingerprint_lock.setChecked(false);
                    ToastUtils.showCenterToast(this, "您的手机不支持指纹功能");
                    return;
                }

                if (!isFingerprintCanUse()) {
                    cb_switch_fingerprint_lock.setChecked(false);
                    ToastUtils.showCenterToast(this, "您的手机不支持指纹功能");
                    return;
                }
                // 如果手机上没有录入过指纹
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    cb_switch_fingerprint_lock.setChecked(false);
                    ToastUtils.showCenterToast(this, getString(R.string.account_safe_notice_open_fingerprint));
                }
                break;

            case R.id.layout_reset_gesture:
                startActivity(new Intent(this, CreateGestureActivity.class));
                break;

            case R.id.layout_my_face:
                // 脸部解锁
//                network();
                ToastUtils.showCenterToast(GestureFingerprintLockSetActivity.this, "暂未开放此功能，敬请期待");
                break;
        }

    }

    /**
     * 判断手机是否有指纹功能
     *
     * @return
     */
    @SuppressLint("NewApi")
    private boolean isFingerprintCanUse() {
        // 只有手机android版本大于等于23，并且手机设备有指纹功能，才能设置指纹解锁
        LogUtil.i(TAG, "当前手机安卓版本为：" + currentAndroidVersion);
        if (currentAndroidVersion >= 23 && fingerprintManager.isHardwareDetected()) {
            return true;
        }
        return false;
    }

    /**
     * 获取face++网络授权
     */
    private void network() {
        if (Facepp.getSDKAuthType(ConUtil.getFileContent(this, R.raw.megviifacepp_0_4_7_model)) == 2) { // 非联网授权
            authState(true);
            return;
        }

        final LicenseManager licenseManager = new LicenseManager(this);
        licenseManager.setExpirationMillis(Facepp.getApiExpirationMillis(this, ConUtil.getFileContent(this, R.raw.megviifacepp_0_4_7_model)));
        String uuid = ConUtil.getUUIDString(GestureFingerprintLockSetActivity.this);

        long apiName = Facepp.getApiName();
        licenseManager.setAuthTimeBufferMillis(0);
        licenseManager.takeLicenseFromNetwork(uuid, Constants.API_KEY, Constants.API_SECRET, apiName, LicenseManager.DURATION_30DAYS, "Landmark", "1", true, new LicenseManager.TakeLicenseCallback() {
            @Override
            public void onSuccess() {
                authState(true);
            }

            @Override
            public void onFailed(int i, byte[] bytes) {
                LogUtil.d(new String(bytes));
                authState(false);
            }
        });
    }

    private void authState(boolean isSuccess) {
        if (isSuccess) {
            Intent intent = new Intent(this, FaceLockActivity.class);
            startActivity(intent);
        } else {
            ToastUtils.showCenterToast(this, "联网授权失败!");
        }
    }

}

