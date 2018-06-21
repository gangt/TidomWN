package com.xiangpu.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.xiangpu.bean.MenuBean;
import com.xiangpu.bean.MenuBean.MenuItem;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 点开小黄点的菜单的适配器
 *
 * @author huangda
 */
public class SuspendMenuAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<MenuBean.MenuItem> menuItems;

    public SuspendMenuAdapter(Context c, List<MenuBean.MenuItem> menuItems) {
        mInflater = LayoutInflater.from(c);
        this.menuItems = menuItems;
    }

    @Override
    public int getCount() {
        return menuItems == null ? 0 : menuItems.size();
    }

    @Override
    public Object getItem(int position) {
        return menuItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        MenuItem menuItem = menuItems.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_popupwindow_menu, null);

            viewHolder = new ViewHolder();
            viewHolder.tvimage = (ImageView) convertView.findViewById(R.id.iv_popupwindow_menu);
            viewHolder.tvname = (TextView) convertView.findViewById(R.id.iv_popupwindow_menu_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        /**
         * 根据图片名字取图片
         */
        Class drawable = R.drawable.class;
        Field field = null;
        try {
            viewHolder.tvname.setText(menuItem.getMenuItemName());

            String imageName = "menu_item_" + menuItem.getMenuItemIconName().toLowerCase();
            field = drawable.getField(imageName);
            if (field != null) {
                int res_ID = field.getInt(field.getName());
                viewHolder.tvimage.setImageResource(res_ID);
            }

        } catch (Exception e) {
            Log.e("imageName", "未找到对应小圆点图标");
        }
        return convertView;
    }

    static class ViewHolder {
        public ImageView tvimage;
        public TextView tvname;
    }
}
