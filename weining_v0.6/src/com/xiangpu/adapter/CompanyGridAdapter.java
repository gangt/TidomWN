package com.xiangpu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.xiangpu.bean.UserCompanyBean;
import com.xiangpu.utils.LoadLocalImageUtil;

import java.util.List;

/**
 * description: $todo$
 * autour: Andy
 * date: 2018/1/29 16:29
 * update: 2018/1/29
 * version: 1.0
 */
public class CompanyGridAdapter extends BaseAdapter {

    private Context context;  // 上下文

    private List<UserCompanyBean> userCompanyBeans; // 数据源

    private String selectorCompanyCode; // 已选企业编码

    private boolean isShowIcon = true;

    public CompanyGridAdapter(Context context) {
        this.context = context;
    }

    public List<UserCompanyBean> getCompanyDetailBeanList() {
        return userCompanyBeans;
    }

    public void setCompanyDetailBeanList(List<UserCompanyBean> userCompanyBeanList) {
        this.userCompanyBeans = userCompanyBeanList;
    }

    public void setSelectorCompanyCode(String selectorCompanyCode) {
        this.selectorCompanyCode = selectorCompanyCode;
    }

    public boolean isShowIcon() {
        return isShowIcon;
    }

    public void setShowIcon(boolean showIcon) {
        isShowIcon = showIcon;
    }

    @Override
    public int getCount() {
        return userCompanyBeans == null ? 0 : userCompanyBeans.size();
    }

    @Override
    public UserCompanyBean getItem(int position) {
        return userCompanyBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_company_grid_item, null);
            viewHolder = new ViewHolder();

            viewHolder.logoImg = (ImageView) convertView.findViewById(R.id.logo_img);
            viewHolder.compTv = (TextView) convertView.findViewById(R.id.comp_tv);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserCompanyBean companyDetailBean = userCompanyBeans.get(position);

        if (companyDetailBean.getComp_code().equals(selectorCompanyCode)) {
            viewHolder.compTv.setTextColor(Color.parseColor("#D70C19"));
        } else {
            viewHolder.compTv.setTextColor(Color.parseColor("#231815"));
        }

        viewHolder.compTv.setText(companyDetailBean.getComp_name());

        if (!isShowIcon) {
            viewHolder.logoImg.setVisibility(View.GONE);
        } else {
            viewHolder.logoImg.setVisibility(View.VISIBLE);
            String imgName = "icon_" + companyDetailBean.getComp_code().toLowerCase();
            LoadLocalImageUtil.getInstance().displayFromDrawable(imgName, viewHolder.logoImg);
        }

        return convertView;
    }

    class ViewHolder {
        ImageView logoImg;
        TextView compTv;
    }
}
