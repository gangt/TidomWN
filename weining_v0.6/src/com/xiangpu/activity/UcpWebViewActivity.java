package com.xiangpu.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.konecty.rocket.chat.AuthenticationActivity;
import com.konecty.rocket.chat.R;
import com.lssl.activity.MainActivity;
import com.lssl.activity.SuneeeApplication;
import com.lssl.activity.WebViewActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.suneee.demo.scan.decode.Intents;
import com.xiangpu.activity.person.ServerMainLoginActivity;
import com.xiangpu.activity.usercenter.PersonCenterActivity;
import com.xiangpu.adapter.SuspendMenuAdapter;
import com.xiangpu.bean.MenuBean;
import com.xiangpu.bean.PointUserInfoBean;
import com.xiangpu.bean.UserCompanyBean;
import com.xiangpu.common.Constants;
import com.xiangpu.plugin.TimeSpacePlugin;
import com.xiangpu.plugin.UCPCommandPlugin;
import com.xiangpu.utils.DensityUtil;
import com.xiangpu.utils.FileUtils;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.ScreenUtil;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.Utils;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

import chat.rocket.android.InitializeUtils;
import chat.rocket.android.LaunchUtil;
import chat.rocket.android.RocketChatApplication;
import chat.rocket.android.RocketChatCache;
import chat.rocket.android.activity.ChatMainActivity;
import chat.rocket.android.video.helper.TempFileUtils;

/**
 * Created by fangfumin on 2017/7/27.
 */
