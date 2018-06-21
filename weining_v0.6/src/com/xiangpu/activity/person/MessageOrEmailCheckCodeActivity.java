package com.xiangpu.activity.person;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.MD5Encoder;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.Utils;
import com.xiangpu.utils.WebServiceUtil;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Jason Huang on 2017/8/31.
 */

public class MessageOrEmailCheckCodeActivity extends BaseActivity implements WebServiceUtil.OnDataListener {

    private static final String TAG = MessageOrEmailCheckCodeActivity.class.getSimpleName();
    private TextView tvAccount;
    private EditText et_checkcode;
    private EditText txt_edit_old_id;
    private EditText txt_edit_new1_id;
    private String account;
    private String accountType;
    private String sessionId;
    private String checkCode;
    private String newPwd;

    private Button btnSendCheckcode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_or_email_checkcode_layout);

        initView();
        initData();
    }

    private void initView() {
        tvAccount = (TextView) findViewById(R.id.tv_account);

        et_checkcode = (EditText) findViewById(R.id.et_checkcode);
        btnSendCheckcode = (Button) findViewById(R.id.bt_send_checkcode);

        txt_edit_old_id = (EditText) findViewById(R.id.txt_edit_old_id);
        txt_edit_new1_id = (EditText) findViewById(R.id.txt_edit_new1_id);

        Button bt_comfirm = (Button) findViewById(R.id.bt_comfirm);
        ImageButton iv_back = (ImageButton) findViewById(R.id.iv_back);
        btnSendCheckcode.setOnClickListener(this);
        bt_comfirm.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }


    private void initData() {
        accountType = getIntent().getStringExtra("type");
        account = getIntent().getStringExtra("account");
        sessionId = getIntent().getStringExtra("sessionId");

        tvAccount.setText(account);

        btnSendCheckcode.setEnabled(false);
        timer.start();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                this.finish();
                break;

            case R.id.bt_comfirm:
                checkInput();
                break;

            case R.id.bt_send_checkcode:
                btnSendCheckcode.setEnabled(false);
                timer.start();
                sendCheckCode();
                break;

            default:
                super.onClick(v);
                break;

        }
    }

    private void checkInput() {
        checkCode = et_checkcode.getText().toString().trim();
        String oldPwd = txt_edit_old_id.getText().toString().trim();
        newPwd = txt_edit_new1_id.getText().toString().trim();
        if (TextUtils.isEmpty(checkCode)) {
            ToastUtils.showCenterToast(this, getString(R.string.checkcode_no_null));
            return;
        }
        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtils.showCenterToast(this, getString(R.string.new_psd_no_null));
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            ToastUtils.showCenterToast(this, getString(R.string.sure_psd_no_null));
            return;
        }
        if (!oldPwd.equals(newPwd)) {
            ToastUtils.showCenterToast(this, getString(R.string.password_mismatch));
            return;
        }
        showProgressDialog();
        WebServiceUtil.request(Constants.moduleSetNewPassword, Constants.REQUEST_TYPE_JSON, this);
    }

    private CountDownTimer timer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            btnSendCheckcode.setText("重发验证码(" + (millisUntilFinished / 1000) + "s)");
        }

        @Override
        public void onFinish() {
            btnSendCheckcode.setEnabled(true);
            btnSendCheckcode.setText("发送验证码");
        }
    };


    private void sendCheckCode() {
        if ("mobile".equals(accountType)) {
            WebServiceUtil.request(Constants.moduleGetMobileCheckCode, Constants.REQUEST_TYPE_JSON, this);
        } else if ("email".equals(accountType)) {
            WebServiceUtil.request(Constants.moduleGetEmailCheckCode, Constants.REQUEST_TYPE_JSON, this);
        }
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        dismissProgressDialog();
        if (result == null) {
            if (mode != null && mode.equals(Constants.moduleSetNewPassword)) {
                ToastUtils.showCenterToast(this, getString(R.string.failed_change_password));
            } else {
                ToastUtils.showCenterToast(this, getString(R.string.failed_get_telphone_checkcode));
            }
            return;
        }
        if (mode != null && (mode.equals(Constants.moduleGetMobileCheckCode)
                || mode.equals(Constants.moduleGetEmailCheckCode))) {
            try {
                int status = result.getInt("status");
                if (status == 1) {
                    sessionId = result.getString("data");
                } else {
                    ToastUtils.showCenterToast(this, result.getString("msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (mode != null && mode.equals(Constants.moduleSetNewPassword)) {
            try {
                int status = result.getInt("status");
                if (status == 1) {
                    ToastUtils.showCenterToast(this, getString(R.string.success_change_password));
                    Utils.startActivity(this, LoginActivity2.class);
                    this.finish();
                } else {
                    ToastUtils.showCenterToast(this, result.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<NameValuePair> onGetParamData(String mode) {
        return null;
    }

    @Override
    public String onGetParamDataString(String mode) {
        JSONObject json = new JSONObject();
        if (mode != null && (mode.equals(Constants.moduleGetMobileCheckCode)
                || mode.equals(Constants.moduleGetEmailCheckCode))) {
            try {
                if ("mobile".equals(accountType)) {
                    json.put("mobile", account);
                } else if ("email".equals(accountType)) {
                    json.put("email", account);
                }
                json.put("enterpriseCode", getString(R.string.api_compcode));
                json.put("clientIp", "127.0.0.1");
                json.put("appCode", getString(R.string.api_appcode));
                json.put("serverIp", "127.0.0.1");
                json.put("encryptCode", "1234567899876543");
                json.put("prefix", context.getString(R.string.app_prefix));
                return json.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (mode != null && mode.equals(Constants.moduleSetNewPassword)) {
            try {

                json.put("sessionId", sessionId);
                json.put(accountType, account);
                json.put("code", checkCode);
                String strPwd = "";
                try {
                    strPwd = MD5Encoder.encode(newPwd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                json.put("newPassword", strPwd);

                LogUtil.i(TAG, "params:" + json.toString());
                return json.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}