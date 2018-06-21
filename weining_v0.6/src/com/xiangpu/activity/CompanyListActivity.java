//package com.xiangpu.activity;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//
//import com.konecty.rocket.chat.R;
//import com.lssl.activity.MainActivity;
//import com.lssl.activity.SuneeeApplication;
//import com.sina.weibo.sdk.openapi.models.CommentList;
//import com.xiangpu.adapter.CompanySelectListAdapter;
//import com.xiangpu.bean.UserCompanyBean;
//
//import org.litepal.crud.DataSupport;
//
//import java.util.List;
//
///**
// * description: 可切换的企业列表
// * autour: Andy
// * date: 2017/12/2 20:26
// * update: 2017/12/2
// * version: 1.0
// */
//public class CompanyListActivity extends BaseActivity {
//
//    private ListView listView;  // 公司列表
//    private CompanySelectListAdapter adapter; // 公司列表适配器
//
//    private String selectCompanyCode; // 当前显示的公司编码
//    private boolean isGoHomeActivity; // 是否需要跳转到主页
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_company_list);
//
//        selectCompanyCode = getIntent().getStringExtra("selectCompanyCode");
//        isGoHomeActivity = getIntent().getBooleanExtra("isGoHomeActivity", false);
//
//        loadPoint("", "切换");
//
//        initView();
//        initData();
//
//    }
//
//    /**
//     * 初始化UI
//     */
//    private void initView() {
//        listView = (ListView) findViewById(R.id.company_list);
//        adapter = new CompanySelectListAdapter(this);
//        listView.setAdapter(adapter);
//    }
//
//    /**
//     * 初始化数据
//     */
//    private void initData() {
//        final List<UserCompanyBean> userCompanyBeans = DataSupport.where("hasPower = ?", "1").
//                find(UserCompanyBean.class);
//        adapter.setCompanyDetailBeanList(userCompanyBeans);
//        adapter.setSelectorCompanyCode(selectCompanyCode);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                UserCompanyBean bean = adapter.getItem(position);
//                if (isGoHomeActivity) {
//                    Intent intent = new Intent(CompanyListActivity.this, MainActivity.class);
//                    SuneeeApplication.getUser().setSelectCompanyId(userCompanyBeans.get(position).getComp_code());
//                    startActivity(intent);
//                } else {
////                    if (selectCompanyCode.equals(bean.getComp_code())) {
////                        setResult(RESULT_CANCELED);
////                    } else {
//                        Intent intent = new Intent();
//                        intent.putExtra("companyCode", bean.getComp_code());
//                        setResult(RESULT_OK, intent);
////                    }
//                }
//                finish();
//            }
//        });
//    }
//
//}
