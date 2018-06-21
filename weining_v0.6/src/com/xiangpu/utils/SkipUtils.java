package com.xiangpu.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.konecty.rocket.chat.AuthenticationActivity;
import com.konecty.rocket.chat.R;
import com.lssl.activity.MainActivity;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.activity.person.ServerMainLoginActivity;
import com.xiangpu.bean.UserCompBean;
import com.xiangpu.bean.UserInfoBean;
import com.xiangpu.common.Constants;

import java.util.List;


/**
 * 根据用户判断处理跳转的工具栏
 * Created by huangda on 2017/8/24.
 */

public class SkipUtils {

    private static final String TAG = SkipUtils.class.getSimpleName();

    private static SkipUtils skipUtils = null;
    private boolean isAutoLoad = false; // 是否自动登录，false不是自动登录，true自动登录

    public static SkipUtils getInstance() {
        synchronized (SkipUtils.class) {
            if (skipUtils == null) {
                skipUtils = new SkipUtils();
            }
            return skipUtils;
        }
    }

    /**
     * 根据用户点击的入口
     *
     * @param context
     * @param entrance
     */
    public void skipByEntranceAndUserType(Context context, String entrance) {
        UserCompBean userCompBean = SuneeeApplication.getUserCompBean();
        String compId = userCompBean.getCompId();
        String userType = userCompBean.getUserType();
        LogUtil.i(TAG, "compId:" + compId + ",userType:" + userType);
        switch (entrance) {
            case "weilian_hai":
                SuneeeApplication.getUser().setSelectCompanyId("suneee_weilianhai");
                if ("BUSER".equals(userType)) {
                    Utils.startActivity(context, ServerMainLoginActivity.class);
                } else {
                    SharedPrefUtils.saveSessionId(context, null);
                    ToastUtils.showCenterToast(context, "无权限访问");
                }
                break;

            case "weilian_wa":
                SuneeeApplication.getUser().setSelectCompanyId(Constants.WEI_LIAN_WA);
                Utils.startActivity(context, MainActivity.class);
                break;
            default:
                break;
        }
    }

    public void autoLoadToMainActivity(boolean autoLoad) {
        this.isAutoLoad = autoLoad;
    }

    /**
     * 根据用户上一次选择的入口处理跳转逻辑
     *
     * @param context
     * @param entrance
     */
    public void skipByEntranceForAuto(Context context, String entrance) {
        UserCompBean userCompBean = SuneeeApplication.getUserCompBean();
        String compId = userCompBean.getCompId();
        String userType = userCompBean.getUserType();
        LogUtil.i(TAG, "compId:" + compId + ",userType:" + userType);
        SharedPrefUtils.saveStringData(context, Constants.WEI_LIAN_TYPE, entrance);
        List<UserInfoBean.DataBean.SpatialBean> spatial = SuneeeApplication.getSpatial();
        switch (entrance) {
            case Constants.WEI_LIAN_HAI:
                if (PowerUtils.checkLoginPower(Constants.WEI_LIAN_HAI, spatial)) {
                    if ("BUSER".equals(userType)) {
                        Utils.startActivity(context, ServerMainLoginActivity.class);
                        return;
                    }
                    String selectCompanyId = SharedPrefUtils.getStringData(context, Constants.SELECT_COMPANY_ID, "");
                    if (TextUtils.isEmpty(selectCompanyId)) {
                        Utils.startActivity(context, ServerMainLoginActivity.class);
                    } else {
                        getHomepageConfigInfo(context, selectCompanyId);
                    }
                } else {
                    goToAuthenticationActivity(context);
                }
                break;
            case Constants.WEI_LIAN_BAO:
                if (PowerUtils.checkLoginPower(Constants.WEI_LIAN_BAO, spatial)) {
                    SuneeeApplication.getUser().setSelectCompanyId(context.getString(R.string.api_compcode));
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                } else {
                    goToAuthenticationActivity(context);
                }
                break;
            case Constants.WEI_LIAN_WA:
                if (PowerUtils.checkLoginPower(Constants.WEI_LIAN_WA, spatial)) {
                    SuneeeApplication.getUser().setSelectCompanyId(context.getString(R.string.api_compcode));
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                } else {
                    goToAuthenticationActivity(context);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 如果没权限就跳到鉴权页
     *
     * @param context
     */
    private void goToAuthenticationActivity(Context context) {
        Utils.startActivity(context, AuthenticationActivity.class);
        SharedPrefUtils.saveSessionId(context, null);
        ToastUtils.showCenterToast(context, context.getString(R.string.no_limit_visit));
    }

    /**
     * 获取主页配置信息
     */
    private void getHomepageConfigInfo(Context context, String companyId) {
        SuneeeApplication.getUser().setSelectCompanyId(companyId);
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.INTENT_ACTIVITY, isAutoLoad);
        context.startActivity(intent);
    }

    public void skipToByUserPower(Activity context, String type) {
        if (Constants.WEI_LIAN_HAI.equals(type)) {
            if (PowerUtils.checkLoginPower(Constants.WEI_LIAN_HAI, SuneeeApplication.getSpatial())) {
                //登录成功并有权限进去时保存入口类型
                SharedPrefUtils.saveStringData(context, Constants.WEI_LIAN_TYPE, type);
                context.startActivity(new Intent(context, ServerMainLoginActivity.class));
                context.finish();
            } else {
                SharedPrefUtils.saveSessionId(context, null);
                ToastUtils.showCenterToast(context, "无权限访问");
            }
        } else if (Constants.WEI_LIAN_BAO.equals(type)) {
            if (PowerUtils.checkLoginPower(Constants.WEI_LIAN_BAO, SuneeeApplication.getSpatial())) {
                SuneeeApplication.getUser().setSelectCompanyId(context.getString(R.string.api_compcode));
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else {
                SharedPrefUtils.saveSessionId(context, null);
                ToastUtils.showCenterToast(context, "无权限访问");
            }
        } else if (Constants.WEI_LIAN_WA.equals(type)) {
            if (PowerUtils.checkLoginPower(Constants.WEI_LIAN_WA, SuneeeApplication.getSpatial())) {
                SuneeeApplication.getUser().setSelectCompanyId(context.getString(R.string.api_compcode));
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
            } else {
                SharedPrefUtils.saveSessionId(context, null);
                ToastUtils.showCenterToast(context, "无权限访问");
            }
        }
    }

}
