package com.xiangpu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.konecty.rocket.chat.R;

/**
 * Created by Administrator on 2017/12/5 0005.
 * Info：下载进度dialog
 */

public class DownloadProgressDialog extends Dialog {
    private Context context = null;
    private ProgressBar progress_bar = null;
    private TextView tv_load_percent = null;
    private TextView tv_progress = null;

    public DownloadProgressDialog(Context context) {
        this(context, R.style.dialog_style);
    }

    public DownloadProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download_progress);
        findView();
    }

    private void findView() {
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        tv_load_percent = (TextView) findViewById(R.id.tv_load_percent);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
    }

    public void setProgress(int progress) {
        if (progress_bar != null) {
            progress_bar.setProgress(progress);
        }
        if (tv_load_percent != null) {
            tv_load_percent.setText(progress + "%");
        }
        if (tv_progress != null) {
            tv_progress.setText(progress + "");
        }
    }

}
