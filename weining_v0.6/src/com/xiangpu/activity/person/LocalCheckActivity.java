package com.xiangpu.activity.person;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.konecty.rocket.chat.R;
import com.xiangpu.action.GetCodeAction;
import com.xiangpu.action.GetCodeInterface;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.utils.RegexUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.views.VerifyCode;

/**
 * Created by fangfumin on 2017/8/31.
 */

public class LocalCheckActivity extends BaseActivity {

    private EditText etAccount;
    private EditText etVerifyCode;
    private VerifyCode verifyCode;

    private Button nextBtn;
    private ImageButton backBtn;

    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_check_layout);

        type = getIntent().getStringExtra("type");

        initView();
        initData();

    }

    private void initView() {

        etAccount = (EditText) findViewById(R.id.account);
        etVerifyCode = (EditText) findViewById(R.id.et_verifycode);
        verifyCode = (VerifyCode) findViewById(R.id.verifycode);
        nextBtn = (Button) findViewById(R.id.bt_next_step);
        backBtn = (ImageButton) findViewById(R.id.img_back_id);

    }

    private void initData() {
        if ("mobile".equals(type)) {
            etAccount.setHint(getString(R.string.input_telphone));
        } else if ("email".equals(type)) {
            etAccount.setHint(getString(R.string.input_email));
        }
        nextBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_id:
                this.finish();
                break;

            case R.id.bt_next_step:
                if (TextUtils.isEmpty(etAccount.getText() + "")) {
                    if ("mobile".equals(type)) {
                        ToastUtils.showCenterToast(this, getString(R.string.input_telphone));
                    } else if ("email".equals(type)) {
                        ToastUtils.showCenterToast(this, getString(R.string.input_email));
                    }
                    return;
                }
                if ("mobile".equals(type) && !RegexUtils.isMobile(etAccount.getText() + "")) {
                    ToastUtils.showCenterToast(this, getString(R.string.error_telphone));
                    return;
                } else if ("email".equals(type) && !RegexUtils.isEmail(etAccount.getText() + "")) {
                    ToastUtils.showCenterToast(this, getString(R.string.error_email));
                    return;
                }

                if (TextUtils.isEmpty(etVerifyCode.getText() + "")) {
                    ToastUtils.showCenterToast(this, getString(R.string.input_checkcode));
                    return;
                }
                if (!verifyCode.isEqualsIgnoreCase(etVerifyCode.getText() + "")) {
                    ToastUtils.showCenterToast(this, getString(R.string.error_checkcode));
                    return;
                }

                showProgressDialog();
                if ("mobile".equals(type)) {
                    GetCodeAction.getCodeByMobile(this, etAccount.getText() + "", new GetCodeInterface() {

                        @Override
                        public void success(String data) {
                            LocalCheckActivity.this.dismissProgressDialog();
                            Intent intent = new Intent(LocalCheckActivity.this, MessageOrEmailCheckCodeActivity.class);
                            intent.putExtra("type", type);
                            intent.putExtra("account", etAccount.getText() + "");
                            intent.putExtra("sessionId", data);
                            startActivity(intent);
                        }

                        @Override
                        public void failed(String error) {
                            LocalCheckActivity.this.dismissProgressDialog();
                            ToastUtils.showCenterToast(LocalCheckActivity.this, error);
                            return;
                        }
                    });
                } else if ("email".equals(type)) {
                    GetCodeAction.getCodeByEmail(this, etAccount.getText() + "", new GetCodeInterface() {

                        @Override
                        public void success(String data) {
                            LocalCheckActivity.this.dismissProgressDialog();
                            Intent intent = new Intent(LocalCheckActivity.this, MessageOrEmailCheckCodeActivity.class);
                            intent.putExtra("type", type);
                            intent.putExtra("account", etAccount.getText() + "");
                            intent.putExtra("sessionId", data);
                            startActivity(intent);
                        }

                        @Override
                        public void failed(String error) {
                            LocalCheckActivity.this.dismissProgressDialog();
                            ToastUtils.showCenterToast(LocalCheckActivity.this, error);
                            return;
                        }
                    });
                }
                break;

            default:
                super.onClick(v);
                break;
        }

    }
}
