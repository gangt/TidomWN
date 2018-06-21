package com.xiangpu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.MainActivity;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.adapter.CompanyGridAdapter;
import com.xiangpu.bean.UserCompanyBean;
import com.xiangpu.utils.DensityUtil;
import com.xiangpu.utils.MapSortUtils;
import com.xiangpu.utils.NumberConvertUtils;
import com.xiangpu.views.FullGridView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 新企业列表页面
 * autour: Andy
 * date: 2018/1/28 17:41
 * update: 2018/1/28
 * version: 1.0
 */
public class CompanyGridActivity extends BaseActivity {

    private LinearLayout layout;

    private Map<String, List<UserCompanyBean>> listMap;

    int level = 0;

    private String selectCompanyCode; // 当前显示的公司编码
    private boolean isGoHomeActivity; // 是否需要跳转到主页

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_layout);

        selectCompanyCode = getIntent().getStringExtra("selectCompanyCode");
        isGoHomeActivity = getIntent().getBooleanExtra("isGoHomeActivity", false);

        loadPoint("", "切换");

        listMap = new HashMap();

        initView();
        initData();

        addListView(listMap);

    }

    private void initView() {
        layout = (LinearLayout) findViewById(R.id.layout);
    }

    private void initData() {
        int code = DataSupport.min(UserCompanyBean.class, "comp_parent_code", int.class);
        setItemView(code, level);
        listMap = MapSortUtils.sortMapByKey(listMap);
    }

    private void setItemView(int code, int level) {
        final List<UserCompanyBean> userCompanyBeans = DataSupport.where("comp_parent_code = ?", code + "").order("comp_id asc").find(UserCompanyBean.class);
        if (userCompanyBeans != null && userCompanyBeans.size() > 0) {
            if (listMap.containsKey(level + "")) {
                listMap.get(level + "").addAll(userCompanyBeans);
            } else {
                listMap.put(level + "", userCompanyBeans);
            }
            level++;
            for (UserCompanyBean bean : userCompanyBeans) {
                setItemView(bean.getComp_id(), level);
            }
        }
    }

    private void addListView(Map<String, List<UserCompanyBean>> listMap) {
        for (Map.Entry<String, List<UserCompanyBean>> entry : listMap.entrySet()) {
            if (entry.getValue() == null || entry.getValue().size() <= 0) {
                continue;
            }
            List<UserCompanyBean> powerList = getPowerCompList(entry.getValue());
            if (powerList == null || powerList.size() <= 0) {
                continue;
            }
            View view = getLayoutInflater().inflate(R.layout.company_grid_layout, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.title);
            FullGridView gridView = (FullGridView) view.findViewById(R.id.company_grid);
            final CompanyGridAdapter adapter = new CompanyGridAdapter(this);
            gridView.setAdapter(adapter);
            adapter.setCompanyDetailBeanList(powerList);
            adapter.setSelectorCompanyCode(selectCompanyCode);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UserCompanyBean bean = adapter.getItem(position);
                    if (isGoHomeActivity) {
                        Intent intent = new Intent(CompanyGridActivity.this, MainActivity.class);
                        SuneeeApplication.getUser().setSelectCompanyId(bean.getComp_code());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("companyCode", bean.getComp_code());
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                }
            });
            if (entry.getKey().equals("0")) {
                tvTitle.setText("集团");
            } else {
                tvTitle.setText(NumberConvertUtils.number2Chinese(Integer.parseInt(entry.getKey()))
                        + "级监管单位");
                if (!entry.getKey().equals("1")) {
                    adapter.setShowIcon(false);
                }
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, DensityUtil.dip2px(this, 8f));
            layout.addView(view, params);
        }
    }

    private List<UserCompanyBean> getPowerCompList(List<UserCompanyBean> list) {
        Log.e("list", list + "");
        if (list == null || list.size() <= 0) {
            return null;
        }
        List<UserCompanyBean> powerList = new ArrayList();
        for (UserCompanyBean bean : list) {
            if (bean.getHasPower() == 1) {
                powerList.add(bean);
            }
        }
        return powerList;
    }

}
