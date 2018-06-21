package com.xiangpu.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.bean.CommandBean;
import com.xiangpu.bean.MenuBean;
import com.xiangpu.plugin.CenterCommandPlugin;
import com.xiangpu.plugin.TimeSpacePlugin;
import com.xiangpu.plugin.UCPCommandPlugin;
import com.xiangpu.utils.BottomDialogUtil;
import com.xiangpu.utils.FileUtils;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.WebServiceUtil;
import com.xiangpu.views.TitleHeaderBar;

import org.apache.cordova.ConfigXmlParser;
import org.apache.cordova.CordovaInterfaceImpl;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewImpl;
import org.apache.cordova.LOG;
import org.apache.cordova.engine.SystemWebView;
import org.apache.cordova.engine.SystemWebViewEngine;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

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

public class WebMainActivity extends BaseActivity implements BottomDialogUtil.DialogDataPosition,
        WebServiceUtil.OnDataListener {

    private static final String TAG = "WebMainActivity";
    public String TagPlugin = "";

    public final ArrayBlockingQueue<String> onPageFinishedUrl = new ArrayBlockingQueue<String>(5);
    private ArrayList<CommandBean> commandinfoList;

    private CordovaPlugin mPlugin = null;
    private TitleHeaderBar titleBar;
    private String title;
    private TextView tvFefault;

    protected CordovaInterfaceImpl cordovaInterface = new CordovaInterfaceImpl(this) {
        @Override
        public Object onMessage(String id, Object data) {
            if ("onPageStarted".equals(id)) {
            }
            if ("onPageFinished".equals(id)) {
                tvFefault.setVisibility(View.GONE);
            }
            if ("onReceivedError".equals(id)) {
                final JSONObject object = (JSONObject) data;
                tvFefault.setVisibility(View.VISIBLE);
                tvFefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            cordovaWebView.loadUrl(object.getString("url"));
                            tvFefault.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            return super.onMessage(id, data);
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = this.getIntent().getStringExtra("title");
        if ("注册".equals(title) || "粒子链".equals(title) || "学习中心".equals(title)) {
            //解决H5页面软键盘遮挡输入框的问题
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        setContentView(R.layout.activity_webmain);

        ConfigXmlParser parser = new ConfigXmlParser();
        parser.parse(this);

        String link = this.getIntent().getStringExtra("link");
        TagPlugin = this.getIntent().getStringExtra("TagPlugin");

        titleBar = (TitleHeaderBar) findViewById(R.id.titleBar);
        titleBar.setTitleText(title);
        titleBar.setVisibility(View.GONE);
        SystemWebView webView = (SystemWebView) findViewById(R.id.cordovaWebView);
        webView.setDownloadListener(new MyWebViewDownLoadListener());
        tvFefault = (TextView) findViewById(R.id.network_default);

        if (!TextUtils.isEmpty(link)) {
            //&& (link.startsWith("http://") || link.startsWith("https://"))) {

            cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(webView));
            cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());

            String sessionId = SharedPrefUtils.getSessionId(this);
            String strSessionUrl = link;
            if (!StringUtils.isEmpty(sessionId) && !"0".equals(sessionId)) {
                if (!strSessionUrl.contains("actId")) {
                    if (strSessionUrl.contains("?")) {
                        strSessionUrl += "&sessionId=" + sessionId;
                    } else {
                        strSessionUrl += "?sessionId=" + sessionId;
                    }
                }
            }
            Log.e("strSessionUrl", strSessionUrl + "");
            cordovaWebView.loadUrl(strSessionUrl);
            cordovaWebView.handlePause(true);
        } else {
            LinearLayout ll_system_webView = (LinearLayout) findViewById(R.id.ll_system_webView);
            ll_system_webView.setVisibility(View.GONE);
            TextView tv_result = (TextView) findViewById(R.id.tv_result);

            tv_result.setText(link);
        }

//        if (title.equals("指挥中心")) {
//            loadPoint("CommandCenter", Constants.COMMANDCENTER_TITLE);//加载小圆点
//            WebServiceUtil.request(Constants.moduleIsCreateCommandByAuth, Constants.REQUEST_TYPE_JSON, this);
//        } else
        if (title.equals("时空中心")) {
            loadPoint("", "时空中心");//加载小圆点
        } else if (title.equals("聚能器")) {
            loadPoint("JuNengQi", "聚能器");//加载小圆点
        } else if (title.equals("赋能器")) {
            loadPoint("FuNengQi", "赋能器");//加载小圆点
        } else if (title.equals("场景观")) {
            loadPoint("", "场景观");//加载小圆点
        } else if (title.equals("象谱商城")) {
//            loadPoint("ShoppingMall", title);
            loadPoint("", title);
//        } else if (title.equals("运营中心")) {
//            loadPoint("ShoppingMall", title);
        } else if (title.equals("注册")) {

        } else if (link.equals("http://ecmp.weilian.cn/wap/member/center.html")) {
            loadPoint("JuNengQi", title);
        } else if (link.equals("http://ecmp.weilian.cn/wap/category.html")) {
            loadPoint("JuNengQi", title);
        } else if (link.equals("http://scn.vr.weilian.cn:8111/sale/H5/mobile/purchase_order_list.html")) {
            loadPoint("JuNengQi", title);
        } else if (link.equals("http://scn.vr.weilian.cn:8111/sale/H5/mobile/vendor_stock_list.html")) {
            loadPoint("JuNengQi", title);
        } else if (link.equals("http://scn.vr.weilian.cn:8111/sale/H5/mobile/retail_order.html")) {
            loadPoint("JuNengQi", title);
        } else {
            loadPoint("", title);
        }

        if ("官网".equals(title) || "象谱商城".equals(title)) {
            // 只有官网和象谱商城不显示企业
            setShowCompany(false);
        }
        initEvent();
    }

    private void initEvent() {
        if (cordovaWebView != null && cordovaWebView.getPluginManager() != null) {
            CordovaPlugin plugin = cordovaWebView.getPluginManager().getPlugin("TimeSpacePlugin");
            if (plugin != null && plugin instanceof TimeSpacePlugin) {
//                ((TimeSpacePlugin) plugin).setListener(this);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ("TimeSpacePlugin".equals(TagPlugin)) {
            mPlugin = cordovaWebView.getPluginManager().getPlugin("TimeSpacePlugin");
            if (mPlugin != null) {
                ((TimeSpacePlugin) mPlugin).AndroidCommand("closeAudio");
            }
        }
    }

    public void sendCmdPlugin(String tag, String cmd) {
        if (tag.equals("TimeSpacePlugin")) {
            mPlugin = cordovaWebView.getPluginManager().getPlugin("TimeSpacePlugin");
            if (mPlugin != null) {
                ((TimeSpacePlugin) mPlugin).AndroidCommand(cmd);
            }
        } else if (tag.equals("CenterCommandPlugin")) {
            mPlugin = cordovaWebView.getPluginManager().getPlugin("CenterCommandPlugin");
            if (mPlugin != null) {
                ((CenterCommandPlugin) mPlugin).AndroidChangeCommand(cmd);
            }
        } else if (tag.equals("UCPCommandPlugin")) {
            mPlugin = cordovaWebView.getPluginManager().getPlugin("UCPCommandPlugin");
            if (mPlugin != null) {
                ((UCPCommandPlugin) mPlugin).AndroidUcpCommand(cmd);
            }
        }
    }

    @Override
    protected void sendMenuCmd(String cmd) {
        if ("TimeSpacePlugin".equals(TagPlugin)) {
            sendCmdPlugin("TimeSpacePlugin", cmd);
        } else if ("CenterCommandPlugin".equals(TagPlugin)) {
            sendCmdPlugin("CenterCommandPlugin", cmd);
        } else if ("UCPCommandPlugin".equals(TagPlugin)) {
            sendCmdPlugin("UCPCommandPlugin", cmd);
        }
    }

    @Override
    protected ArrayList<MenuBean.MenuItem> loadMenuItems() {
        if ("UCPCommandPlugin".equals(TagPlugin)) {
            mPlugin = cordovaWebView.getPluginManager().getPlugin("UCPCommandPlugin");
            if (mPlugin != null) {
                JSONObject jsonMenu = ((UCPCommandPlugin) mPlugin).getLoadMenu();

                try {
                    return LoadParamMenu(jsonMenu);
                } catch (JSONException e) {

                }
            }
        }
        return null;
    }

    private ArrayList<MenuBean.MenuItem> LoadParamMenu(JSONObject result) throws JSONException {

        if (result == null) {
            return null;
        }

        ArrayList<MenuBean.MenuItem> menuItems = null;

        String name = result.getString("name");
        String type = result.getString("type");
        JSONArray buttons = result.getJSONArray("buttons");

        if (menuItems == null) {
            menuItems = new ArrayList<MenuBean.MenuItem>();

            for (int i = 0; i < buttons.length(); i++) {

                JSONObject jsonObject2 = buttons.getJSONObject(i);

                MenuBean.MenuItem menuItem = new MenuBean().new MenuItem();
                menuItem.setMenuItemName(jsonObject2.getString("title"));
                menuItem.setMenuItemIconName(jsonObject2.getString("icon"));

                String strType = jsonObject2.getString("type");
                menuItem.setMenuItemType(strType);
                menuItem.setMenuItemSub(strType, jsonObject2.getString("sub"));

                menuItems.add(menuItem);
            }
        }
        return menuItems;
    }

    @Override
    protected void onDestroy() {
        LogUtil.e(TAG, TAG + "onDestroy");
        super.onDestroy();
        // 退出时释放连接,把下面两行代码注释掉后点击返回就不会卡死了
    }

    @Override
    protected void showCommandList() {
        if (commandinfoList != null && commandinfoList.size() > 0) {
            BottomDialogUtil.setDialogUiAdpter(this);

            ArrayList<CommandBean> items = new ArrayList<>();
            for (int i = 0; i < commandinfoList.size(); i++) {
                items.add(commandinfoList.get(i));
            }
            BottomDialogUtil.showBottomDialog(this, getWindow().getDecorView(), "指挥列表", items);
        }
    }


    @Override
    public void onDialogResultData(int position) {
        try {

            CommandBean bean = commandinfoList.get(position);

            if (bean != null && !TextUtils.isEmpty(bean.commandid)) {

                SuneeeApplication.user.curCommand = bean;

                String currentCommandName = bean.getName();
                String currentCommandId = bean.getCommandid();

                JSONObject json = new JSONObject();
                json.put("compId", SuneeeApplication.user.compId);
                json.put("commandId", currentCommandId);
                json.put("userId", SharedPrefUtils.getUserId(this));

                sendMenuCmd(json.toString());

            } else {
                ToastUtils.showCenterToast(this, "指挥ID无效");
            }

        } catch (Exception e) {
        }
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        if (result == null) {
            return;
        }
    }

    @Override
    public List<NameValuePair> onGetParamData(String mode) {
        return null;
    }

    @Override
    public String onGetParamDataString(String mode) {
        return null;
    }

    class JsInteration {

        @JavascriptInterface
        public void toastMessage(String message) {
            // Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }

        @JavascriptInterface
        public void onSumResult(int result) {
            //Log.i(LOGTAG, "onSumResult result=" + result);
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
//            Uri uri = Uri.parse(url);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);

        }

    }

    public CordovaWebView getCordovaWebView() {
        return cordovaWebView;
    }


//    @SuppressLint("NewApi")
//    @Override
//    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
//        // Capture requestCode here so that it is captured in the setActivityResultCallback() case.
//        cordovaInterface.setActivityResultRequestCode(requestCode);
//        super.startActivityForResult(intent, requestCode, options);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        LOG.d(TAG, "Incoming Result. Request code = " + requestCode);
//        super.onActivityResult(requestCode, resultCode, intent);
//        cordovaInterface.onActivityResult(requestCode, resultCode, intent);
//    }

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
