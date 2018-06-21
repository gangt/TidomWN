package com.xiangpu.activity.person;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.konecty.rocket.chat.AuthenticationActivity;
import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.bean.AuthenticationBean;
import com.xiangpu.common.Constants;
import com.xiangpu.interfaces.LoginInterface;
import com.xiangpu.utils.HomeWatcherReceiver;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.NetWorkUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.SkipUtils;
import com.xiangpu.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import chat.rocket.android.InitializeUtils;
import chat.rocket.android.RocketChatCache;
import chat.rocket.android.service.ServerConnectivity;

/**
 * Created by andi on 17/2/23.
 */
public class LoginActivity2 extends BaseActivity implements View.OnClickListener {

    public static final String TAG = LoginActivity2.class.getSimpleName();

    private EditText mMobileNum;
    private EditText mPassword;
    private Button mNextStep;

    private ImageButton btnBack;
    private TextView tv_register;
    private TextView tvForgetPsd;

    private String mPhoneNumber;

    private ImageView logoImg;
    private CheckBox rememberPsd;

//    private boolean bNeedAlarm = true;//检查app 是不是被劫持

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        SuneeeApplication.weilianType = SharedPrefUtils.getStringData(this, Constants.WEI_LIAN_TYPE, Constants.WEI_LIAN_HAI);
        initViews();
        bindEvent();
        initNetBroadcastReceiver();
        RocketChatCache.INSTANCE.setConnectStus(ServerConnectivity.STATE_DISCONNECTED+"");
        chat.rocket.android.service.ChatConnectivityManager.getInstance(this).keepAliveServer();
        String hostname = RocketChatCache.INSTANCE.getSelectedServerHostname();
        if (hostname == null) {
            InitializeUtils.getInstance().serviceConnection(this);
        }
    }

    private void initViews() {
        mMobileNum = (EditText) findViewById(R.id.register_account);
        mPassword = (EditText) findViewById(R.id.et_login_password_id);
        mNextStep = (Button) findViewById(R.id.bt_login_next);
        mNextStep.setOnClickListener(this);

        logoImg = (ImageView) findViewById(R.id.iv_logo);
        btnBack = (ImageButton) findViewById(R.id.img_back_id);
        btnBack.setOnClickListener(this);

        tv_register = (TextView) findViewById(R.id.tv_register);
        tv_register.setOnClickListener(this);
        tvForgetPsd = (TextView) findViewById(R.id.forget_psd);
        tvForgetPsd.setOnClickListener(this);
        String userName = SharedPrefUtils.getStringData(this, "userName", "");
        mMobileNum.setText(userName);

//        rememberPsd = (CheckBox) findViewById(R.id.remember_psd);
//        rememberPsd.setChecked(SharedPrefUtils.getBooleanData(this, "remember_password", true));
//        rememberPsd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                SharedPrefUtils.saveBooleanData(LoginActivity2.this, "remember_password", isChecked);
//            }
//        });
//        if (SharedPrefUtils.getBooleanData(this, "remember_password", true)) {
//            mPassword.setText(SharedPrefUtils.getStringData(this, "psdWord", ""));
//        }

        final AuthenticationBean bean = DataSupport.where("orgName = ? and version = ? and env = ? ", getString(R.string.api_compcode), getString(R.string.api_version), getString(R.string.api_env)).findFirst(AuthenticationBean.class);
        if (bean != null) {
            ImageLoader.getInstance().displayImage(Constants.getConfigLogoPath(bean.getLoginLogoDir()), logoImg);
        }
    }

    private void bindEvent() {

        mMobileNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    mNextStep.setClickable(false);
                    mNextStep.setBackgroundResource(R.drawable.login_button_unclick_bg);
                } else {
                    mNextStep.setClickable(true);
                    mNextStep.setBackgroundResource(R.drawable.login_button_bg);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });

        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(mMobileNum.getText().toString())
                        && !TextUtils.isEmpty(mPassword.getText().toString())) {
                    // mNextStep.setEnabled(true);
                } else {
                    // mNextStep.setEnabled(false);
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back_id:
//                bNeedAlarm = false;
                Intent intent = new Intent(this, AuthenticationActivity.class);
                startActivity(intent);
                LoginActivity2.this.finish();
                break;

            case R.id.bt_login_next:
                mPhoneNumber = mMobileNum.getText().toString().trim();
                mPhoneNumber = mPhoneNumber.replace(" ", "");

                final String mobileCode = mPassword.getText().toString();
                if (TextUtils.isEmpty(mobileCode)) {
                    ToastUtils.showCenterToast(this, getString(R.string.password_no_null));
                    return;
                }
                if (!NetWorkUtils.isNetworkConnected(this)) {
                    ToastUtils.showCenterToast(this, getString(R.string.no_network));
                    return;
                }
                showProgressDialog();
                login(mobileCode);
//                String strSessionId = SharedPrefUtils.getSessionId(this);
//                boolean logoutFirst = false;
//                if (!logoutFirst) {
//                } else {
//                    LoginUtils.logout(this, strSessionId, false, Constants.DEVICE_TYPE, new LogoutInterface() {
//
//                        @Override
//                        public void logoutSuccess() {
//                            login(mobileCode);
//                        }
//
//                        @Override
//                        public void logutFailed(String errorMsg) {
//                            dismissProgressDialog();
//                            ToastUtils.showCenterToast(LoginActivity2.this, errorMsg);
//                        }
//                    });
//                }
                break;

            case R.id.tv_register:
//                bNeedAlarm = false;
                if (!NetWorkUtils.isNetworkConnected(this)) {
                    ToastUtils.showCenterToast(this, getString(R.string.no_network));
                    return;
                }
                String enterpriseCode = getString(R.string.api_compcode);
                String appCode = getString(R.string.api_appcode);
                String prefix = getString(R.string.app_prefix);
                String registerUrl = Constants.moduleRegister + "?enterpriseCode=" + enterpriseCode
                        + "&appCode=" + appCode
                        + "&prefix=" + prefix;
                goWebMainActivity(registerUrl, "注册", "");
                break;

            case R.id.iv_logo:
                break;

            case R.id.forget_psd:
//                bNeedAlarm = false;
                startActivity(new Intent(this, ForgetPsdMethodActivity.class));
                break;

            default:
                break;
        }
    }

    private void login(String mobileCode) {
        LoginUtils.getLoginInfo(this, Constants.WEI_LIAN_HAI, mPhoneNumber, mobileCode, new LoginInterface() {
            @Override
            public void loginSuccess(String type) {
                dismissProgressDialog();
//                bNeedAlarm = false;
                SkipUtils.getInstance().skipToByUserPower(LoginActivity2.this, type);
            }

            @Override
            public void loginFailed(String errorMsg) {
                dismissProgressDialog();
                ToastUtils.showCenterToast(LoginActivity2.this, errorMsg);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

//            bNeedAlarm = false;

            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
            LoginActivity2.this.finish();

            return true;
        }

        if ((keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) && event.getRepeatCount() == 0) {
//            bNeedAlarm = false;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerHomeKeyReceiver(this);
    }

    private HomeWatcherReceiver mHomeKeyReceiver = null;

    private void registerHomeKeyReceiver(Context context) {

        mHomeKeyReceiver = new HomeWatcherReceiver(new HomeWatcherReceiver.IKeyEvent() {
            @Override
            public void onKeyEvent(boolean bValue) {
                if (bValue) {
//                    bNeedAlarm = false;
                }
            }
        });

        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    private void unregisterHomeKeyReceiver(Context context) {

        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }

}
