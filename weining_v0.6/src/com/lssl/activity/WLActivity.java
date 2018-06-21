package com.lssl.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.konecty.rocket.chat.AuthenticationActivity;
import com.konecty.rocket.chat.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiangpu.activity.UcpWebViewActivity;
import com.xiangpu.activity.WebMainActivity;
import com.xiangpu.activity.person.ServerMainLoginActivity;
import com.xiangpu.activity.usercenter.MessageActivity;
import com.xiangpu.activity.usercenter.PersonCenterActivity;
import com.xiangpu.adapter.SuspendMenuAdapter;
import com.xiangpu.bean.MenuBean;
import com.xiangpu.bean.PointUserInfoBean;
import com.xiangpu.bean.UserCompanyBean;
import com.xiangpu.bean.UserInfoBean;
import com.xiangpu.common.Constants;
import com.xiangpu.plugin.TimeSpacePlugin;
import com.xiangpu.utils.DensityUtil;
import com.xiangpu.utils.FileUtils;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.NetWorkUtils;
import com.xiangpu.utils.ScreenUtil;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.SmallDotMenuUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.Utils;
import com.xiangpu.views.WebBackImageView;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.os.Environment;
import android.provider.MediaStore;
import com.xiangpu.utils.FileUtils;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.BitmapUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.ContentValues;

import android.net.Uri;
import android.widget.Toast;
import com.xiangpu.common.Constants;
import org.apache.cordova.LOG;
/**
 * 微链界面基类
 *
 * @author Yafei.Chen
 */
public abstract class WLActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = WLActivity.class.getSimpleName();
    private ArrayList<MenuBean.MenuItem> menuItems = null;
    PopupWindow popupWindow;

    private View view;
    private Button loadingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (isApplyKitKatTranslucency()) {
//            setTranslucentStatus(true);
//        }
//        if (isApplyColorPrimary()) {
//            setSystemBarTintDrawable(getResources().getDrawable(R.color.basic_color_primary));
//        }
    }

