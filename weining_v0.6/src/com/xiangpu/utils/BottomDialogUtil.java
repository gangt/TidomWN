package com.xiangpu.utils;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.xiangpu.adapter.CategoryListAdapter;
import com.xiangpu.bean.CommandBean;

import java.util.ArrayList;

public class BottomDialogUtil {
    private static PopupWindow window = null;
    private static View popupView;
    private static TextView diaCusPopupPic = null;
    private static ListView lv_command_list;
    private static TextView cancel = null;
    private static DialogDataPosition dialogDataPosition = null;
    private static MenuItemClickListener mListener;

    public interface DialogDataPosition {
        void onDialogResultData(int position);
    }

    public static void setDialogUiAdpter(DialogDataPosition dialogDataPosition) {
        BottomDialogUtil.dialogDataPosition = dialogDataPosition;
    }


    public static void showBottomDialog(final Context context, View v, String title, ArrayList<CommandBean> items) {
        popupView = LayoutInflater.from(context).inflate(R.layout.dialog_bottom_list_1, null);
        diaCusPopupPic = (TextView) popupView.findViewById(R.id.tv_bottom_dialog_title);
        lv_command_list = (ListView) popupView.findViewById(R.id.lv_command_list);
        diaCusPopupPic.setText(title);

        cancel = (TextView) popupView.findViewById(R.id.tv_cancel);
        CategoryListAdapter categoryListAdapter = new CategoryListAdapter(context, items);
        lv_command_list.setAdapter(categoryListAdapter);
        window = new PopupWindow(popupView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

        // window.setAnimationStyle(R.style.PopupAnimation);
        window.setFocusable(true);

        window.setBackgroundDrawable(new BitmapDrawable());
        window.update();
        window.showAtLocation(v, Gravity.BOTTOM, 0, 40);
        window.update();

        lv_command_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BottomDialogUtil.window.dismiss();
                window = null;
                if (dialogDataPosition != null) {
                    dialogDataPosition.onDialogResultData(position);
                }
                initClick(position);
            }
        });

        BottomDialogUtil.window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BottomDialogUtil.window.dismiss();
            }
        });
    }

    public interface MenuItemClickListener {
        void onItemClick(int itemPosition);
    }

    public BottomDialogUtil setItemClickListener(MenuItemClickListener listener) {
        this.mListener = listener;
        return this;
    }

    private static void initClick(int position) {
        if (mListener != null) {
            mListener.onItemClick(position);
        }
    }

}
