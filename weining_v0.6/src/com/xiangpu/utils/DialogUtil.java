package com.xiangpu.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.konecty.rocket.chat.R;

public class DialogUtil {

    public static final int PHOTO = 0;
    public static final int CAPTURE = 1;
    private static PopupWindow window = null;
    private static View popupView;

    private static Button cusPopupBtn = null;
    private static Button diaCusPopupPic = null;
    private static Button cancel = null;
    private static TextView cusPopupText = null;
    private static DialogUiAdpter uiadpter = null;

    private static Button previewImage;

    private static ListView mListView;


    public interface DialogUiAdpter {
        void receiveData(int status);
    }

    public static void setDialogUiAdpter(DialogUiAdpter ui) {
        uiadpter = ui;
    }

    public static AlertDialog dialog;

    public static void showDialog(Context context, String title, String text, String cancleText) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        View view = View.inflate(context, R.layout.dialog_fingerprint, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(false);
        TextView tv_dialog_title = (TextView) view.findViewById(R.id.tv_dialog_title);
        TextView tv_dialog_text = (TextView) view.findViewById(R.id.tv_dialog_text);
        TextView tv_ok = (TextView) view.findViewById(R.id.tv_ok);
        tv_dialog_title.setText(title);
        tv_dialog_text.setText(text);
        tv_ok.setText(cancleText);
        tv_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void dismissDialog() {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    public static void showVoiceSearch(final Context context, View v) {
        popupView = LayoutInflater.from(context).inflate(R.layout.activity_audiosearch, null);
        previewImage = (Button) popupView.findViewById(R.id.iat_recognize);
        window = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        window.setFocusable(true);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.update();
        window.showAtLocation(v, Gravity.BOTTOM, 0, 40);
        window.update();

        previewImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DialogUtil.window.dismiss();
                window = null;
            }
        });
    }


    public static void showCusPopUp(final Context context, View v, String firstLine, String secondLine) {

        popupView = LayoutInflater.from(context).inflate(R.layout.dia_cuspopup_dia, null);


        LinearLayout popLayout = (LinearLayout) popupView.findViewById(R.id.pop_layout);
        diaCusPopupPic = (Button) popupView.findViewById(R.id.diaCusPopupPic);
        cusPopupBtn = (Button) popupView.findViewById(R.id.diaCusPopupCapture);
        diaCusPopupPic.setText(firstLine);
        cusPopupBtn.setText(secondLine);

        cancel = (Button) popupView.findViewById(R.id.diaCusPopupCancel);
        window = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        window.setFocusable(true);
        window.setOutsideTouchable(true);
        window.setBackgroundDrawable(new BitmapDrawable());
        //防止被底部虚拟键挡住
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        window.update();

        diaCusPopupPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DialogUtil.window.dismiss();
                window = null;

                if (uiadpter != null) {
                    uiadpter.receiveData(PHOTO);
                }
            }

        });

        cusPopupBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DialogUtil.window.dismiss();
                window = null;

                if (uiadpter != null) {
                    uiadpter.receiveData(CAPTURE);
                }
            }

        });


        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogUtil.window.dismiss();
            }
        });

        popLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.window.dismiss();
            }
        });

    }

}
