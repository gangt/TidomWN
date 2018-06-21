package com.xiangpu.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konecty.rocket.chat.AuthenticationActivity;
import com.konecty.rocket.chat.R;
import com.lssl.activity.MainActivity;
import com.lssl.activity.SuneeeApplication;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiangpu.activity.usercenter.PersonCenterActivity;
import com.xiangpu.adapter.SuspendMenuAdapter;
import com.xiangpu.bean.MenuBean;
import com.xiangpu.bean.MenuBean.MenuItem;
import com.xiangpu.bean.PointUserInfoBean;
import com.xiangpu.bean.UserCompBean;
import com.xiangpu.bean.UserCompanyBean;
import com.xiangpu.common.Constants;
import com.xiangpu.dialog.CustomProgressDialog;
import com.xiangpu.receivers.NetBroadcastReceiver;
import com.xiangpu.utils.DensityUtil;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.NetWorkUtils;
import com.xiangpu.utils.ScreenUtil;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.Utils;
import com.xiangpu.views.WebBackImageView;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BaseActivity";
    protected CordovaWebView cordovaWebView = null;//当前选择的web

    protected CordovaWebView cordovaWebView1 = null;//用户指挥中心，页面能左右滑动，切换的web
    protected CordovaWebView cordovaWebView2 = null;//用户指挥中心，页面能左右滑动，切换的web
    protected CordovaWebView cordovaWebView3 = null;//用户指挥中心，页面能左右滑动，切换的web

    protected CustomProgressDialog mProgressDialog;
    private NetBroadcastReceiver nbReceiver;  // 网络变化广播监听
    protected static Context context = null;

    //小圆点菜单
    private ArrayList<MenuItem> menuItems;

    private View view;
    private Button loadingBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        SuneeeApplication.getInstance().addActivity_(this);
    }

    /**
     * 统一跳转到web 页面入口
     */
    protected void goWebMainActivity(String link, String title, String tagPlugin) {
        Intent intent = new Intent(this, WebMainActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("link", link);
        intent.putExtra("TagPlugin", tagPlugin);
        this.startActivity(intent);
    }

    /**
     * 网络广播监听
     */
    public void initNetBroadcastReceiver() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        nbReceiver = new NetBroadcastReceiver();
        nbReceiver.setListener(new NetBroadcastReceiver.ActionListener() {
            @Override
            public void receiveResult(boolean isNetworkOk) {
                isShowErrorNotice(isNetworkOk);
                netBrResult(isNetworkOk);
            }
        });
        registerReceiver(nbReceiver, mFilter);
    }

    /**
     * @param netResult：表示网络状态，true有网络，false无网络
     */
    protected void netBrResult(boolean netResult) {
        LogUtil.d("netResult: " + netResult);
    }

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
        if (view != null) removeErrorNetworkView();

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                DensityUtil.dip2px(this, 53));
        view = getLayoutInflater().inflate(R.layout.layout_netbar, (ViewGroup) view);
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
     * 显示自定义进度框
     */
    protected void showProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog = new CustomProgressDialog(this, R.style.CustomProgressDialog);
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

    /**
     * Easy to hide the soft keyboard
     *
     * @param view A view to getWindowToken
     */
    protected void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回处理
     */
    public void addBackView() {
        FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        backParams.bottomMargin = 60;
        backParams.rightMargin = 45;

        backParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        WebBackImageView back = new WebBackImageView(this);
        back.setBackgroundResource(R.drawable.lssl_web_back);
        back.setClickable(true);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.finish();
            }
        });
        addContentView(back, backParams);
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
                //if(checkLogin())
                {
                    hideKeyboard(back);
                    showPopupWindow(back, loadkey, title);
                    back.setVisibility(View.INVISIBLE);
                    if (!showCompany) {
                        hideCompanyView();
                    }
                }
