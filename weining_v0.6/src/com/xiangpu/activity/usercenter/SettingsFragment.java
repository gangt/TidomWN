package com.xiangpu.activity.usercenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.konecty.rocket.chat.AuthenticationActivity;
import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.person.LoginActivity2;
import com.xiangpu.bean.ErrorCode;
import com.xiangpu.common.Constants;
import com.xiangpu.dialog.CustomProgressDialog;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.NoticeMessageUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.WebServiceUtil;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2017/12/3 0003.
 * Info：
 */

public class SettingsFragment extends Fragment implements View.OnClickListener, WebServiceUtil.OnDataListener {

    private View rootView = null;
    private CheckBox checkbox_alarmvoice = null;
    private CheckBox checkbox_alarmzhendong = null;
    private CheckBox checkbox_message_push = null;
    private TextView tv_exit = null;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_personal_settings, container, false);
        findView();
        initListener();
        return rootView;
    }

    private void findView() {
        checkbox_alarmvoice = (CheckBox) rootView.findViewById(R.id.checkbox_alarmvoice);
        checkbox_alarmzhendong = (CheckBox) rootView.findViewById(R.id.checkbox_alarmzhendong);
        checkbox_message_push = (CheckBox) rootView.findViewById(R.id.checkbox_message_push);
        tv_exit = (TextView) rootView.findViewById(R.id.tv_exit);
    }

    private void initListener() {
        checkbox_alarmvoice.setOnClickListener(this);
        checkbox_alarmzhendong.setOnClickListener(this);
        checkbox_message_push.setOnClickListener(this);
        tv_exit.setOnClickListener(this);

        if (SharedPrefUtils.getBooleanData(getActivity(), "AlarmZhendongSet", false)) {
            checkbox_alarmzhendong.setChecked(true);
        }
        if (SharedPrefUtils.getBooleanData(getActivity(), "AlarmVoiceSet", false)) {
            checkbox_alarmvoice.setChecked(true);
        }
        if (SharedPrefUtils.getBooleanData(getActivity(), Constants.IS_OPEN_MESSAGE_PUSH, true)) {
            checkbox_message_push.setChecked(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkbox_alarmvoice.setChecked(getAlarmVoice());
        checkbox_alarmzhendong.setChecked(getAlarmzhendong());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkbox_alarmvoice:
                checkbox_alarmvoice.setChecked(checkbox_alarmvoice.isChecked());
                setAlarmVoice(checkbox_alarmvoice.isChecked());
                break;

            case R.id.checkbox_alarmzhendong:
                checkbox_alarmzhendong.setChecked(checkbox_alarmzhendong.isChecked());
                setAlarmzhendong(checkbox_alarmzhendong.isChecked());
                break;

            case R.id.checkbox_message_push:
//                checkbox_message_push.setChecked(checkbox_message_push.isChecked());
//                SharedPrefUtils.saveBooleanData(getActivity(), Constants.IS_OPEN_MESSAGE_PUSH, checkbox_message_push.isChecked());
                break;

            case R.id.tv_exit:
                String strSessionId = SharedPrefUtils.getSessionId(getActivity());
                if (TextUtils.isEmpty(strSessionId)) {
                    LoginUtils.logoutSessionIdIsInvalid(getActivity());
                    return;
                }
                showProgressDialog();
                WebServiceUtil.request(Constants.moduleLogout, "json", this);
                break;
        }
    }

    private void setAlarmVoice(boolean bShow) {
        SharedPrefUtils.saveBooleanData(getActivity(), "AlarmVoiceSet", bShow);
    }

    private boolean getAlarmVoice() {
        return SharedPrefUtils.getBooleanData(getActivity(), "AlarmVoiceSet", false);
    }

    private void setAlarmzhendong(boolean bShow) {
        SharedPrefUtils.saveBooleanData(getActivity(), "AlarmZhendongSet", bShow);
    }

    private boolean getAlarmzhendong() {
        return SharedPrefUtils.getBooleanData(getActivity(), "AlarmZhendongSet", false);
    }

    protected CustomProgressDialog mProgressDialog;

    /**
     * 显示自定义进度框
     */
    protected void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = new CustomProgressDialog(getActivity(), R.style.CustomProgressDialog);
        mProgressDialog.show();
    }

    /**
     * 取消自定义进度框
     */
    protected void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        dismissProgressDialog();
        if (result == null) {
            ToastUtils.showCenterToast(getActivity(), "退出失败！");
            return;
        }
        try {
            if (mode != null && mode.equals(Constants.moduleLogout)) {

                if (!result.getString("status").equals("1")) {
                    String code = result.getString("code");
                    String errorMsgByCode = NoticeMessageUtils.getErrorMsgByCode(getActivity(), code);
                    if (TextUtils.isEmpty(errorMsgByCode)) {
                        ToastUtils.showCenterToast(getActivity(), result.getString("message"));
                    } else {
                        ToastUtils.showCenterToast(getActivity(), errorMsgByCode);
                        if (ErrorCode.CONNECTION_FAIL.equals(code)) {
                            LoginUtils.logoutSessionIdIsInvalid(getActivity());
                        }
                    }
                } else {
                    clearUserInfo();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
            String strSessionId = SharedPrefUtils.getSessionId(getActivity());

            if (!"".equals(strSessionId)) {
                json.put("sessionId", strSessionId);
            }
            json.put("syn", false);
            json.put("deviceType", Constants.DEVICE_TYPE);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 清除数据，跳转到登录
     */
    private void clearUserInfo() {
//        SharedPrefUtils.saveBooleanData(getActivity(), Constants.SWITCH_GESTURE_FINGERPRINT, false);
//        SharedPrefUtils.saveBooleanData(getActivity(), Constants.IS_OPEN_FINGERPRINT, false);
//        SharedPrefUtils.saveStringData(getActivity(), Constants.FACE_TOKEN, "");
        SharedPrefUtils.saveStringData(getActivity(), "psdWord", null);
        SharedPrefUtils.saveSessionId(getActivity(), null);
        SharedPrefUtils.saveUserId(getActivity(), null);

        // 退出登录清空个人信息的facetoken
        SuneeeApplication.getInstance().removeALLActivity_();
        Intent intent = new Intent(getActivity(), LoginActivity2.class);
        startActivity(intent);
        getActivity().finish();
    }

}
