package com.xiangpu.activity.usercenter.leftmenu;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konecty.rocket.chat.R;

import java.util.List;

public class LeftMenuAdapter extends BaseAdapter {
    private Context mContext;
    private List<LeftMenuBean> mLists;
    private LayoutInflater mLayoutInflater;
    private int itemSelect = 0;

    public LeftMenuAdapter(Context pContext, List<LeftMenuBean> pLists) {
        this.mContext = pContext;
        this.mLists = pLists;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mLists != null ? mLists.size() : 0;
    }

    @Override
    public Object getItem(int arg0) {
        return mLists.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View view, ViewGroup arg2) {
        Holder holder = null;
        if (null == view) {
            holder = new Holder();
            view = mLayoutInflater.inflate(R.layout.listview_left_menu_item, null);
            holder.ll_item = (LinearLayout) view.findViewById(R.id.ll_item);
            holder.iv_image = (ImageView) view.findViewById(R.id.iv_image);
            holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        holder.iv_image.setImageResource(mLists.get(arg0).getId());
        holder.tv_content.setText(mLists.get(arg0).getName());
        if (arg0 == itemSelect) {
            holder.ll_item.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_e6e6e6));
            holder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.color_1b1b1b));
        } else {
            holder.ll_item.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_f2f2f2));
            holder.tv_content.setTextColor(ContextCompat.getColor(mContext, R.color.txt_color));
        }
        return view;
    }

    public void setItemSelect(int select) {
        this.itemSelect = select;
    }

    private static class Holder {
        LinearLayout ll_item;
        ImageView iv_image;
        TextView tv_content;
    }

}
