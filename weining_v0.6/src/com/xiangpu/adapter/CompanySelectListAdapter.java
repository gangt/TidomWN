package com.xiangpu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.xiangpu.bean.UserCompanyBean;
import com.xiangpu.utils.LoadLocalImageUtil;

import java.util.List;

/**
 * description: 企业选择适配器
 * autour: Andy
 * date: 2017/12/2 20:41
 * update: 2017/12/2
 * version: 1.0
 */
public class CompanySelectListAdapter extends BaseAdapter {

    private Context context;  // 上下文

    private List<UserCompanyBean> userCompanyBeans; // 数据源

    private String selectorCompanyCode; // 已选企业编码

    public CompanySelectListAdapter(Context context) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.user_company_list_item, null);
            viewHolder = new ViewHolder();

            viewHolder.topTv = (TextView) convertView.findViewById(R.id.line_top);
            viewHolder.stateImg = (ImageView) convertView.findViewById(R.id.point_state);
            viewHolder.bottomTv = (TextView) convertView.findViewById(R.id.line_bottom);
            viewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.company_list_item_layout);
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.company_icon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        UserCompanyBean companyDetailBean = userCompanyBeans.get(position);
        if (getCount() > 1) {
            if (position == 0) {
                viewHolder.topTv.setVisibility(View.INVISIBLE);
                viewHolder.bottomTv.setVisibility(View.VISIBLE);
            } else if (position == getCount() - 1) {
                viewHolder.topTv.setVisibility(View.VISIBLE);
                viewHolder.bottomTv.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.topTv.setVisibility(View.VISIBLE);
                viewHolder.bottomTv.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.topTv.setVisibility(View.INVISIBLE);
            viewHolder.bottomTv.setVisibility(View.INVISIBLE);
        }

        if (companyDetailBean.getComp_code().equals(selectorCompanyCode)) {
            viewHolder.stateImg.setImageResource(R.drawable.company_item_bg_selector);
            viewHolder.itemLayout.setBackgroundResource(R.drawable.company_list_bg_selector);
        } else {
            viewHolder.stateImg.setImageResource(R.drawable.company_item_bg_normal);
            viewHolder.itemLayout.setBackgroundResource(R.drawable.company_list_bg_normal);
        }

        LoadLocalImageUtil.getInstance().dispalyFromAssets("logo/" + userCompanyBeans.get(position).getComp_code() + ".png",
                viewHolder.imageView);

        return convertView;
    }

    class ViewHolder {
        TextView topTv;
        ImageView stateImg;
        TextView bottomTv;
        LinearLayout itemLayout;
        ImageView imageView;
    }

}
