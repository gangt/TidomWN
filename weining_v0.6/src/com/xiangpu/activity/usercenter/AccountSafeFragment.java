package com.xiangpu.activity.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.xiangpu.activity.person.GestureFingerprintLockSetActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.ToastUtils;

/**
 * Created by Administrator on 2017/12/2 0002.
 * Info：
 */

public class AccountSafeFragment extends Fragment implements View.OnClickListener {
    private View rootView = null;
    private LinearLayout layout_change_password = null;
    private LinearLayout layout_gesture_fingerprint_lock = null;
    private TextView tv_gesture_fingerprint_lock_status = null;
    private LinearLayout ll_face = null;

    public static AccountSafeFragment newInstance() {
        AccountSafeFragment fragment = new AccountSafeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_account_safe, container, false);
        findView();
        initListener();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean isopenGestureAndFingerprint = SharedPrefUtils.getBooleanData(getActivity(), Constants.SWITCH_GESTURE_FINGERPRINT, false);
        if (isopenGestureAndFingerprint) {
            tv_gesture_fingerprint_lock_status.setText(getResources().getText(R.string.open));
        } else {
            tv_gesture_fingerprint_lock_status.setText(getResources().getText(R.string.close));
        }
    }

    private void findView() {
        layout_change_password = (LinearLayout) rootView.findViewById(R.id.layout_change_password);
        layout_gesture_fingerprint_lock = (LinearLayout) rootView.findViewById(R.id.layout_gesture_fingerprint_lock);
        tv_gesture_fingerprint_lock_status = (TextView) rootView.findViewById(R.id.tv_gesture_fingerprint_lock_status);
        ll_face = (LinearLayout) rootView.findViewById(R.id.ll_face);
    }

    private void initListener() {
        layout_change_password.setOnClickListener(this);
        layout_gesture_fingerprint_lock.setOnClickListener(this);
        ll_face.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_change_password:
//                startActivity(new Intent(getActivity(), AccountPwdActivity.class));
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                break;

            case R.id.layout_gesture_fingerprint_lock:
                startActivity(new Intent(getActivity(), GestureFingerprintLockSetActivity.class));
                break;

            case R.id.ll_face:
                ToastUtils.showCenterToast(getActivity(), "暂未开放此功能");
                break;
        }
    }

}