//    protected boolean isApplyKitKatTranslucency() {
//        return true;
//    }
//
//    protected boolean isApplyColorPrimary() {
//        return true;
//    }
//
//    private void setSystemBarTintDrawable(Drawable tintDrawable) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            SystemBarUtil mTintManager = new SystemBarUtil(this);
//            if (tintDrawable != null) {
//                mTintManager.setStatusBarTintEnabled(true);
//                mTintManager.setTintDrawable(tintDrawable);
//            } else {
//                mTintManager.setStatusBarTintEnabled(false);
//                mTintManager.setTintDrawable(null);
//            }
//        }
//    }
//
//    protected void setTranslucentStatus(boolean on) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window win = getWindow();
//            WindowManager.LayoutParams winParams = win.getAttributes();
//            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//            if (on) {
//                winParams.flags |= bits;
//            } else {
//                winParams.flags &= ~bits;
//            }
//            win.setAttributes(winParams);
//        }
//    }

    /**
     * 检查网络状况
     */
    protected boolean isNetworkOk() {
        if (NetWorkUtils.isNetworkConnected(this)) {
            removeErrorNetworkView();
            return true;
        } else {
            showErrorNetworkView();
            return false;
        }
    }

    /**
     * 是否显示网络错误提示
     *
     * @param isNetworkOk 网络是否良好
     */
    protected void isShowErrorNotice(boolean isNetworkOk) {
        if (isNetworkOk) {
            removeErrorNetworkView();
        } else {
            showErrorNetworkView();
        }
    }

    /**
     * 添加无网络视图
     */
    private void showErrorNetworkView() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                DensityUtil.dip2px(this, 53));
        view = getLayoutInflater().inflate(R.layout.layout_netbar, null);
        loadingBtn = (Button) view.findViewById(R.id.loading_again);
        loadingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadingBtnClick();
            }
        });
        addContentView(view, params);
    }

    /**
     * 删除无网络视图
     */
    private void removeErrorNetworkView() {
        if (view == null) {
            return;
        } else {
            ((ViewGroup) view.getParent()).removeView(view);
            view = null;
        }
    }

    /**
     * 重新加载
     */
    private void onLoadingBtnClick() {
    }

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressLint("RestrictedApi")
    private void handleResult(Fragment frag, int requestCode, int resultCode, Intent data) {
        frag.onActivityResult(requestCode & 0xffff, resultCode, data);
        List<Fragment> frags = frag.getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data);
            }
        }
    }

    /**
     * add by yafei.chen（待替换成EventBus）
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            handleNotifyMessage(msg);
        }

        ;
    };

    public Handler getBaseHandle() {
        return handler;
    }

    protected void handleNotifyMessage(Message msg) {

    }

    WebBackImageView back;

    /**
     * 添加悬浮小控件
     *
     * @param
     */
    public void loadPoint(final String loadkey, final String title) {
        final FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(
                DensityUtil.dip2px(this, 60),
                DensityUtil.dip2px(this, 60));
        backParams.bottomMargin = DensityUtil.dip2px(this, 60);
        backParams.rightMargin = DensityUtil.dip2px(this, 10);

        backParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        back = new WebBackImageView(this);
        back.setBackgroundResource(R.drawable.point);

        back.setClickable(true);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmallDotMenu(back, loadkey, title);
                back.setVisibility(View.INVISIBLE);
            }
        });
        addContentView(back, backParams);
    }

    private PointUserInfoBean pointUserInfoBean = new PointUserInfoBean();
    private LinearLayout companyLayout; // 小圆点企业切换
    private RoundedImageView imageView; // 用户头像
    private TextView companyNameTv; // 企业名称
    private TextView userNameTv; // 用户名称

    /**
     * 显示小圆点菜单
     *
     * @param viewPranet
     * @param loadkey
     * @param title
     */
    private void showSmallDotMenu(View viewPranet, String loadkey, String title) {
        View popupView = this.getLayoutInflater().inflate(R.layout.popupwindow_main_page_menu_layout, null);
        GridView gv_suspend_menu = (GridView) popupView.findViewById(R.id.gv_suspend_menu);
        gv_suspend_menu.setSelector(new ColorDrawable(Color.TRANSPARENT));// 设置点击背景
//        ImageView homeImg = (ImageView) popupView.findViewById(R.id.image_home);
//        TextView homeTv = (TextView) popupView.findViewById(R.id.text_home);
//        ImageView sevenStarImg = (ImageView) popupView.findViewById(R.id.image_seven_star);
//        TextView sevenStarTv = (TextView) popupView.findViewById(R.id.text_seven_star);

        companyLayout = (LinearLayout) popupView.findViewById(R.id.company_layout);
        companyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCompanyAction();
            }
        });
        imageView = (RoundedImageView) companyLayout.findViewById(R.id.avatar);
        companyNameTv = (TextView) companyLayout.findViewById(R.id.company_name);
        userNameTv = (TextView) companyLayout.findViewById(R.id.user_name);

        setCompanyInfo(pointUserInfoBean);

//        //如果是微链海，      第一排显示后退，主页，首页，布局默认是微链海的情况
//        //如果是微链宝/微链娃，第一排显示后退，我的，登录
//        if (!Constants.WEI_LIAN_HAI.equals(SuneeeApplication.weilianType)) {
//            homeImg.setImageResource(R.drawable.menu_item_my);
//            homeTv.setText(getString(R.string.mine));
//            sevenStarImg.setImageResource(R.drawable.menu_item_logout);
//            sevenStarTv.setText(getString(R.string.login));
//        }
        LinearLayout llHomePage = (LinearLayout) popupView.findViewById(R.id.ll_home_page);
        llHomePage.setOnClickListener(this);
        popupView.findViewById(R.id.ll_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startActivity(WLActivity.this, PersonCenterActivity.class);
                popupWindow.dismiss();
            }
        });