public class UcpWebViewActivity extends Activity {
    /**
     * nType	string	是	‘video’ or ‘audio’	类别,正常消息不存在这个字段
     * mediaId	string	是	‘xxxxxxxxxxxxxxxxxxxxxxxxxxx’	音视频通话房间Id
     * status	string	是	‘wait’ or ‘noreplay’ or ‘refuse’ or ‘end’	消息状态
     * receiveMsg	string	是	根据状态不同显示不同的文案	无
     * fromMsg	string	是	根据状态不同显示不同的文案	无
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_rocket_chat);
        String router = SharedPrefUtils.getStringData(this, "router", "");
        String rid = "";
        String nType = "";
        String name = "";
        String ruserId = "";
        String mediaId = "";
        try {
            JSONObject jsonObject = new JSONObject(router);
            rid = jsonObject.getString("rid");
            nType = jsonObject.getString("nType");
            ruserId = jsonObject.getString("userId");
            name = jsonObject.getString("name");
            mediaId = jsonObject.getString("mediaId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //{"mediaId":"768e36c2-ace6-4a4a-b990-b0e92a8e0b00","nType":"audio","name":"刘波&581490","realName":"刘波","rid":"87fJnK25FCyiBWa7KPX6mdpYGYi44X5KBC","status":"wait","type":"d","userId":"PX6mdpYGYi44X5KBC"}
        if ("video".equals(nType) || "audio".equals(nType)) {
            //处理音视频消息
            if (TempFileUtils.getInstance().getTalkingStatus()) {
                RocketChatApplication.stopSound();
                finish();
                return;
            }
            if (mediaId != null && mediaId.equals(TempFileUtils.getInstance().getMediaId())) {
                go2ChatMainActivity();
                RocketChatApplication.stopSound();
                finish();
                return;
            }
            LaunchUtil.showVideoActivityFromNotification(ruserId, name, "", rid, false, nType.equals("video"), mediaId);
            finish();
            return;
        }
        Intent intent = new Intent(this, ChatMainActivity.class);
        String sessionId = SharedPrefUtils.getSessionId(this);
        String userId = SharedPrefUtils.getUserId(this);
        String companyId = SharedPrefUtils.getSelectCompanyCode(this);
        intent.putExtra("sessionId", sessionId);
        intent.putExtra("userId", userId);
        intent.putExtra("companyId", companyId);
        intent.putExtra("roomId", rid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        RocketChatCache.INSTANCE.setSelectedRoomId(rid);
        startActivity(intent);
        finish();
    }

    public void go2ChatMainActivity(){
        String userId = SharedPrefUtils.getUserId(this);
        String companyId = SharedPrefUtils.getSelectCompanyCode(this);
//                if (!userId.equals(RocketChatCache.INSTANCE.getFrameUserId()))
//                    InitializeUtils.getInstance().exit();
        if (!chat.rocket.android.helper.TextUtils.isEmpty(RocketChatCache.INSTANCE.getUserId())&&!userId.equals(RocketChatCache.INSTANCE.getFrameUserId())){
            try {
                InitializeUtils.getInstance().clearSession();
//                    InitializeUtils.getInstance().clearSubscription();
                InitializeUtils.getInstance().clearUser();
            } catch (Exception e) {
            }
        }
        Intent itDzl = new Intent(this, chat.rocket.android.activity.ChatMainActivity.class);
        String sessionId = SharedPrefUtils.getSessionId(this);
        String sessionId1 = RocketChatCache.INSTANCE.getSessionId();
        itDzl.putExtra("sessionId", sessionId);
        itDzl.putExtra("userId", userId);
        itDzl.putExtra("companyId", companyId);
        startActivity(itDzl);
    }



    //    public static final String TAG = UcpWebViewActivity.class.getSimpleName();
//
//    private ArrayList<MenuBean.MenuItem> menuItems = null;
//
//    private CordovaPlugin mPlugin = null;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        SuneeeApplication.getInstance().addActivity_(this);
//        setContentView(R.layout.activity_test_cordova_with_layout);
//
//        loadUrl(launchUrl);
//        loadPoint("", "定子链");
//    }
//
//    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            LogUtil.i(TAG, "Receive Broadcast to SEND_ROUTER_TO_UCP");
//            String action = intent.getAction();
//            String router = intent.getStringExtra("router");
//            if (action.equals(Constants.SEND_ROUTER_TO_UCP)) {
//                JSONObject jsonObject = new JSONObject();
//                String cmd = "";
//                try {
//                    jsonObject.put("source", "ucp");
//                    jsonObject.put("type", "router");
//                    jsonObject.put("url", router);
//                    cmd = jsonObject.toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                sendCmd(cmd);
//            }
//        }
//    };
//
//    @Override
//    protected CordovaWebView makeWebView() {
//        SystemWebView webView = (SystemWebView) findViewById(R.id.cordovaWebView);
//        return new CordovaWebViewImpl(new SystemWebViewEngine(webView));
//    }
//
//    @Override
//    protected void createViews() {
//
//        if (preferences.contains("BackgroundColor")) {
//            int backgroundColor = preferences.getInteger("BackgroundColor", Color.BLACK);
//            // Background of activity:
//            appView.getView().setBackgroundColor(backgroundColor);
//        }
//
//        appView.getView().requestFocusFromTouch();
//    }
//
//    public void sendCmd(String cmd) {
//
//        mPlugin = appView.getPluginManager().getPlugin("UCPCommandPlugin");
//        if (mPlugin != null) {
//            ((UCPCommandPlugin) mPlugin).AndroidUcpCommand(cmd);
//        }
//    }
//
//    com.xiangpu.views.WebBackImageView back;
//
//    /**
//     * Easy to hide the soft keyboard
//     *
//     * @param view A view to getWindowToken
//     */
//    protected void hideKeyboard(View view) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//    }
//
//    /**
//     * 添加悬浮小控件
//     *
//     * @param
//     */
//    public void loadPoint(final String loadkey, final String title) {
//        FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(
//                DensityUtil.dip2px(this, 60),
//                DensityUtil.dip2px(this, 60));
//        backParams.bottomMargin = DensityUtil.dip2px(this, 10);
//        backParams.rightMargin = DensityUtil.dip2px(this, 10);
//
//        backParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
//        back = new com.xiangpu.views.WebBackImageView(this);
//        back.setBackgroundResource(R.drawable.point);
//
//        back.setClickable(true);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideKeyboard(back);
//                showPopupWindow(back, loadkey, title);
//                back.setVisibility(View.INVISIBLE);
//            }
//        });
//        addContentView(back, backParams);
//    }
//
//    private boolean loadMenuItems() { // 加载菜单
//        mPlugin = appView.getPluginManager().getPlugin("UCPCommandPlugin");
//        if (mPlugin != null) {
//            JSONObject jsonMenu = ((UCPCommandPlugin) mPlugin).getLoadMenu();
//            try {
//                return LoadMenu(jsonMenu);
//            } catch (Exception e) {
//
//            }
//        }
//        return false;
//    }
//
//    private PointUserInfoBean pointUserInfoBean = new PointUserInfoBean();
//    private LinearLayout companyLayout; // 小圆点企业切换
//    private TextView tvLine;
//    private RoundedImageView imageView; // 用户头像
//    private TextView companyNameTv; // 企业名称
//    private TextView userNameTv; // 用户名称
//
//    private boolean LoadMenu(JSONObject result) throws JSONException {
//
//        if (result == null) {
//            return false;
//        }
//
//        String name = result.getString("name");
//        String type = result.getString("type");
//        JSONArray buttons = result.getJSONArray("buttons");
//
//        if (menuItems != null) {
//            menuItems.clear();
//            menuItems = null;
//        }
//
//        if (menuItems == null) {
//            menuItems = new ArrayList<MenuBean.MenuItem>();
//
//            for (int i = 0; i < buttons.length(); i++) {
//
//                JSONObject jsonObject2 = buttons.getJSONObject(i);
//
//                MenuBean.MenuItem menuItem = new MenuBean().new MenuItem();
//                menuItem.setMenuItemName(jsonObject2.getString("title"));
//                menuItem.setMenuItemIconName(jsonObject2.getString("icon"));
//
//                String strType = jsonObject2.getString("type");
//                menuItem.setMenuItemType(strType);
//                menuItem.setMenuItemSub(strType, jsonObject2.getString("sub"));
//
//                menuItems.add(menuItem);
//            }
//        }
//        return true;
//    }
//
//    PopupWindow popupWindow;
//
//    /**
//     * 展示悬浮菜单
//     */
//    protected void showPopupWindow(View viewParent, String loadkey, String title) {
//        if (!loadMenuItems()) {
////            return;
//        }
//        //加载菜单
//
//        View popupView = this.getLayoutInflater().inflate(R.layout.popupwindow_menu_layout, null);
//        GridView gv_suspend_menu = (GridView) popupView.findViewById(R.id.gv_suspend_menu);
//        gv_suspend_menu.setSelector(new ColorDrawable(Color.TRANSPARENT));//设置点击背景
//
//        View view_small_red_point = popupView.findViewById(R.id.view_small_red_point);
//        if (SuneeeApplication.user.isHavMessage) {
//            view_small_red_point.setVisibility(View.VISIBLE);
//        } else {
//            view_small_red_point.setVisibility(View.GONE);
//        }
//        LinearLayout ll_back = (LinearLayout) popupView.findViewById(R.id.ll_back);
//        ll_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendCmd("exitEnterpriseEmail");
//                if (appView.canGoBack()) {
//                    appView.backHistory();
//                } else {
//                    UcpWebViewActivity.this.finish();
//                }
//                popupWindow.dismiss();
//            }
//        });
//        LinearLayout ll_home_page = (LinearLayout) popupView.findViewById(R.id.ll_home_page);
//        ll_home_page.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(UcpWebViewActivity.this, com.lssl.activity.MainActivity.class);
//                startActivity(intent);
//                popupWindow.dismiss();
//            }
//        });
//        popupView.findViewById(R.id.ll_my).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Utils.startActivity(UcpWebViewActivity.this, PersonCenterActivity.class);
//                popupWindow.dismiss();
//            }
//        });
////        LinearLayout llLogout = (LinearLayout) popupView.findViewById(R.id.ll_logout);
////        llLogout.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                SharedPrefUtils.saveStringData(UcpWebViewActivity.this, "router", null);
////                Utils.startActivity(UcpWebViewActivity.this, AuthenticationActivity.class);
////                SuneeeApplication.getInstance().removeActivity_(UcpWebViewActivity.class);
////            }
////        });
//        popupView.findViewById(R.id.ll_service).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showServicePopupView();
//                popupWindow.dismiss();
//            }
//        });
////        popupView.findViewById(R.id.ll_soso).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                String link = Constants.POINT_SEARCH_URL;
////                String title = "时空中心";
////                goWebMainActivity(link, title, TimeSpacePlugin.TAG);
////                popupWindow.dismiss();
////            }
////        });
////        popupView.findViewById(R.id.img_server_icon).setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent intent = new Intent(UcpWebViewActivity.this, ServerMainLoginActivity.class);
////                intent.putExtra("isAutoSkip", false);
////                startActivity(intent);
////                popupWindow.dismiss();
////            }
////        });
//
//        companyLayout = (LinearLayout) popupView.findViewById(R.id.company_layout);
//        tvLine = (TextView) popupView.findViewById(R.id.tv_line);
//        companyLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeCompanyAction();
//            }
//        });
//        imageView = (RoundedImageView) companyLayout.findViewById(R.id.avatar);
//        companyNameTv = (TextView) companyLayout.findViewById(R.id.company_name);
//        userNameTv = (TextView) companyLayout.findViewById(R.id.user_name);
//        companyLayout.setVisibility(View.VISIBLE);
//        tvLine.setVisibility(View.VISIBLE);
//        setCompanyInfo(pointUserInfoBean);
//
//        gv_suspend_menu.setAdapter(new SuspendMenuAdapter(this, menuItems));
//        gv_suspend_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//                MenuBean.MenuItem menuitem = menuItems.get(position);
//
//                String linkType = menuitem.getMenuItemType();
//                String linkSub = menuitem.getMenuItemSub();
//
//                if (!TextUtils.isEmpty(linkType) && (linkType.startsWith("url"))) {
//                    //跳转Url
//                    Intent intent = new Intent(getApplicationContext(), WebMainActivity.class);
//                    intent.putExtra("title", menuitem.getMenuItemName());
//                    intent.putExtra("link", linkSub);
//                    startActivity(intent);
//                } else if (!TextUtils.isEmpty(linkType) && (linkType.startsWith("action"))) {
//                    sendCmd(linkSub);
//                }
//                popupWindow.dismiss();
//            }
//
//        });
//
//        popupWindow = new PopupWindow(popupView, DensityUtil.dip2px(this, 180),
//                DensityUtil.dip2px(this, 204));
//        // 设置动画
//        popupWindow.setAnimationStyle(R.style.popup_window_anim);
//        // 设置背景颜色
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
//        // 设置可以获取焦点
//        popupWindow.setFocusable(true);
//        // 设置可以触摸弹出框以外的区域
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                back.setVisibility(View.VISIBLE);
//            }
//        });
//        // 更新popupwindow的状态
//        popupWindow.update();
//        // 以下拉的方式显示，并且可以设置显示的位置
//        int offsetX = 0;
//        int offsetY = 0;
//
//        if (viewParent.getLeft() + (viewParent.getWidth() / 2) < (popupWindow.getWidth() / 2)) {
//            offsetX += (popupWindow.getWidth() / 2) - (viewParent.getLeft() + (viewParent.getWidth() / 2));
//        } else if (ScreenUtil.getScreenWidth(this) - viewParent.getRight() + (viewParent.getWidth() / 2) < (popupWindow.getWidth() / 2)) {
//            offsetX += -((popupWindow.getWidth() / 2) - (ScreenUtil.getScreenWidth(this) - viewParent.getRight()) - (viewParent.getWidth() / 2));
//        }
//
//        if (viewParent.getTop() + (viewParent.getHeight() / 2) < (popupWindow.getHeight() / 2)) {
//            offsetY += ((popupWindow.getHeight() / 2) - (viewParent.getTop() + (viewParent.getHeight() / 2)));
//        } else if (ScreenUtil.getContentHeight(this) - viewParent.getBottom() + (viewParent.getHeight() / 2) < (popupWindow.getHeight() / 2)) {
//            int xxx = (popupWindow.getHeight() / 2) - (ScreenUtil.getContentHeight(this) - viewParent.getBottom()) - (viewParent.getHeight() / 2);
//            offsetY -= (xxx);
//        }
//
//        offsetX += -(popupWindow.getWidth() / 2 - viewParent.getWidth() / 2);
//        offsetY += -(popupWindow.getHeight() / 2 + viewParent.getHeight() / 2);
//
//        popupWindow.showAsDropDown(viewParent, offsetX, offsetY);
//    }
//
//    /**
//     * 切换企业
//     */
//    protected void changeCompanyAction() {
//        Intent intent = new Intent(this, CompanyGridActivity.class);
//        intent.putExtra("selectCompanyCode", SuneeeApplication.getUser().getSelectCompanyId());
//        intent.putExtra("isGoHomeActivity", true);
//        startActivity(intent);
//        popupWindow.dismiss();
//    }
//
//    /**
//     * 设置小圆点个人信息
//     */
//    private void setCompanyInfo(PointUserInfoBean bean) {
//        if (bean == null) {
//            return;
//        }
//        if (!StringUtils.isEmpty(SuneeeApplication.getUser().getPhoto())) {
//            ImageLoader.getInstance().displayImage(Constants.BASE_HEADIMG_URL + SuneeeApplication.getUser().getPhoto(), imageView);
//        }
//        if (!StringUtils.isEmpty(SuneeeApplication.getUser().getAliasName())) {
//            userNameTv.setText(SuneeeApplication.user.getAliasName() + "");
//        } else {
//            userNameTv.setText(SuneeeApplication.user.getName() + "");
//        }
//        if (!StringUtils.isEmpty(SuneeeApplication.getUser().getSelectCompanyId())) {
//            UserCompanyBean userCompanyBean = DataSupport.where("comp_code = ?", SuneeeApplication.getUser().getSelectCompanyId()).findFirst(UserCompanyBean.class);
//            if (userCompanyBean != null) {
//                companyNameTv.setText(userCompanyBean.getComp_name());
//            }
//        }
//    }
//
////    @Override
////    protected void onNewIntent(Intent intent) {
////        super.onNewIntent(intent);
////        isSetContextView = true;
////        setContentView(appView.getView());
////    }
//
//    PopupWindow servicePopupView;
//
//    private void showServicePopupView() {
//        View view = this.getLayoutInflater().inflate(R.layout.popupwindow_service_layout, null);
//
//        ConfigXmlParser parser = new ConfigXmlParser();
//        parser.parse(this);
//        SystemWebView serviceCordovaWebView = (SystemWebView) view.findViewById(R.id.service_cordova_webview);
//        CordovaWebView cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(serviceCordovaWebView));
//        cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
//        String sessionId = SharedPrefUtils.getSessionId(this);
//        String serviceUrl = Constants.SERVICE_URL + "?sessionId=" + sessionId;
//        LogUtil.e(TAG, "serviceUrl:" + serviceUrl);
//        cordovaWebView.loadUrl(serviceUrl);
//        cordovaWebView.handlePause(true);
//
//        servicePopupView = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
//                ScreenUtil.getScreenHeight(this) / 2);
//        // 设置动画
//        servicePopupView.setAnimationStyle(R.style.popup_window_anim);
//        // 设置背景颜色
//        servicePopupView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
//        // 设置可以获取焦点
//        servicePopupView.setFocusable(true);
//        // 设置可以触摸弹出框以外的区域
//        servicePopupView.setOutsideTouchable(true);
//        // 更新popupwindow的状态
//        servicePopupView.update();
//        // 从底部弹出
//        View rootView = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
//        servicePopupView.showAtLocation(rootView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//
//    }
//
//    public void closeServiceWindow() {
//        if (servicePopupView.isShowing()) {
//            servicePopupView.dismiss();
//        }
//    }
//
//    protected void goWebMainActivity(String link, String title, String tagPlugin) {
//        Intent intent = new Intent(UcpWebViewActivity.this, WebMainActivity.class);
//        intent.putExtra("title", title);
//        intent.putExtra("link", link);
//        intent.putExtra("TagPlugin", tagPlugin);
//        this.startActivity(intent);
//    }
//
//    private void gotoWebActivity(String link, String title) {
//        Intent intent = new Intent(UcpWebViewActivity.this, WebViewActivity.class);
//        intent.putExtra("title", title);
//        intent.putExtra("url", link);
//        startActivity(intent);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        SuneeeApplication.getInstance().removeActivity_(this);
//        //注销广播
////        unregisterReceiver(mBroadcastReceiver);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && isSetContextView) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}