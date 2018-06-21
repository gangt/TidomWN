package com.xiangpu.activity.person;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.konecty.rocket.chat.R;
import com.lssl.activity.MainActivity;
import com.lssl.activity.SuneeeApplication;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.adapter.CompanyListAdapter;
import com.xiangpu.adapter.ServerPopLeftViewAadapter;
import com.xiangpu.adapter.ServerPopRightViewAadapter;
import com.xiangpu.bean.AuthenticationBean;
import com.xiangpu.bean.CompanyInfo;
import com.xiangpu.bean.IndustryBean;
import com.xiangpu.bean.ServenCompanyBeanTable;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.DensityUtil;
import com.xiangpu.utils.LoadLocalImageUtil;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.ScreenUtil;
import com.xiangpu.utils.SizeUtils;
import com.xiangpu.utils.StringUtils;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.WebServiceUtil;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.lang.reflect.Field;
import java.util.List;

/**
 * description: 七星页
 * autour: Andy
 * date: 2017/12/5 13:39
 * update: 2017/12/5
 * version: 1.0
 */
public class ServerMainLoginActivity extends BaseActivity implements View.OnClickListener, WebServiceUtil.OnDataListener {

    public static final String TAG = ServerMainLoginActivity.class.getSimpleName();
    public boolean isAutoSkip; // 是否自动跳转。默认为自动跳转

    private LinearLayout loginXpLayout; // 集团入口按钮

    private List<IndustryBean> industryBeans;//产业轴集合

    private String innerCompId = "";// 内置集团编码

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //主页配置齐全
                    dismissProgressDialog();
                    Intent intent = new Intent(ServerMainLoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case -1:
                    //主页配置返回失败或配置不全
                    dismissProgressDialog();
                    ToastUtils.showCenterToast(ServerMainLoginActivity.this, msg.obj + "");
                    break;
                default:
                    break;

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upc_splash);
        innerCompId = this.getString(R.string.api_compcode);
        loginXpLayout = (LinearLayout) findViewById(R.id.layout_id_login_xp);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        lp.setMargins(0, 0, 0, DensityUtil.dip2px(this, 122f) - ScreenUtil.getBottomStatusHeight(this) / 2);//4个参数按顺序分别是左上右下
        loginXpLayout.setLayoutParams(lp);

        ImageView ivGroupLogo = (ImageView) findViewById(R.id.iv_group_logo);

        AuthenticationBean bean = DataSupport.where("orgName = ? and version = ? and env = ? ", getString(R.string.api_compcode), getString(R.string.api_version), getString(R.string.api_env)).findFirst(AuthenticationBean.class);
        if (bean != null) {
            String groupLogoUrl = Constants.getConfigLogoPath(bean.getIndexLogoDir());
            ImageLoader.getInstance().displayImage(groupLogoUrl, ivGroupLogo);
        }