//        LinearLayout imgServerIcon = (LinearLayout) popupView.findViewById(R.id.img_server_icon);
//        imgServerIcon.setOnClickListener(this);

        if (Constants.WEI_LIAN_HAI.equals(SuneeeApplication.weilianType)) {
            menuItems = SmallDotMenuUtils.getMenuItems(this, loadkey);
        } else {
            menuItems = new ArrayList<>();
        }
        //默认没有权限
        boolean showWeiLianHai = false;
        boolean showWeiLianBao = false;
        boolean showWeiLianWa = false;
        List<UserInfoBean.DataBean.SpatialBean> spatial = SuneeeApplication.getSpatial();
        for (UserInfoBean.DataBean.SpatialBean spatialBean : spatial) {
            switch (spatialBean.getSpaceCode()) {
                case Constants.HAI:
                    showWeiLianHai = spatialBean.getHaspower() == 1;
                    break;
                case Constants.BAO:
                    showWeiLianBao = spatialBean.getHaspower() == 1;
                    break;
                case Constants.WA:
                    showWeiLianWa = spatialBean.getHaspower() == 1;
                    break;
                default:
                    break;
            }
        }
        //继续拼接微链海，宝，娃的数据
        if (showWeiLianHai) {
        }
        if (showWeiLianBao) {
        }
        if (showWeiLianWa) {
        }

        if (Constants.WEI_LIAN_HAI.equals(SuneeeApplication.weilianType)) {
            MenuBean.MenuItem menuItem = new MenuBean().new MenuItem();
            menuItem.setMenuItemName("客满");
            menuItem.setMenuItemIconName("service");
            menuItem.setMenuItemSub("action", "service");
            menuItems.add(menuItem);
        }

        gv_suspend_menu.setAdapter(new SuspendMenuAdapter(this, menuItems));
        gv_suspend_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MenuBean.MenuItem menuitem = menuItems.get(position);

                String linkSub = menuitem.getMenuItemSub();
                switch (linkSub) {
                    case Constants.SEARCH:
                        String link = Constants.POINT_SEARCH_URL;
                        String title = "时空中心";

                        Intent intent = new Intent(WLActivity.this, WebMainActivity.class);
                        intent.putExtra("link", link);
                        intent.putExtra("title", title);
                        intent.putExtra("TagPlugin", TimeSpacePlugin.TAG);
                        WLActivity.this.startActivity(intent);
                        break;

                    case Constants.MY:
                        Utils.startActivity(WLActivity.this, PersonCenterActivity.class);
                        break;

                    case Constants.MESSAGE:
                        Utils.startActivity(WLActivity.this, MessageActivity.class);
                        break;

                    case Constants.LOGOUT:
                        SharedPrefUtils.saveStringData(WLActivity.this, "router", null);
                        goToAuthenticationActivity();
                        break;

                    case Constants.WEI_LIAN_HAI:
                        SuneeeApplication.weilianType = Constants.WEI_LIAN_HAI;
                        Utils.startActivity(WLActivity.this, ServerMainLoginActivity.class);
                        break;

                    case Constants.WEI_LIAN_BAO:
                        setMainPageDataAndPower(Constants.WEI_LIAN_BAO);
                        break;

                    case Constants.WEI_LIAN_WA:
                        setMainPageDataAndPower(Constants.WEI_LIAN_WA);
                        break;

                    case "service":
                        showServicePopupView();
                        break;

                    default:
                        break;
                }
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(popupView, DensityUtil.dip2px(this, 174),
                DensityUtil.dip2px(this, 204));
        // 设置动画
        popupWindow.setAnimationStyle(R.style.popup_window_anim);
        // 设置背景颜色
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // 设置可以获取焦点
        popupWindow.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                back.setVisibility(View.VISIBLE);
            }
        });
        // 更新popupwindow的状态
        popupWindow.update();
        // 以下拉的方式显示，并且可以设置显示的位置
        popupWindow.showAsDropDown(viewPranet, 0, 0);
    }

    PopupWindow servicePopupView;

    protected CordovaInterfaceImpl cordovaInterface = new CordovaInterfaceImpl(this) {
        @Override
        public Object onMessage(String id, Object data) {
            if ("onPageStarted".equals(id)) {

            }
            if ("onPageFinished".equals(id)) {

            }
            if ("onReceivedError".equals(id)) {

            }
            return super.onMessage(id, data);
        }
    };

    private void showServicePopupView() {
        View view = this.getLayoutInflater().inflate(R.layout.popupwindow_service_layout, null);

        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);
        SystemWebView serviceCordovaWebView = (SystemWebView) view.findViewById(R.id.service_cordova_webview);
        CordovaWebView cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(serviceCordovaWebView));
        cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
        String sessionId = SharedPrefUtils.getSessionId(this);
        String serviceUrl = Constants.SERVICE_URL + "?sessionId=" + sessionId;
        LogUtil.e(TAG, "serviceUrl:" + serviceUrl);
        cordovaWebView.loadUrl(serviceUrl);
        cordovaWebView.handlePause(true);

        servicePopupView = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                ScreenUtil.getScreenHeight(this) / 2);
        // 设置动画
        servicePopupView.setAnimationStyle(R.style.popup_window_anim);
        // 设置背景颜色
        servicePopupView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // 设置可以获取焦点
        servicePopupView.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        servicePopupView.setOutsideTouchable(true);
        // 更新popupwindow的状态
        servicePopupView.update();
        // 从底部弹出
        View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        servicePopupView.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    public void closeServiceWindow() {
        if (servicePopupView.isShowing()) {
            servicePopupView.dismiss();
        }
    }

    /**
     * 更新user数据
     *
     * @param pointUserInfoBean
     */
    public void setPointUserInfoBean(PointUserInfoBean pointUserInfoBean) {
        if (pointUserInfoBean == null) {
            return;
        }
        if (pointUserInfoBean.getAvatar() != null) {
            this.pointUserInfoBean.setAvatar(pointUserInfoBean.getAvatar());
        }
        if (pointUserInfoBean.getUserName() != null) {
            this.pointUserInfoBean.setUserName(pointUserInfoBean.getUserName());
        }
        if (pointUserInfoBean.getCompanyCode() != null) {
            this.pointUserInfoBean.setCompanyCode(pointUserInfoBean.getCompanyCode());
        }
    }

    /**
     * 设置小圆点个人信息
     */
    private void setCompanyInfo(PointUserInfoBean bean) {
//        if (bean == null) {
//            return;
//        }

        if (SuneeeApplication.user != null) {
            if (!StringUtils.isEmpty(SuneeeApplication.getUser().getPhoto())) {
                ImageLoader.getInstance().displayImage(Constants.BASE_HEADIMG_URL + SuneeeApplication.getUser().getPhoto(), imageView);
            }
            if (!StringUtils.isEmpty(SuneeeApplication.getUser().getAliasName())) {
                userNameTv.setText(SuneeeApplication.getUser().getAliasName() + "");
            } else {
                userNameTv.setText(SuneeeApplication.getUser().getName() + "");
            }
            if (!StringUtils.isEmpty(SuneeeApplication.getUser().getSelectCompanyId())) {
                UserCompanyBean userCompanyBean = DataSupport.where("comp_code = ?", SuneeeApplication.getUser().getSelectCompanyId()).findFirst(UserCompanyBean.class);
                if (userCompanyBean != null) {
                    companyNameTv.setText(userCompanyBean.getComp_name());
                }
            }
        }

    }

    /**
     * 切换企业
     */
    protected void changeCompanyAction() {
        popupWindow.dismiss();
    }

    /**
     * 重新设置主页的配置和权限
     */
    protected abstract void setMainPageDataAndPower(String weilianType);

    /**
     * 跳转到鉴权页
     */
    protected void goToAuthenticationActivity() {
        Utils.startActivity(WLActivity.this, AuthenticationActivity.class);
        SuneeeApplication.getInstance().removeActivity_(UcpWebViewActivity.class);
    }

    /**
     * 展示悬浮菜单
     */
    protected void showPopupWindow(View viewPranet, String loadkey, String title) {
        View popupView = this.getLayoutInflater().inflate(R.layout.popupwindow_menu_layout, null);
        GridView gv_suspend_menu = (GridView) popupView.findViewById(R.id.gv_suspend_menu);
        gv_suspend_menu.setSelector(new ColorDrawable(Color.TRANSPARENT));//设置点击背景

        TextView Tvtitle = (TextView) popupView.findViewById(R.id.tv_title);// 标题
        Tvtitle.setText(title);
//        View view_small_red_point = popupView.findViewById(R.id.view_small_red_point);
//        if (SuneeeApplication.user.isHavMessage) {
//            view_small_red_point.setVisibility(View.VISIBLE);
//        } else {
//            view_small_red_point.setVisibility(View.GONE);
//        }
        LinearLayout ll_home_page = (LinearLayout) popupView.findViewById(R.id.ll_home_page);
//        LinearLayout llLogout = (LinearLayout) popupView.findViewById(R.id.ll_logout);
//        llLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.startActivity(WLActivity.this, AuthenticationActivity.class);
//                SuneeeApplication.getInstance().removeActivity_(UcpWebViewActivity.class);
//            }
//        });
//        popupView.findViewById(R.id.ll_soso).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Constants.WEI_LIAN_BAO.equals(SuneeeApplication.weilianType) || Constants.WEI_LIAN_WA.equals(SuneeeApplication.weilianType)) {
//                    Utils.goWebMainActivity(WLActivity.this, Constants.CONSTRUCTING_URL, "", "");
////                    ToastUtil.showSingleToast(WLActivity.this,"正在建设中");
//                } else {
////                    String link = SuneeeApplication.getInstance().commandSpaceTime;
//                    String link = Constants.POINT_SEARCH_URL;
//                    String title = "时空中心";
//
//                    Intent intent = new Intent(WLActivity.this, WebMainActivity.class);
//                    intent.putExtra("link", link);
//                    intent.putExtra("title", title);
//                    intent.putExtra("TagPlugin", TimeSpacePlugin.TAG);
//                    WLActivity.this.startActivity(intent);
//                }
//                popupWindow.dismiss();
//            }
//        });
        popupView.findViewById(R.id.ll_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.startActivity(WLActivity.this, PersonCenterActivity.class);
                popupWindow.dismiss();
            }
        });