//                else{
//                    Utils.startActivity(BaseActivity.this,ServerMainLoginActivity.class);
//                }
            }
        });
        addContentView(back, backParams);
    }

    public ArrayList<MenuItem> getMenuItems(String loadkey) {
        ArrayList<MenuItem> retMenu = null;
        retMenu = loadMenuItems();

        if (retMenu == null) {
            try {
                return (ArrayList<MenuItem>) readMenuItem(loadkey);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return retMenu;
    }

    protected ArrayList<MenuItem> loadMenuItems() {
        return null;
    }

    protected void sendMenuCmd(String cmd) {
    }

    protected void showCommandList() {
    }

    private PointUserInfoBean pointUserInfoBean = new PointUserInfoBean();
    private LinearLayout companyLayout; // 小圆点企业切换
    private TextView tvLine;
    private RoundedImageView imageView; // 用户头像
    private TextView companyNameTv; // 企业名称
    private TextView userNameTv; // 用户名称

    private PopupWindow popupWindow;
    private boolean showCompany = true;
    private boolean customBack = false; // 小圆点是否自定义回退键

    /**
     * 展示悬浮菜单
     */
    protected void showPopupWindow(View viewParent, String loadkey, String title) {

        menuItems = getMenuItems(loadkey);
        if (menuItems == null) {
            return;
        }

        View popupView = null;
        UserCompBean userCompBean = SuneeeApplication.getUserCompBean();
        String compId = userCompBean.getCompId();
        String userType = userCompBean.getUserType();

        if (!Constants.WEI_LIAN_HAI.equals(SuneeeApplication.weilianType)) {
            popupView = this.getLayoutInflater().inflate(R.layout.popupwindow_weilian_layout, null);
            popupView.findViewById(R.id.ll_back).setOnClickListener(new MyButtonClickListener(title));
            popupView.findViewById(R.id.ll_my).setOnClickListener(new MyButtonClickListener(title));
            popupView.findViewById(R.id.ll_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.startActivity(BaseActivity.this, AuthenticationActivity.class);
                    SuneeeApplication.getInstance().removeActivity_(UcpWebViewActivity.class);
                }
            });
        } else {
            popupView = this.getLayoutInflater().inflate(R.layout.popupwindow_menu_layout, null);
            GridView gv_suspend_menu = (GridView) popupView.findViewById(R.id.gv_suspend_menu);
            gv_suspend_menu.setSelector(new ColorDrawable(Color.TRANSPARENT));//设置点击背景

            View view_small_red_point = popupView.findViewById(R.id.view_small_red_point);
            if (SuneeeApplication.user.isHavMessage) {
                view_small_red_point.setVisibility(View.VISIBLE);
            } else {
                view_small_red_point.setVisibility(View.GONE);
            }

            popupView.findViewById(R.id.ll_back).setOnClickListener(new MyButtonClickListener(title));
            popupView.findViewById(R.id.ll_home_page).setOnClickListener(new MyButtonClickListener(title));
            popupView.findViewById(R.id.ll_my).setOnClickListener(new MyButtonClickListener(title));
            popupView.findViewById(R.id.ll_service).setOnClickListener(new MyButtonClickListener(title));
//            LinearLayout llLogout = (LinearLayout) popupView.findViewById(R.id.ll_logout);
//            llLogout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SharedPrefUtils.saveStringData(BaseActivity.this, "router", null);
//                    Utils.startActivity(BaseActivity.this, AuthenticationActivity.class);
//                    SuneeeApplication.getInstance().removeActivity_(UcpWebViewActivity.class);
//                }
//            });
//            popupView.findViewById(R.id.ll_soso).setOnClickListener(new MyButtonClickListener(title));
//            popupView.findViewById(R.id.ll_info).setOnClickListener(new MyButtonClickListener(title));
//            LinearLayout img_server_icon = (LinearLayout) popupView.findViewById(R.id.img_server_icon);

            companyLayout = (LinearLayout) popupView.findViewById(R.id.company_layout);
            tvLine = (TextView) popupView.findViewById(R.id.tv_line);
            companyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeCompanyAction();
                }
            });
            imageView = (RoundedImageView) companyLayout.findViewById(R.id.avatar);
            companyNameTv = (TextView) companyLayout.findViewById(R.id.company_name);
            userNameTv = (TextView) companyLayout.findViewById(R.id.user_name);
            companyLayout.setVisibility(View.VISIBLE);
            tvLine.setVisibility(View.VISIBLE);
            setCompanyInfo(pointUserInfoBean);

