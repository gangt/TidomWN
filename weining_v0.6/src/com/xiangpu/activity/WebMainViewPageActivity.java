package com.xiangpu.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
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
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.WebServiceUtil;
import com.xiangpu.views.TitleHeaderBar;
import com.xiangpu.views.WebScrollViewPager;

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

public class WebMainViewPageActivity extends BaseActivity implements BottomDialogUtil.DialogDataPosition,
        WebServiceUtil.OnDataListener, ViewPager.OnPageChangeListener {

    private static final String TAG = "WebMainViewPageActivity";
    public String TagPlugin = "";

    public final ArrayBlockingQueue<String> onPageFinishedUrl = new ArrayBlockingQueue<String>(5);
    private ArrayList<CommandBean> commandinfoList;

    private CordovaPlugin mPlugin = null;
    private TitleHeaderBar titleBar;
    private String title;
    private TextView tvFefault;


    private WebScrollViewPager mViewPager;
    private List<View> mPagerViews;

    private ConfigXmlParser parser;

    private int jdqRigth = 0;
    private int syzxRight = 0;
    private int skqRigth = 0;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = this.getIntent().getStringExtra("title");

        if ("注册".equals(title)) {
            //解决H5页面软键盘遮挡输入框的问题
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        setContentView(R.layout.activity_webmain_viewpage);
        parser = new ConfigXmlParser();
        parser.parse(this);

        //titleBar = (TitleHeaderBar) findViewById(R.id.titleBar);
        //titleBar.setTitleText(title);
        //titleBar.setVisibility(View.GONE);

        //syzxRight= this.getIntent().getIntExtra("syzx",0);
        //jdqRigth = this.getIntent().getIntExtra("jdq",0);
        //skqRigth = this.getIntent().getIntExtra("skq",0);

        tvFefault = (TextView) findViewById(R.id.network_default);

        initViews();

        String link = this.getIntent().getStringExtra("link");
        TagPlugin = this.getIntent().getStringExtra("TagPlugin");

        View v = null;
        if (TagPlugin.equals(CenterCommandPlugin.TAG)) {
            v = mPagerViews.get(0);
            mViewPager.setCurrentItem(0);
        } else if (TagPlugin.equals(TimeSpacePlugin.TAG)) {
            v = mPagerViews.get(2);
            mViewPager.setCurrentItem(2);
        } else {
            v = mPagerViews.get(1);
            mViewPager.setCurrentItem(1);
        }

//        if(link.equals(SuneeeApplication.configManage.getFunctionUrl("syzx"))){
//            v = mPagerViews.get(0);
//            mViewPager.setCurrentItem(0);
//        }else if(link.equals(SuneeeApplication.configManage.getFunctionUrl("jdq"))){
//            v = mPagerViews.get(1);
//            mViewPager.setCurrentItem(1);
//        }else if(link.equals(SuneeeApplication.configManage.getFunctionUrl("skq"))){
//            v = mPagerViews.get(2);
//            mViewPager.setCurrentItem(2);
//        }

        if (!TextUtils.isEmpty(link)) {
            loadUrl(link);//加载页面
        } else {
            LinearLayout ll_system_webView = (LinearLayout) findViewById(R.id.ll_system_webView);
            ll_system_webView.setVisibility(View.GONE);
            TextView tv_result = (TextView) findViewById(R.id.tv_result);

            tv_result.setText(link);
        }

        if (title.equals("时空中心")) {
            loadPoint("", "时空中心");//加载小圆点
        } else {
            loadPoint("", title);
        }

        initEvent();
        initTouchMoveEvent();
    }

    private void loadUrl(String link) {

        String sessionId = SharedPrefUtils.getSessionId(this);
        String strSessionUrl = link;
        if (!"".equals(sessionId) && !"null".equals(sessionId) && !"0".equals(sessionId)) {
            if (!strSessionUrl.contains("actId")) {
                if (strSessionUrl.contains("?")) {
                    strSessionUrl += "&sessionId=" + sessionId;
                } else {
                    strSessionUrl += "?sessionId=" + sessionId;
                }
            }
        }

        if (link.equals(SuneeeApplication.configManage.getFunctionUrl("syzx"))) {
            if (cordovaWebView1 == null) {
                SystemWebView webView = (SystemWebView) mPagerViews.get(0).findViewById(R.id.cordovaWebView);
                webView.setDownloadListener(new MyWebViewDownLoadListener());
                cordovaWebView1 = new CordovaWebViewImpl(new SystemWebViewEngine(webView));
                cordovaWebView1.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
            }

            cordovaWebView = cordovaWebView1;
            cordovaWebView.loadUrl(strSessionUrl);
        }

        if (link.equals(SuneeeApplication.configManage.getFunctionUrl("jdq"))) {
            if (cordovaWebView2 == null) {
                SystemWebView webView = (SystemWebView) mPagerViews.get(1).findViewById(R.id.cordovaWebView);
                webView.setDownloadListener(new MyWebViewDownLoadListener());
                cordovaWebView2 = new CordovaWebViewImpl(new SystemWebViewEngine(webView));
                cordovaWebView2.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
            }
            cordovaWebView = cordovaWebView2;
            cordovaWebView.loadUrl(strSessionUrl);
        }

        if (link.equals(SuneeeApplication.configManage.getFunctionUrl("skq"))) {
            if (cordovaWebView3 == null) {
                SystemWebView webView = (SystemWebView) mPagerViews.get(2).findViewById(R.id.cordovaWebView);
                webView.setDownloadListener(new MyWebViewDownLoadListener());
                cordovaWebView3 = new CordovaWebViewImpl(new SystemWebViewEngine(webView));
                cordovaWebView3.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());
            }
            cordovaWebView = cordovaWebView3;
            cordovaWebView.loadUrl(strSessionUrl);
        }

        //cordovaWebView = new CordovaWebViewImpl(new SystemWebViewEngine(webView));
        //cordovaWebView.init(cordovaInterface, parser.getPluginEntries(), parser.getPreferences());


    }

    private void initViews() {
        try {
            mViewPager = (WebScrollViewPager) findViewById(R.id.webview_viewpager);

            LayoutInflater inflater = LayoutInflater.from(this);

            mPagerViews = new ArrayList<View>();
            mPagerViews.add(inflater.inflate(R.layout.activity_webmain, null));
            mPagerViews.add(inflater.inflate(R.layout.activity_webmain, null));
            mPagerViews.add(inflater.inflate(R.layout.activity_webmain, null));

            mViewPager.setAdapter(new ViewPageAdapter());
            mViewPager.addOnPageChangeListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    int curSelectPos;

    class ViewPageAdapter extends PagerAdapter {

        @Override
        public void destroyItem(View v, int position, Object arg2) {
            ((ViewPager) v).removeView(mPagerViews.get(position));
        }

        @Override
        public void finishUpdate(View arg0) {

        }

        // 获取当前窗体界面数
        @Override
        public int getCount() {
            return mPagerViews.size();
        }

        // 初始化position位置的界面
        @Override
        public Object instantiateItem(View v, int position) {
            ((ViewPager) v).addView(mPagerViews.get(position));


            switch (position) {
                case 0:
                    //SystemWebView webView1 = (SystemWebView) v.findViewById(R.id.cordovaWebView);
                    String link1 = SuneeeApplication.configManage.getFunctionUrl("syzx");
                    loadUrl(link1);

                    break;
                case 1:
                    //SystemWebView webView2 = (SystemWebView) v.findViewById(R.id.cordovaWebView);
                    String link2 = SuneeeApplication.configManage.getFunctionUrl("jdq");
                    loadUrl(link2);

                    break;
                case 2:
                    //SystemWebView webView3 = (SystemWebView) v.findViewById(R.id.cordovaWebView);
                    String link3 = SuneeeApplication.configManage.getFunctionUrl("skq");
                    loadUrl(link3);
                    break;
            }

            curSelectPos = position;

            return mPagerViews.get(position);
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View v, Object arg1) {
            return v == arg1;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
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

    private void initTouchMoveEvent() {

        final DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = (WindowManager) getSystemService(this.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(dm);

        if (cordovaWebView.getView() != null) {
            cordovaWebView.getView().setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_HOVER_MOVE:
                        case MotionEvent.ACTION_MOVE:
                        case MotionEvent.ACTION_DOWN:

                            int xPoint = (int) event.getX();
                            int yPoint = (int) event.getY();
                            LogUtil.d("xPoint: " + xPoint + " yPoint: " + yPoint);

                            if (yPoint > dm.heightPixels * 3 / 4) {
                                //ToastUtil.show(WebMainViewPageActivity.this,"1/4");
                                if (xPoint > 0 && xPoint < 50 || xPoint > dm.widthPixels - 50 && xPoint < dm.widthPixels) {
                                    ((SystemWebView) cordovaWebView.getView()).requestDisallowInterceptTouchEvent(true);
                                    // ToastUtil.show(WebMainViewPageActivity.this,"0<xPoint <50");
                                } else {
                                    //   ToastUtil.show(WebMainViewPageActivity.this,"xPoint>50 true");
                                    ((SystemWebView) cordovaWebView.getView()).requestDisallowInterceptTouchEvent(false);

                                }
                            } else {

                                // ToastUtil.show(WebMainViewPageActivity.this,"3/4");
                                ((SystemWebView) cordovaWebView.getView()).requestDisallowInterceptTouchEvent(true);
                            }

                            break;
                    }

                    return false;
                }
            });
        }
    }

//    //
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
