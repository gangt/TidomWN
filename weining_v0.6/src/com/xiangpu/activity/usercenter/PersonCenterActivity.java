package com.xiangpu.activity.usercenter;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.activity.usercenter.leftmenu.LeftMenuAdapter;
import com.xiangpu.activity.usercenter.leftmenu.LeftMenuBean;
import com.xiangpu.bean.ErrorCode;
import com.xiangpu.bean.UserInfo;
import com.xiangpu.common.Constants;
import com.xiangpu.plugin.GetPhotoPlugin;
import com.xiangpu.plugin.ShowTitlePlugin;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.LoginUtils;
import com.xiangpu.utils.NoticeMessageUtils;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.WebServiceUtil;

import org.apache.cordova.CordovaPlugin;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/12/8 0008.
 * Info：
 */

public class PersonCenterActivity extends BaseActivity implements WebServiceUtil.OnDataListener {

    private DrawerLayout drawer_layout = null;
    private ImageView iv_title = null;
    private TextView tv_title = null;
    private RoundedImageView iv_user_image = null;
    private TextView tv_user_name = null;
    private ListView lv_menu = null;

    private FragmentManager fragmentManager = null;
    private String title = "";
    private List<LeftMenuBean> leftMenu = null;
    private LeftMenuAdapter menuAdapter = null;
    private PersonDataFragment personDataFragment = null;
    private AccountSafeFragment accountSafeFragment = null;
    private SettingsFragment settingsFragment = null;
    private HelpFeedbackFragment helpFeedbackFragment = null;
    private AboutFragment aboutFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_center);
        findView();
        initDatas();
        initNetBroadcastReceiver();
    }

    private void findView() {
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        iv_title = (ImageView) findViewById(R.id.iv_title);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_user_image = (RoundedImageView) findViewById(R.id.iv_user_image);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        lv_menu = (ListView) findViewById(R.id.lv_menu);
    }

    private void initDatas() {
        fragmentManager = getSupportFragmentManager();
        showTitleFragment(getString(R.string.person_app_set1));
        initLeftMenu();

        loadPoint("", "个人中心");
        drawer_layout.openDrawer(Gravity.LEFT);
        iv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer_layout.openDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    protected void netBrResult(boolean netResult) {
        super.netBrResult(netResult);
        if (netResult) {
            String strSessionId = SharedPrefUtils.getSessionId(this);
            if (TextUtils.isEmpty(strSessionId)) {
                ToastUtils.showCenterToast(this, getString(R.string.get_user_info_fail));
                LoginUtils.logoutSessionIdIsInvalid(this);
                return;
            }
            WebServiceUtil.request(Constants.moduleDetails, "json", this);
            Fragment fragment = fragmentManager.findFragmentByTag(title);
            if (fragment != null && fragment == personDataFragment) {
                ((PersonDataFragment) fragment).loadConfig();
            }
        }
    }

    private void initLeftMenu() {
        leftMenu = new ArrayList<>();

        int[] menu_icon_id = new int[]{
                R.drawable.person_data,
                R.drawable.person_account_safe,
                R.drawable.person_settings,
                R.drawable.person_help,
                R.drawable.person_about};

        String[] arrays_menu = new String[]{
                getString(R.string.person_app_set1),
                getString(R.string.person_app_set2),
                getString(R.string.person_app_set4),
                getString(R.string.person_app_set5),
                getString(R.string.person_app_set6),
        };
        for (int i = 0; i < menu_icon_id.length; i++) {
            LeftMenuBean menu = new LeftMenuBean(menu_icon_id[i], arrays_menu[i]);
            leftMenu.add(menu);
        }
        menuAdapter = new LeftMenuAdapter(this, leftMenu);
        lv_menu.setAdapter(menuAdapter);
        lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuAdapter.setItemSelect(position);
                menuAdapter.notifyDataSetChanged();
                showTitleFragment(leftMenu.get(position).getName());
                drawer_layout.closeDrawer(Gravity.LEFT);
            }
        });
    }

    public void showTitleFragment(String titleName) {
        LogUtil.d("titleName: " + titleName);
        boolean customBack = false;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (titleName.equals(getString(R.string.person_app_set1))) {
            // 个人资料
            if (personDataFragment == null) {
                personDataFragment = PersonDataFragment.newInstance();
            }
            transaction.replace(R.id.ll_fragment, personDataFragment, titleName).commit();
        } else if (titleName.equals(getString(R.string.person_app_set2))) {
            // 账户安全
            if (accountSafeFragment == null) {
                accountSafeFragment = AccountSafeFragment.newInstance();
            }
            transaction.replace(R.id.ll_fragment, accountSafeFragment, titleName).commit();
        } else if (titleName.equals(getString(R.string.person_app_set4))) {
            // 设置
            if (settingsFragment == null) {
                settingsFragment = SettingsFragment.newInstance();
            }
            transaction.replace(R.id.ll_fragment, settingsFragment, titleName).commit();
        } else if (titleName.equals(getString(R.string.person_app_set5))) {
            // 帮助和反馈
            if (helpFeedbackFragment == null) {
                helpFeedbackFragment = HelpFeedbackFragment.newInstance();
            }
            customBack = true;
            transaction.replace(R.id.ll_fragment, helpFeedbackFragment, titleName).commit();
        } else if (titleName.equals(getString(R.string.person_app_set6))) {
            // 帮助和反馈
            if (aboutFragment == null) {
                aboutFragment = AboutFragment.newInstance();
            }
            transaction.replace(R.id.ll_fragment, aboutFragment, titleName).commit();
        }
        tv_title.setText(titleName);
        title = titleName;
        setCustomBack(customBack);
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        if (result == null) {
            ToastUtils.showCenterToast(this, "请求接口超时");
            return;
        }

        try {
            if (mode != null && mode.equals(Constants.moduleDetails)) {
                if (result.getInt("status") != 1) {
                    String code = result.getString("code");
                    String errorMsgByCode = NoticeMessageUtils.getErrorMsgByCode(this, code);
                    if (TextUtils.isEmpty(errorMsgByCode)) {
                        ToastUtils.showCenterToast(this, result.getString("message"));
                    } else {
                        ToastUtils.showCenterToast(this, errorMsgByCode);
                        if (ErrorCode.CONNECTION_FAIL.equals(code)) {
                            //sessionId失效，退出登录
                            LoginUtils.logoutSessionIdIsInvalid(this);
                        }
                    }
                    return;
                }
                JSONObject User = result.getJSONObject("data");
                String name = User.getString("name");
                String userName = User.getString("userName");
                String nick = User.getString("nick");
                String sex = User.getString("sex");
                String photo = User.getString("photo");
                String signature = User.getString("signature");
                String backgroundImg = User.getString("backgroundImg");
                String mobile = User.getString("mobile");
                String address = User.getString("address");
                String aliasName = User.getString("aliasName");

                UserInfo userInfo = new UserInfo();
                userInfo.setName(name);
                userInfo.setUserName(userName);
                userInfo.setNick(nick);
                userInfo.setSex(sex);
                userInfo.setPhoto(photo);
                userInfo.setSignature(signature);
                userInfo.setBackgroundImg(backgroundImg);
                userInfo.setMobile(mobile);
                userInfo.setAddress(address);
                userInfo.setAliasName(aliasName);

                SuneeeApplication.setUser(userInfo);

                userInfo = SuneeeApplication.user;
                LogUtil.d("个人中心中后台返回的头像地址：" + photo);
                userInfo.headImg = Constants.BASE_HEADIMG_URL + photo;

                SuneeeApplication.user.headName = photo;
                SuneeeApplication.user.headImg = Constants.BASE_HEADIMG_URL + photo;
                ImageLoader.getInstance().displayImage(Constants.BASE_HEADIMG_URL + photo, iv_user_image);
                if (!StringUtils.isEmpty(aliasName)) {
                    tv_user_name.setText(aliasName);
                } else {
                    tv_user_name.setText(name);
                }
            } else if (mode != null && mode.equals(Constants.moduleUpdatePwd)) {
                if (result.getInt("status") != 1) {
                    ToastUtils.showCenterToast(this, result.getString("message"));
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
            String type = SharedPrefUtils.getStringData(this, Constants.WEI_LIAN_TYPE, "");
            String strSessionId = SharedPrefUtils.getSessionId(this);
            if (!"".equals(strSessionId)) {
                json.put("sessionId", strSessionId);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return json.toString();
    }

    public void refreshPersonHead(String imagePath) {
        if (iv_user_image != null) {
            iv_user_image.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    protected void isShowErrorNotice(boolean flag) {
        super.isShowErrorNotice(flag);
    }

    @Override
    protected void customPopupViewBack() {
        super.customPopupViewBack();
        if (helpFeedbackFragment != null) {
            helpFeedbackFragment.goBack();
        }
    }

    public static CordovaPlugin activityResultCallback;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("requestCode: " + requestCode);
        CordovaPlugin callback = this.activityResultCallback;
        if (requestCode == GetPhotoPlugin.TAKE_BIG_PICTURE || requestCode == GetPhotoPlugin.CHOOSE_BIG_PICTURE || requestCode == GetPhotoPlugin.PICTURE_CUSTOM_CAIJIE) {
            // 拍照或是从相册中选取返回
            if (callback != null) {
                callback.onActivityResult(requestCode, resultCode, data);
            }
        } else if (requestCode == ShowTitlePlugin.PERSON_DATA_SECOND) {
            // 昵称、个性签名和地区二级菜单返回
            if (personDataFragment != null) {
                personDataFragment.loadConfig();
            }
        }
    }

}