//            if (Constants.WEI_LIAN_BAO.equals(SuneeeApplication.weilianType)
//                    || Constants.WEI_LIAN_WA.equals(SuneeeApplication.weilianType)
//                    || "官网".equals(title) || "象谱商城".equals(title)) {
//                img_server_icon.setVisibility(View.INVISIBLE);
//            }
//            img_server_icon.setOnClickListener(new MyButtonClickListener(title));

            gv_suspend_menu.setAdapter(new SuspendMenuAdapter(this, menuItems));
            gv_suspend_menu.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    if (!checkLogin()) {
                        popupWindow.dismiss();
                        finish();
                        return;
                    }
                    MenuItem menuitem = menuItems.get(position);

                    String linkType = menuitem.getMenuItemType();
                    String linkSub = menuitem.getMenuItemSub();

                    if (!TextUtils.isEmpty(linkType) && (linkType.startsWith("url"))) {
                        //跳转Url
                        Intent intent = new Intent(getApplicationContext(), WebMainActivity.class);
                        intent.putExtra("title", menuitem.getMenuItemName());
                        intent.putExtra("link", linkSub);
                        startActivity(intent);
                    } else if (!TextUtils.isEmpty(linkType) && (linkType.startsWith("action"))) {

                        //原生页面跳转
                        String intenturl = linkSub;
                        if (!TextUtils.isEmpty(intenturl)) {
                            if (intenturl.contains("SwitchCommand")) {
                                showCommandList();
                                return;
                            } else if (intenturl.contains("com.xiangpu")) {

                                Intent intent = new Intent();
                                intent.setClassName(getApplicationContext(), intenturl);
                                intent.putExtra("title", menuitem.getMenuItemName());

                                if (!checkRight(menuitem.getMenuItemName())) {
                                    ToastUtils.showCenterToast(getApplicationContext(), "尚未开通此权限");
                                    return;
                                }

                                if ("新增内容".equals(menuitem.getMenuItemName())) {
                                    intent.putExtra(Constants.TARGET_PAGE, Constants.CREATE_CONTENT);
                                } else if ("删除内容".equals(menuitem.getMenuItemName())) {
                                    intent.putExtra(Constants.TARGET_PAGE, Constants.DAELETE_CONTENT);
                                } else if (intenturl.contains("ControlRoom")) {
                                    if (SuneeeApplication.user != null && SuneeeApplication.user.curCommand != null) {
                                        String currentCommandId = SuneeeApplication.user.curCommand.commandid;
                                        intent.putExtra(Constants.CURRENT_COMMAND_ID, currentCommandId);
                                    }
                                }

                                startActivity(intent);
                            } else {
                                sendMenuCmd(linkSub);
                            }
                        }

                    }
                    popupWindow.dismiss();
                }

            });
        }

        // 创建PopupWindow对象，指定宽度和高度
        popupWindow = new PopupWindow(popupView, DensityUtil.dip2px(this, 174),
                DensityUtil.dip2px(this, 204));
//        // 设置动画
//        popupWindow.setAnimationStyle(R.style.popup_window_anim);
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
        int offsetX = 0;
        int offsetY = 0;

        popupWindow.showAsDropDown(viewParent, offsetX, offsetY);

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
     * @param flag：false隐藏公司布局，true显示公司布局
     */
    public void setShowCompany(boolean flag) {
        this.showCompany = flag;
    }

    /**
     * 隐藏公司布局
     */
    private void hideCompanyView() {
        if (tvLine != null) {
            tvLine.setVisibility(View.GONE);
        }
        if (companyLayout != null) {
            companyLayout.setVisibility(View.GONE);
        }
    }

    protected void setCustomBack(boolean flag) {
        this.customBack = flag;
    }

    /**
     * 自定义小圆点回退键方法
     */
    protected void customPopupViewBack() {

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
        if (pointUserInfoBean.getAvatar() != null)
            this.pointUserInfoBean.setAvatar(pointUserInfoBean.getAvatar());
        if (pointUserInfoBean.getUserName() != null)
            this.pointUserInfoBean.setUserName(pointUserInfoBean.getUserName());
        if (pointUserInfoBean.getCompanyCode() != null)
            this.pointUserInfoBean.setCompanyCode(pointUserInfoBean.getCompanyCode());
    }

    /**
     * 设置小圆点个人信息
     */
    private void setCompanyInfo(PointUserInfoBean bean) {
//        if (bean == null) {
//            return;
//        }
        if (!StringUtils.isEmpty(SuneeeApplication.getUser().getPhoto())) {
            ImageLoader.getInstance().displayImage(Constants.BASE_HEADIMG_URL + SuneeeApplication.getUser().getPhoto(), imageView);
        }
        if (!StringUtils.isEmpty(SuneeeApplication.getUser().getAliasName())) {
            userNameTv.setText(SuneeeApplication.getUser().getAliasName() + "");
        } else if (!StringUtils.isEmpty(SuneeeApplication.getUser().getName())) {
            userNameTv.setText(SuneeeApplication.getUser().getName() + "");
        } else {
            userNameTv.setText("");
        }
        if (!StringUtils.isEmpty(SuneeeApplication.getUser().getSelectCompanyId())) {
            UserCompanyBean userCompanyBean = DataSupport.where("comp_code = ?", SuneeeApplication.getUser().getSelectCompanyId()).findFirst(UserCompanyBean.class);
            if (userCompanyBean != null) {
                companyNameTv.setText(userCompanyBean.getComp_name());
            }
        }
    }

