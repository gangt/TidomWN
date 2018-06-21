package com.xiangpu.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.bean.CommandBean;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 指挥内容Adapter
 *
 * @author chengang
 */
public class CategoryListAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<CommandBean> itemList;

    public CategoryListAdapter(Context context,
                               ArrayList<CommandBean> item) {
        this.context = context;
        this.itemList = item;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Datalist data = new Datalist();
        convertView = LayoutInflater.from(context).inflate(R.layout.category_item, null);
        data.mNameTextView = (TextView) convertView.findViewById(R.id.name);
        data.mImage = (ImageView) convertView.findViewById(R.id.haschild);
        data.iv_my = (ImageView) convertView.findViewById(R.id.iv_my);

        CommandBean bean = itemList.get(position);
        if (bean != null) {
            String id = "";
            if (SuneeeApplication.getUser().curCommand != null) {

                id = SuneeeApplication.getUser().curCommand.getCommandid();
            }
            final String name = bean.name;

            if (!TextUtils.isEmpty(bean.commandid) && bean.commandid.equals(id)) {
                data.mNameTextView.setTextColor(R.color.red);
                data.mNameTextView.setText(name);
                data.mImage.setVisibility(View.GONE);
            } else {
                data.mNameTextView.setText(name);
                data.mImage.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private class Datalist {
        public TextView mNameTextView;
        public ImageView mImage;
        public ImageView iv_my;
    }


}
