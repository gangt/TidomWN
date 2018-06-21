package com.xiangpu.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.bean.CommandBean;
import com.xiangpu.bean.CompanyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 七星页子公司列表Adapter
 *
 * @author huangda
 */
public class CompanyListAdapter extends BaseAdapter {

    private Context context;
    private List<CompanyInfo> companyInfos;

    public CompanyListAdapter(Context context, List<CompanyInfo> companyInfos) {
        this.context = context;
        this.companyInfos = companyInfos;
    }

    @Override
    public int getCount() {
        return companyInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return companyInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        CompanyInfo companyInfo = companyInfos.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.sevenstar_company_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tv_company = (TextView) convertView.findViewById(R.id.tv_company);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_company.setText(companyInfo.getPopName());

        return convertView;
    }

    private class ViewHolder {
        public TextView tv_company;
    }


}
