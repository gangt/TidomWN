package com.xiangpu.action;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import com.xiangpu.common.Constants;
import com.xiangpu.utils.SharedPrefUtils;

/**
 * description: 身份校验
 * autour: Andy
 * date: 2018/2/1 16:29
 * update: 2018/2/1
 * version: 1.0
 */
public class IdentityCheckAction {

    private final static int NONE = 0;
    private final static int GESTURES = 1;
    private final static int FINGERPRINT = 2;
    private final static int GESTURES_FINGERPRINT = 3;

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isOpened(Context context) {
        boolean gestures = SharedPrefUtils.getBooleanData(context, Constants.IS_OPEN_GESTURE, false);
        boolean fingerprint = SharedPrefUtils.getBooleanData(context, Constants.IS_OPEN_FINGERPRINT, false)
                && context.getSystemService(FingerprintManager.class).hasEnrolledFingerprints();
        return gestures || fingerprint;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static int checkMethods(Context context) {
        boolean gestures = SharedPrefUtils.getBooleanData(context, Constants.IS_OPEN_GESTURE, false);
        boolean fingerprint = SharedPrefUtils.getBooleanData(context, Constants.IS_OPEN_FINGERPRINT, false)
                && context.getSystemService(FingerprintManager.class).hasEnrolledFingerprints();

        if (gestures && fingerprint) {
            return GESTURES_FINGERPRINT;
        } else if (fingerprint) {
            return FINGERPRINT;
        } else if (gestures) {
            return GESTURES;
        } else {
            return NONE;
        }
    }

}