//        popupView.findViewById(R.id.ll_info).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.startActivity(WLActivity.this, MessageActivity.class);
//                popupWindow.dismiss();
//            }
//        });
//        LinearLayout img_server_icon = (LinearLayout) popupView.findViewById(R.id.img_server_icon);
//        if (Constants.WEI_LIAN_BAO.equals(SuneeeApplication.weilianType) || Constants.WEI_LIAN_WA.equals(SuneeeApplication.weilianType)) {
//            img_server_icon.setVisibility(View.INVISIBLE);
//        }
//        img_server_icon.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(WLActivity.this, ServerMainLoginActivity.class);
//                intent.putExtra("isAutoSkip", false);
//                startActivity(intent);
//                popupWindow.dismiss();
//            }
//        });

        popupWindow = new PopupWindow(popupView, DensityUtil.dip2px(this, 180), DensityUtil.dip2px(this, 260));
        // 设置动画
        popupWindow.setAnimationStyle(R.style.popup_window_anim);
        // 设置背景颜色
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // 设置可以获取焦点
        popupWindow.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                back.setVisibility(View.VISIBLE);
            }
        });
        // 更新popupwindow的状态
        popupWindow.update();
        // 以下拉的方式显示，并且可以设置显示的位置
        popupWindow.showAsDropDown(viewPranet, 0, 0);
    }

    private void show() {

    }

    @Override
    public void onClick(View v) {

    }

    @SuppressLint("NewApi")
    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        // Capture requestCode here so that it is captured in the setActivityResultCallback() case.
        cordovaInterface.setActivityResultRequestCode(requestCode);

        if(requestCode == 5173){
            selectImage(intent,requestCode,options);
        }else{
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    public void startSuperActivityForResult(Intent intent, int requestCode, Bundle options){
        super.startActivityForResult(intent, requestCode, options);
    }

//    @SuppressLint("RestrictedApi")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        cordovaInterface.onActivityResult(requestCode, resultCode, data);
//
//        FragmentManager fm = getSupportFragmentManager();
//        int index = requestCode >> 16;
//        if (index != 0) {
//            index--;
//            if (fm.getFragments() == null || index < 0 || index >= fm.getFragments().size()) {
//                Log.w(TAG, "Activity result fragment index out of range: 0x" + Integer.toHexString(requestCode));
//                return;
//            }
//            Fragment frag = fm.getFragments().get(index);
//            if (frag == null) {
//                Log.w(TAG, "Activity result no fragment exists for index: 0x" + Integer.toHexString(requestCode));
//            } else {
//                handleResult(frag, requestCode, resultCode, data);
//            }
//            return;
//        }
//    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        LOG.d(TAG, "Incoming Result. Request code = " + requestCode);
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_CANCELED) {
            cordovaInterface.onActivityResult(requestCode, RESULT_CANCELED, intent);
            return;
        }

        switch (requestCode) {
            case 5173:
                cordovaInterface.onActivityResult(requestCode, resultCode, intent);
                break;
            case TAKE_BIG_PICTURE:

                File file = new File(imageUri.getPath());
                if(!file.exists()){
                    imageUri = Uri.parse("");
                }

                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent data = new Intent();
                data.setData(imageUri);

                cordovaInterface.onActivityResult(requestCode, resultCode, data);

                break;
        }

        FragmentManager fm = getSupportFragmentManager();
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            if (fm.getFragments() == null || index < 0 || index >= fm.getFragments().size()) {
                Log.w(TAG, "Activity result fragment index out of range: 0x" + Integer.toHexString(requestCode));
                return;
            }
            Fragment frag = fm.getFragments().get(index);
            if (frag == null) {
                Log.w(TAG, "Activity result no fragment exists for index: 0x" + Integer.toHexString(requestCode));
            } else {
                handleResult(frag, requestCode, resultCode, intent);
            }
            return;
        }
    }

    protected final void selectImage(final Intent intent,final int requestCode,final Bundle options) {

        String[] selectPicTypeStr = { "拍照","从相册选择" };
        AlertDialog alertDialog = new AlertDialog.Builder(this).setItems(selectPicTypeStr,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        switch (which) {
                            // 相机拍摄
                            case TAKE_BIG_PICTURE:
                                getImageFromCamera();
                                break;
                            // 手机相册
                            case CHOOSE_BIG_PICTURE:
                                startSuperActivityForResult(intent, requestCode, options);
                                break;
                            default:
                                break;
                        }
                    }
                }).setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                cordovaInterface.onActivityResult(requestCode, RESULT_CANCELED, intent);
            }
        }).show();
    }

    public Uri imageUri;
    public String imagePath;
    public static final int TAKE_BIG_PICTURE = 0;
    public static final int CHOOSE_BIG_PICTURE = 1;

    public void getImageFromRoom() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        //super.startActivityForResult(intent, CHOOSE_BIG_PICTURE);
        startSuperActivityForResult(intent,CHOOSE_BIG_PICTURE,null);
    }
    public void getImageFromCamera() {
        String state = Environment.getExternalStorageState(); // 拿到sdcard是否可用的状态码
        if (state.equals(Environment.MEDIA_MOUNTED)) { // 如果可用

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

            FileUtils fileUtils = FileUtils.getInstance(Constants.DEFAULT_SAVE_IMAGE_PATH);
            String fileName = Constants.CROP_CACHE_FILE_NAME + System.currentTimeMillis() + ".jpg";

            imagePath = fileUtils.getFilePath(fileName);

            // 必须确保文件夹路径存在，否则拍照后无法完成回调
            File vFile = new File(imagePath);
            if (!vFile.exists()) {
                File vDirPath = vFile.getParentFile();
                vDirPath.mkdirs();
            } else {
                if (vFile.exists()) {
                    vFile.delete();
                }
            }
            imageUri = Uri.fromFile(vFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startSuperActivityForResult(intent,TAKE_BIG_PICTURE,null);

        } else {
            Toast.makeText(this, "sdcard不可用", Toast.LENGTH_SHORT).show();
        }
    }

}
