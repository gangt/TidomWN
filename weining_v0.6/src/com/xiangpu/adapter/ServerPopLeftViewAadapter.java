package com.xiangpu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.xiangpu.bean.ServenCompanyBeanTable;
import com.xiangpu.bean.UserCompanyBean;

import java.util.List;

/**
 * description: 七星页左边列表页适配器
 * autour: Andy
 * date: 2018/1/17 16:37
 * update: 2018/1/17
 * version: 1.0
 */
public class ServerPopLeftViewAadapter extends BaseAdapter {

    // 上下文
    private Context context;

    // 数据源
    private List<ServenCompanyBeanTable> list;

    // 选中值
    private String selectedCode;

    public ServerPopLeftViewAadapter(Context context) {
        this.context = context;
    }

    public String getSelectedCode() {
        return selectedCode;
    }

    public void setSelectedCode(String selectedCode) {
        this.selectedCode = selectedCode;
        notifyDataSetInvalidated();
    }


    public void setList(List list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public ServenCompanyBeanTable getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.left_list_item, null);
            holder = new ViewHolder();

            holder.rlLayout = (LinearLayout) convertView.findViewById(R.id.ll_item);
            holder.tvSign = (TextView) convertView.findViewById(R.id.tv_sign);
            holder.tvCompName = (TextView) convertView.findViewById(R.id.tv_company_name);
            holder.ivMore = (ImageView) convertView.findViewById(R.id.image_more);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvCompName.setText(list.get(position).getCompName());

        // 判断是否含有子公司
        if (list.get(position).isHasSubCompany()) { // 有子公司
            holder.ivMore.setVisibility(View.VISIBLE);
        } else { // 无子公司
            holder.ivMore.setVisibility(View.INVISIBLE);
        }

        // 判断是否选中项(通过企业编码判断)
        if (list.get(position).getCompCode().equals(selectedCode)) { // 选中项
            holder.rlLayout.setBackgroundColor(Color.parseColor("#F2F2F2"));
            holder.tvSign.setVisibility(View.VISIBLE);
            holder.tvCompName.setTextColor(Color.parseColor("#B01F24"));
            holder.ivMore.setImageResource(R.drawable.more_image_selected);
        } else { // 非选中项
            holder.rlLayout.setBackgroundColor(Color.parseColor("#F7F7F7"));
            holder.tvSign.setVisibility(View.INVISIBLE);
            holder.tvCompName.setTextColor(Color.parseColor("#545454"));
            holder.ivMore.setImageResource(R.drawable.more_image_normal);
        }

        return convertView;
    }

    class ViewHolder {
        LinearLayout rlLayout;
        TextView tvSign;
        TextView tvCompName;
        ImageView ivMore;
    }

}