//    private final int CHOOSE_COMPANY = 100;

    /**
     * 切换企业
     */
    protected void changeCompanyAction() {
        Intent intent = new Intent(this, CompanyGridActivity.class);
        intent.putExtra("selectCompanyCode", SuneeeApplication.getUser().getSelectCompanyId());
        intent.putExtra("isGoHomeActivity", true);
        startActivity(intent);
        popupWindow.dismiss();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case CHOOSE_COMPANY:
//                if (resultCode == RESULT_OK) {
//                    String companyCode = data.getStringExtra("companyCode");
//                    SuneeeApplication.user.selectCompanyId = companyCode;
//
//                    startActivity(new Intent(this, MainActivity.class));
//                    this.finish();
//                }
//                break;
//
//            default:
//                break;
//        }
//    }

    @Override
    public void onClick(View v) {

    }

    class MyButtonClickListener implements View.OnClickListener {

        private String title;

        public MyButtonClickListener(String title) {
            this.title = title;
        }

        @Override
        public void onClick(View v) {
            if (!checkLogin()) {
                if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
                finish();
                return;
            }
            switch (v.getId()) {
                case R.id.ll_service:
                    showServicePopupView();
                    break;

                case R.id.ll_home_page:
                    Utils.startActivity(BaseActivity.this, MainActivity.class);
                    break;

                case R.id.ll_back:
                    if (customBack) {
                        customPopupViewBack();
                    } else {
                        if (BaseActivity.this instanceof WebMainActivity) {
                            if (cordovaWebView.canGoBack()) {
                                cordovaWebView.backHistory();
                            } else {
                                BaseActivity.this.finish();
                            }
                        } else {
                            BaseActivity.this.finish();
                        }
                    }
                    break;

//                case R.id.ll_soso:
//                    if (Constants.WEI_LIAN_BAO.equals(SuneeeApplication.weilianType) || Constants.WEI_LIAN_WA.equals(SuneeeApplication.weilianType)) {
//                        Utils.goWebMainActivity(BaseActivity.this, Constants.CONSTRUCTING_URL, "", "");
////                        ToastUtil.showSingleToast(BaseActivity.this,"正在建设中");
//                    } else {
//                        String link = Constants.POINT_SEARCH_URL;
//                        String title = "时空中心";
//                        goWebMainActivity(link, title, TimeSpacePlugin.TAG);
//                    }
//                    break;

                case R.id.ll_my:
                    Utils.startActivity(BaseActivity.this, PersonCenterActivity.class);
                    break;

//                case R.id.ll_info:
//                    Utils.startActivity(BaseActivity.this, MessageActivity.class);
//                    break;

//                case R.id.img_server_icon:
//                    Intent intent = new Intent(BaseActivity.this, ServerMainLoginActivity.class);
//                    intent.putExtra("isAutoSkip", false);
//                    startActivity(intent);
//                    break;

                default:
                    break;
            }
            if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
            if ("粒子链".equals(title) && R.id.ll_back != v.getId()) BaseActivity.this.finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * 读本地json
     *
     * @param key
     * @return
     * @throws IOException
     */
    private List<MenuItem> readMenuItem(String key)
            throws IOException {

        List<MenuItem> list = new ArrayList<MenuItem>();

        if ("".equals(key)) {
            return list;
        }

        BufferedReader br = null;

        String localAppConfigFilePath = SuneeeApplication.getInstance().configFilePath;
        File file = new File(localAppConfigFilePath);

        if (file.exists()) {
            //读取服务器下载文件
            String config = SuneeeApplication.getInstance().ConfigMenuPath;
            FileInputStream fileInputStream = new FileInputStream(config);
            br = new BufferedReader(new InputStreamReader(fileInputStream));
        } else {
            //读取本地文件
            InputStream in = this.getAssets().open("config.json");
            br = new BufferedReader(new InputStreamReader(in));
            //in.close();
        }

        //读取本地文件
        //InputStream in = this.getAssets().open("config.json");
        //br = new BufferedReader(new InputStreamReader(in));

        if (br != null) {

            StringBuffer input = new StringBuffer();
            String line = null;

            while ((line = br.readLine()) != null) {
                input.append(line);
            }
            String result = input.toString();
            result = result.replaceAll("\r", "");
            result = result.replaceAll("	", "");

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONObject jsonOb = jsonObject.getJSONObject("config");

                // 返回json的数组
                JSONArray jsonArray = jsonOb.getJSONArray(key);

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

                    MenuItem menuItem = new MenuBean().new MenuItem();
                    menuItem.setMenuItemName(jsonObject2.getString("title"));
                    menuItem.setMenuItemIconName(jsonObject2.getString("icon"));

                    String type = jsonObject2.getString("type");
                    menuItem.setMenuItemType(type);
                    menuItem.setMenuItemSub(type, jsonObject2.getString("sub"));

                    list.add(menuItem);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public String ConfigJson = "";

    public boolean checkRight(String itemName) {
        if ("新增内容".equals(itemName) || "删除内容".equals(itemName) || "新建指挥".equals(itemName)) {
            if (SuneeeApplication.user.isCommandCreater) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cordovaWebView != null) {
            cordovaWebView.loadUrl("about:blank");
        }
        if (popupWindow != null && popupWindow.isShowing()) popupWindow.dismiss();
        SuneeeApplication.getInstance().removeActivity_(this);

        if (nbReceiver != null) {
            nbReceiver.setListener(null);
            unregisterReceiver(nbReceiver);
        }
    }

    public boolean checkLogin() {
        String sessionId = SharedPrefUtils.getSessionId(this);
        if (sessionId == null || StringUtils.isEmpty(sessionId)) {
            return false;
        }
        return true;
    }

    private RequestPermissionCallBack mRequestPermissionCallBack;
    private final int mRequestCode = 1024;

    /**
     * 权限请求结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        StringBuilder permissionName = new StringBuilder();
        for (String s : permissions) {
            permissionName = permissionName.append(s + "\r\n");
        }
        switch (requestCode) {
            case mRequestCode: {
                for (int i = 0; i < grantResults.length; ++i) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        hasAllGranted = false;
                        //在用户已经拒绝授权的情况下，如果shouldShowRequestPermissionRationale返回false则
                        // 可以推断出用户选择了“不在提示”选项，在这种情况下需要引导用户至设置页手动授权
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                            new AlertDialog.Builder(this)
                                    .setMessage("【用户选择了不在提示按钮，或者系统默认不在提示（如MIUI）。" +
                                            "引导用户到应用设置页去手动授权,注意提示用户具体需要哪些权限】\r\n" +
                                            "获取相关权限失败:\r\n" +
                                            permissionName +
                                            "将导致部分功能无法正常使用，需要到设置页面手动授权")
                                    .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            mRequestPermissionCallBack.denied();
                                        }
                                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mRequestPermissionCallBack.denied();
                                }
                            }).show();

                        } else {
                            //用户拒绝权限请求，但未选中“不再提示”选项
                            mRequestPermissionCallBack.denied();
                        }
                        break;
                    }
                }
                if (hasAllGranted) {
                    mRequestPermissionCallBack.granted();
                }
            }
        }
    }

    /**
     * 发起权限请求
     *
     * @param context
     * @param permissions
     * @param callback
     */
    public void requestPermissions(final Context context, final String[] permissions,
                                   RequestPermissionCallBack callback) {
        this.mRequestPermissionCallBack = callback;
        StringBuilder permissionNames = new StringBuilder();
        for (String s : permissions) {
            permissionNames = permissionNames.append(s + "\r\n");
        }
        //如果所有权限都已授权，则直接返回授权成功,只要有一项未授权，则发起权限请求
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                isAllGranted = false;

                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
//                    new AlertDialog.Builder(context)
//                            .setMessage("【用户曾经拒绝过你的请求，所以这次发起请求时解释一下】\r\n" +
//                                    "您好，需要如下权限：\r\n" +
//                                    permissionNames +
//                                    " 请允许，否则将影响部分功能的正常使用。")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
//                                }
//                            }).show();
                } else {
                    ActivityCompat.requestPermissions(((Activity) context), permissions, mRequestCode);
                }

                break;
            }
        }
        if (isAllGranted) {
            mRequestPermissionCallBack.granted();
            return;
        }
    }

    /**
     * 权限请求结果回调接口
     */
    public interface RequestPermissionCallBack {
        /**
         * 同意授权
         */
        public void granted();

        /**
         * 取消授权
         */
        public void denied();
    }

}