        isAutoSkip = getIntent().getBooleanExtra("isAutoSkip", true);
        if (isAutoSkip) {
            /**
             * 根据用户登录返回数据的XXX字段（1,字段为空表示停留 2,字段不为空则返回子企业编码）决定是在七星页停留，还是停留一秒自动跳转
             *
             */
            if (!StringUtils.isEmpty(SuneeeApplication.user.affiliatedCompanyId)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getHomepageConfigInfo(SuneeeApplication.user.affiliatedCompanyId);
                    }
                }, 700);
                return;
            }
        }

        ivGroupLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHomepageConfigInfo(innerCompId);
            }
        });

        TextView txt7Star1 = (TextView) findViewById(R.id.txt_7_star_1);
        txt7Star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubCompany(v, 0);
            }
        });
        TextView txt7Star2 = (TextView) findViewById(R.id.txt_7_star_2);
        txt7Star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubCompany(v, 1);
            }
        });
        TextView txt7Star3 = (TextView) findViewById(R.id.txt_7_star_3);
        txt7Star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubCompany(v, 2);
            }
        });
        TextView txt7Star4 = (TextView) findViewById(R.id.txt_7_star_4);
        txt7Star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubCompany(v, 3);
            }
        });
        TextView txt7Star5 = (TextView) findViewById(R.id.txt_7_star_5);
        txt7Star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubCompany(v, 4);
            }
        });
        TextView txt7Star6 = (TextView) findViewById(R.id.txt_7_star_6);
        txt7Star6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubCompany(v, 5);
            }
        });
        TextView txt7Star7 = (TextView) findViewById(R.id.txt_7_star_7);
        txt7Star7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubCompany(v, 6);
            }
        });

        initNetBroadcastReceiver();
    }

    @Override
    protected void netBrResult(boolean netResult) {
        super.netBrResult(netResult);
        if (netResult) {
            initData();
        }
    }

    private int compId;//集团公司的编号

    private void showSubCompany(View view, int index) {
//        industryBeans = DataSupport.findAll(IndustryBean.class);
//        List<CompanyInfo> companyInfos = DataSupport.select("subCompCode", "popName")
//                .where("IndustryNum=?", index + "")
//                .find(CompanyInfo.class);
//        String LabelName = index < industryBeans.size() ? industryBeans.get(index).getName() : "";
        //如果多级弹框没有显示，就显示弹框

        if (popupWindow == null || !popupWindow.isShowing()) {
            //1.根据compParentCode为0，取到威宁集团对象，获得威宁集团的compId
            //2.根据威宁集团的compId和点击的七星轴的位置获得对应轴下的二级菜单
            List<ServenCompanyBeanTable> servenCompanyBeanTables = DataSupport.select("compId")
                    .where("compParentCode=?", "0")
                    .find(ServenCompanyBeanTable.class);
            if (servenCompanyBeanTables == null || servenCompanyBeanTables.size() <= 0) {
                ToastUtils.showCenterToast(this, getString(R.string.industry_has_no_info));
                return;
            }
            compId = servenCompanyBeanTables.get(0).getCompId();
            //获取二级菜单数据
            servenCompanyBeanTablesTwo = initTwoLevelMenuData(++index);
            //如果该产业轴下没有任何子公司
            if (servenCompanyBeanTablesTwo == null || servenCompanyBeanTablesTwo.size() <= 0) {
                ToastUtils.showCenterToast(this, getString(R.string.industry_has_no_info));
                return;
            }
            showMultiCompanyPopupWindow(view, index, servenCompanyBeanTablesTwo);
        } else {
            //如果已经显示重新获取数据源，刷新数据
            index++;
            servenCompanyBeanTablesTwo = initTwoLevelMenuData(index);
            //如果该产业轴下没有任何子公司
            if (servenCompanyBeanTablesTwo == null || servenCompanyBeanTablesTwo.size() <= 0) {
                ToastUtils.showCenterToast(this, getString(R.string.industry_has_no_info));
                popupWindow.dismiss();
                return;
            }
            int resIdByImageName = LoadLocalImageUtil.getInstance().getResIdByImageName("seven_star_industry_" + index);
            LoadLocalImageUtil.getInstance().displayFromDrawable(resIdByImageName,
                    ivIndustryLogo);
            servenCompanyBeanTablesThree = initThreeLevelMenuData(servenCompanyBeanTablesTwo, 0);
            tabLayout.removeAllTabs();
            initTabLayoutData(servenCompanyBeanTablesTwo);
            leftViewAadapter.setList(servenCompanyBeanTablesThree);
            if (servenCompanyBeanTablesFour != null) {
                servenCompanyBeanTablesFour.clear();
                rightViewAadapter.setList(servenCompanyBeanTablesFour);
            }

        }

    }

    /**
     * 初始化数据
     */
    private void initData() {
        LogUtil.i(TAG, "请求七星页子公司信息");
        industryBeans = DataSupport.select("name").find(IndustryBean.class);
        WebServiceUtil.request(Constants.GET_ALL_COMPANY_BY_INDUSTRY, Constants.REQUEST_TYPE_JSON, this);
    }

    /**
     * 显示七星轴的子公司(以前使用的方法，20180125版本中会弃用)
     *
     * @param viewPrent    轴view
     * @param industryName 轴名
     * @param companyInfos 子公司
     */
    private void showSubCompanyPopupWindow(View viewPrent, String industryName, final List<CompanyInfo> companyInfos) {
        View popupView = this.getLayoutInflater().inflate(R.layout.popupwindow_menu_subcompany_layout, null);
        TextView tv_industry_name = (TextView) popupView.findViewById(R.id.tv_industry_name);
        ListView lv_company = (ListView) popupView.findViewById(R.id.lv_company);
        lv_company.setVerticalScrollBarEnabled(true);
        tv_industry_name.setText(industryName);
        CompanyListAdapter companyListAdapter = new CompanyListAdapter(this, companyInfos);
        lv_company.setAdapter(companyListAdapter);
        lv_company.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String subCompCode = companyInfos.get(position).getSubCompCode();
                getHomepageConfigInfo(subCompCode);
            }
        });
        final PopupWindow popupWindow = new PopupWindow(popupView, DensityUtil.dip2px(this, 77f), DensityUtil.dip2px(this, 104f));
        // 设置动画
        popupWindow.setAnimationStyle(R.style.popup_window_anim);
        // 设置背景颜色
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // 设置可以获取焦点
        popupWindow.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popupWindow.setOutsideTouchable(true);
        // 更新popupwindow的状态
        popupWindow.update();
        popupWindow.showAsDropDown(viewPrent, 0, 20);
    }


    private List<ServenCompanyBeanTable> servenCompanyBeanTablesTwo;//二级菜单数据源
    private List<ServenCompanyBeanTable> servenCompanyBeanTablesThree;//三级菜单数据源
    private List<ServenCompanyBeanTable> servenCompanyBeanTablesFour;//四级菜单数据源

    //多级菜单弹框
    private PopupWindow popupWindow;
    private ImageView ivIndustryLogo;
    private TabLayout tabLayout;
    //右边的列表
    private ListView rightView;
    private ServerPopLeftViewAadapter leftViewAadapter;
    private ServerPopRightViewAadapter rightViewAadapter;


    /**
     * 显示七星轴的子公司
     *
     * @param viewPrent               轴view
     * @param index                   轴的脚标
     * @param servenCompanyBeanTables 子公司
     */
    private void showMultiCompanyPopupWindow(View viewPrent, int index, final List<ServenCompanyBeanTable> servenCompanyBeanTables) {
        View popupView = this.getLayoutInflater().inflate(R.layout.multi_select_layout, null);
        ivIndustryLogo = (ImageView) popupView.findViewById(R.id.iv_industry_logo);
        ImageView close_popup_window = (ImageView) popupView.findViewById(R.id.close_popup_window);

        tabLayout = (TabLayout) popupView.findViewById(R.id.tab_layout);
        int resIdByImageName = LoadLocalImageUtil.getInstance().getResIdByImageName("seven_star_industry_" + index);
        LoadLocalImageUtil.getInstance().displayFromDrawable(resIdByImageName, ivIndustryLogo);
        servenCompanyBeanTablesTwo = servenCompanyBeanTables;

        initTabLayoutData(servenCompanyBeanTablesTwo);

        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);


        //初始化三级菜单数据源
        servenCompanyBeanTablesThree = initThreeLevelMenuData(servenCompanyBeanTablesTwo, 0);


        ListView leftView = (ListView) popupView.findViewById(R.id.view_left);
        leftViewAadapter = new ServerPopLeftViewAadapter(this);
        leftView.setAdapter(leftViewAadapter);

        leftViewAadapter.setList(servenCompanyBeanTablesThree);


        rightView = (ListView) popupView.findViewById(R.id.view_right);
        rightViewAadapter = new ServerPopRightViewAadapter(this);
        rightView.setAdapter(rightViewAadapter);

        //三级菜单条目点击事件
        leftView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                leftViewAadapter.setSelectedCode(leftViewAadapter.getItem(position).getCompCode());
                //如果有子公司就展示四级菜单，反之直接跳转
                if (servenCompanyBeanTablesThree.get(position).isHasSubCompany()) {
                    int compId = servenCompanyBeanTablesThree.get(position).getCompId();
                    servenCompanyBeanTablesFour = initFourLevelMenuData(compId);
                    rightViewAadapter.setList(servenCompanyBeanTablesFour);
                } else {
                    getHomepageConfigInfo(servenCompanyBeanTablesThree.get(position).getCompCode());
                }
            }
        });
        //四级菜单条目点击事件
        rightView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getHomepageConfigInfo(servenCompanyBeanTablesFour.get(position).getCompCode());
            }
        });

        //二级菜单条目点击事件
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                servenCompanyBeanTablesThree = initThreeLevelMenuData(servenCompanyBeanTablesTwo, position);
                leftViewAadapter.setList(servenCompanyBeanTablesThree);
                leftViewAadapter.setSelectedCode("");//初始化选中项
                if (servenCompanyBeanTablesFour != null) {
                    servenCompanyBeanTablesFour.clear();
                }
                rightViewAadapter.setList(servenCompanyBeanTablesFour);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT,
                DensityUtil.dip2px(this, 278f));
        // 设置动画
        popupWindow.setAnimationStyle(R.style.popup_bottom_window_anim);
        // 设置背景颜色
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // 设置可以获取焦点
        popupWindow.setFocusable(false);
        // 设置可以触摸弹出框以外的区域
        popupWindow.setOutsideTouchable(false);
        // 更新popupwindow的状态
        popupWindow.update();
        popupWindow.showAtLocation(viewPrent, Gravity.BOTTOM, 0, 0);

        close_popup_window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void initTabLayoutData(List<ServenCompanyBeanTable> servenCompanyBeanTables) {
        for (int i = 0; i < servenCompanyBeanTables.size(); i++) {

            TabLayout.Tab tab = tabLayout.newTab().setText(servenCompanyBeanTables.get(i).getCompName());

            if (i == 0) {
                tabLayout.addTab(tab, true);
            } else {
                tabLayout.addTab(tab);
            }
        }
        reSetTabLayout(tabLayout, 16);
    }

    public void reSetTabLayout(final TabLayout tabLayout, final int dpHorizontalMarginValue) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    int dp10 = SizeUtils.convertDp2Px(tabLayout.getContext(), dpHorizontalMarginValue);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = dp10;
                        params.rightMargin = dp10;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 获取第四级菜单数据
     *
     * @param compParentCode
     * @return
     */
    private List<ServenCompanyBeanTable> initFourLevelMenuData(int compParentCode) {
        List<ServenCompanyBeanTable> servenCompanyBeanTablesFour = DataSupport.select("compName", "compCode")
                .where("compParentCode = ?", compParentCode + "")
                .order("rank asc")
                .find(ServenCompanyBeanTable.class);
        return servenCompanyBeanTablesFour;
    }

    /**
     * 获取第三级菜单数据
     *
     * @param servenCompanyBeanTables 二级菜单集合
     * @param twoLevelMenuIndex       二级菜单脚标
     * @return
     */
    private List<ServenCompanyBeanTable> initThreeLevelMenuData(List<ServenCompanyBeanTable> servenCompanyBeanTables, int twoLevelMenuIndex) {

        ServenCompanyBeanTable servenCompanyBeanTable = servenCompanyBeanTables.get(twoLevelMenuIndex);
        int compId = servenCompanyBeanTable.getCompId();
        List<ServenCompanyBeanTable> servenCompanyBeanTablesThree = DataSupport
                .where("compParentCode = ?", compId + "")
                .order("rank asc")
                .find(ServenCompanyBeanTable.class);
        if (null != servenCompanyBeanTablesThree && servenCompanyBeanTablesThree.size() > 0) {
            for (ServenCompanyBeanTable companyBeanTable : servenCompanyBeanTablesThree) {
                boolean isHasSubCompany = getIsHasSubCompany(companyBeanTable);
                companyBeanTable.setHasSubCompany(isHasSubCompany);
            }
        }
        if (null != servenCompanyBeanTablesThree) {
            servenCompanyBeanTablesThree.add(0, servenCompanyBeanTable);
        }
        return servenCompanyBeanTablesThree;
    }

    /**
     * 获取第二级菜单数据
     *
     * @param oneLevelMenuIndex 产业轴的脚标，从1开始
     * @return
     */
    private List<ServenCompanyBeanTable> initTwoLevelMenuData(int oneLevelMenuIndex) {
        List<ServenCompanyBeanTable> servenCompanyBeanTables = DataSupport
                .where("compParentCode = ? and industryId = ?", compId + "", oneLevelMenuIndex + "")
                .order("rank asc")
                .find(ServenCompanyBeanTable.class);
        return servenCompanyBeanTables;
    }

    /**
     * 判断该公司下是否有子公司
     *
     * @param companyBeanTable
     * @return
     */
    private boolean getIsHasSubCompany(ServenCompanyBeanTable companyBeanTable) {
        int compId = companyBeanTable.getCompId();
        List<ServenCompanyBeanTable> servenCompanyBeanTables = DataSupport.select()
                .where("compParentCode = ?", compId + "")
                .find(ServenCompanyBeanTable.class);
        if (servenCompanyBeanTables != null && servenCompanyBeanTables.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取主页配置信息
     */
    private void getHomepageConfigInfo(String companyId) {
        SuneeeApplication.getUser().setSelectCompanyId(companyId);
        //先验证一下该公司的主页配置是否齐全
        showProgressDialog();
        SuneeeApplication.configManage.loadComplayConfig(true, SuneeeApplication.weilianType,
                SuneeeApplication.getUser().getSelectCompanyId(), handler);
    }

    long clickTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if ((System.currentTimeMillis() - clickTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再次返回退出应用", Toast.LENGTH_SHORT).show();
                clickTime = System.currentTimeMillis();
                return false;
            } else {
                int currentVersion = android.os.Build.VERSION.SDK_INT;
                if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {  //android 版本高于2.2
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    ServerMainLoginActivity.this.finish();
                    SuneeeApplication.getInstance().removeALLActivity_();
                } else {    //android版本低于2.2，android 2.2之后，restartPackage()不可以强制将整个APP退出。
                    ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    am.restartPackage(getPackageName());
                }
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        LogUtil.i(TAG, "七星页公司配置:" + result);
        if (result == null) {
            return;
        }
        if (mode.equals(Constants.GET_ALL_COMPANY_BY_INDUSTRY)) {
            try {
                int code = result.getInt("code");
                if (code == 1) {
                    JSONArray datas = result.getJSONArray("data");
                    DataSupport.deleteAll(ServenCompanyBeanTable.class);
                    if (datas != null) {
                        for (int i = 0; i < datas.length(); i++) {
                            ServenCompanyBeanTable beanTable = new Gson().fromJson(datas.get(i).toString(), ServenCompanyBeanTable.class);
                            beanTable.save();
                        }
                    }

                } else {
                    Log.e("error", result.getString("msg"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (mode.equals(Constants.GET_COMPANY_TREE)) {
            LogUtil.i(TAG, "company tree:" + result.toString());
            try {
                int code = result.getInt("code");
                if (code == 1) {

                } else {
                    Log.e("error", result.getString("msg"));
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

    @Override
    public String onGetParamDataString(String mode) {
        if (mode != null && mode.equals(Constants.GET_ALL_COMPANY_BY_INDUSTRY)) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("orgName", this.getString(R.string.api_compcode));
                jsonObject.put("env", this.getString(R.string.api_env));
                jsonObject.put("version", this.getString(R.string.api_version));
                return jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (mode != null && mode.equals(Constants.GET_COMPANY_TREE)) {
            JSONObject jsonObject = new JSONObject();
            return jsonObject.toString();
        }
        return null;
    }

}
