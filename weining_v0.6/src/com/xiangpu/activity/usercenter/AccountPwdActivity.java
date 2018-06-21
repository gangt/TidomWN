package com.xiangpu.activity.usercenter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.MD5Encoder;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.WebServiceUtil;
import com.xiangpu.utils.WebServiceUtil.OnDataListener;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by fangfumin on 2017/5/13.
 * Info：
 */

public class AccountPwdActivity extends BaseActivity implements OnDataListener {
    private ImageView iv_title;
    private TextView tv_title;
    private EditText txt_edit_old_id;
    private EditText txt_edit_new1_id;
    private EditText txt_edit_new2_id;
    private Button bt_confirm_change;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_pwd);
        initView();
        loadPoint("", "修改密码");
    }

    private void initView() {
        iv_title = (ImageView) findViewById(R.id.iv_title);
        tv_title = (TextView) findViewById(R.id.tv_title);
        bt_confirm_change = (Button) findViewById(R.id.bt_confirm_change);
        txt_edit_old_id = (EditText) findViewById(R.id.txt_edit_old_id);
        txt_edit_new1_id = (EditText) findViewById(R.id.txt_edit_new1_id);
        txt_edit_new2_id = (EditText) findViewById(R.id.txt_edit_new2_id);

        tv_title.setText("修改密码");
        iv_title.setImageResource(R.drawable.person_center_back);
        iv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        bt_confirm_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword();
            }
        });
    }

    private void checkPassword() {
        String oldPwd = txt_edit_old_id.getText().toString().trim();
        String newPwd = txt_edit_new1_id.getText().toString().trim();
        String confirmNewPwd = txt_edit_new2_id.getText().toString().trim();
        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtils.showCenterToast(this, getString(R.string.old_passwor_no_null));
            return;
        }
        if (TextUtils.isEmpty(newPwd)) {
            ToastUtils.showCenterToast(this, getString(R.string.new_password_no_null));
            return;
        }
        if (TextUtils.isEmpty(confirmNewPwd)) {
            ToastUtils.showCenterToast(this, getString(R.string.new_password_mismatch));
            return;
        }
        if (!newPwd.equals(confirmNewPwd)) {
            ToastUtils.showCenterToast(this, getString(R.string.new_password_mismatch));
            return;
        }
        moduleUpdatePwd();
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        this.dismissProgressDialog();
        if (result == null) {
            ToastUtils.showCenterToast(this, getString(R.string.password_change_fail));
            return;
        }
        if (mode != null && mode.equals(Constants.moduleUpdatePwd)) {
            try {
                String status = result.getString("status");
                String errcodeMsg = result.getString("message");
                if ("1".equals(status)) {
                    SharedPrefUtils.saveStringData(this, "psdWord", txt_edit_new1_id.getText().toString());
                    ToastUtils.showCenterToast(this, "修改密码成功");
                    this.finish();
                } else {
                    ToastUtils.showCenterToast(this, errcodeMsg);
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

    private void moduleUpdatePwd() {
        this.showProgressDialog();
        WebServiceUtil.request(Constants.moduleUpdatePwd, "json", this);
    }

    @Override
    public String onGetParamDataString(String mode) {
        JSONObject json = new JSONObject();
        try {
            if (mode != null && mode.equals(Constants.moduleUpdatePwd)) {
                String strSessionId = SharedPrefUtils.getSessionId(this);
                if (!"".equals(strSessionId)) {
                    json.put("sessionId", strSessionId);
                }
                String oldPasswordEncoded = "";
                String newPasswordEncoded = "";
                try {
                    oldPasswordEncoded = MD5Encoder.encode(txt_edit_old_id.getText().toString().trim());
                    newPasswordEncoded = MD5Encoder.encode(txt_edit_new1_id.getText().toString().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                json.put("oldPassword", oldPasswordEncoded);
                json.put("newPassword", newPasswordEncoded);
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return json.toString();
    }

}
