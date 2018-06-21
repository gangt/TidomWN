package com.xiangpu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.xiangpu.bean.ServenCompanyBeanTable;
import com.xiangpu.bean.UserCompanyBean;

import java.util.List;

/**
 * description: 七星页右边列表页适配器
 * autour: Andy
 * date: 2018/1/18 9:37
 * update: 2018/1/18
 * version: 1.0
 */
public class ServerPopRightViewAadapter extends BaseAdapter {

    // 上下文
    private Context context;

    // 数据源
    private List<ServenCompanyBeanTable> list;

    public ServerPopRightViewAadapter(Context context) {
        this.context = context;
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
    public Object getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.right_list_item, null);
            holder = new ViewHolder();
            holder.tvCompName = (TextView) convertView.findViewById(R.id.tv_company_name);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvCompName.setText(list.get(position).getCompName());

        return convertView;
    }

    class ViewHolder {
        TextView tvCompName;
    }

}
