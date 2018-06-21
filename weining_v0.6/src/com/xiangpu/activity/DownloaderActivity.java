package com.xiangpu.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.konecty.rocket.chat.R;
import com.xiangpu.plugin.OpenUrlPlugin;
import com.xiangpu.utils.FileSizeUtils;
import com.xiangpu.utils.OpenWays;
import com.xiangpu.utils.PermissionUtils;

import org.wlf.filedownloader.DownloadFileInfo;
import org.wlf.filedownloader.FileDownloader;
import org.wlf.filedownloader.base.Status;
import org.wlf.filedownloader.listener.OnDeleteDownloadFileListener;
import org.wlf.filedownloader.listener.OnDownloadFileChangeListener;
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener;
import org.wlf.filedownloader.listener.simple.OnSimpleFileDownloadStatusListener;

import java.io.File;


/**
 * description: 附件下载界面
 * autour: Andy
 * date: 2017/12/11 16:55
 * update: 2017/12/11
 * version: 1.0
 */
public class DownloaderActivity extends BaseActivity {

    private ImageButton backBtn;
    private TextView titleTv;
    private TextView nameTv;
    private TextView sizeTv;
    private LinearLayout stateLl;
    private TextView stateTv;
    private Button downBtn;
    private ProgressBar statePb;
    private Button openBtn;

    private String name;
    private String url;
    private String size;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_layout);

        OpenUrlPlugin.isActivityOpened = false;

        name = getIntent().getStringExtra("name");
        url = getIntent().getStringExtra("url");
        size = getIntent().getStringExtra("size");
        type = getIntent().getStringExtra("type");

        backBtn = (ImageButton) findViewById(R.id.back);
        titleTv = (TextView) findViewById(R.id.title);
        nameTv = (TextView) findViewById(R.id.file_name);
        sizeTv = (TextView) findViewById(R.id.file_size);
        downBtn = (Button) findViewById(R.id.btn_download);
        stateLl = (LinearLayout) findViewById(R.id.file_status);
        stateTv = (TextView) findViewById(R.id.tv_status);
        statePb = (ProgressBar) findViewById(R.id.progressBar);
        openBtn = (Button) findViewById(R.id.btn_open);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloaderActivity.this.finish();
            }
        });
        titleTv.setText(name);
        nameTv.setText(name);
        sizeTv.setText("文件大小：" + FileSizeUtils.formatSize(this, size));

        loadPoint("filedown", "附件下载");

        DownloadFileInfo fileInfo = FileDownloader.getDownloadFile(url);
        if (fileInfo == null) {
            downBtn.setVisibility(View.VISIBLE);
            stateLl.setVisibility(View.GONE);
            openBtn.setVisibility(View.GONE);
        } else if (fileInfo.getStatus() == Status.DOWNLOAD_STATUS_COMPLETED) {
            downBtn.setVisibility(View.GONE);
            stateLl.setVisibility(View.VISIBLE);
            stateTv.setText("下载完成");
            statePb.setProgress(100);
            openBtn.setVisibility(View.VISIBLE);
        } else {
            downBtn.setVisibility(View.VISIBLE);
            stateLl.setVisibility(View.GONE);
            openBtn.setVisibility(View.GONE);
            FileDownloader.delete(url, true, new OnDeleteDownloadFileListener() {
                @Override
                public void onDeleteDownloadFilePrepared(DownloadFileInfo downloadFileNeedDelete) {
                }

                @Override
                public void onDeleteDownloadFileSuccess(DownloadFileInfo downloadFileDeleted) {
                }

                @Override
                public void onDeleteDownloadFileFailed(DownloadFileInfo downloadFileInfo, DeleteDownloadFileFailReason failReason) {
                }
            });
        }

        // 注册下载状态监听器
        FileDownloader.registerDownloadStatusListener(mOnFileDownloadStatusListener);

        // 注册文件数据变化监听器
        FileDownloader.registerDownloadFileChangeListener(mOnDownloadFileChangeListener);

        // 下载文件和管理文件
//        FileDownloader.start(url);// 如果文件没被下载过，将创建并开启下载，否则继续下载，自动会断点续传（如果服务器无法支持断点续传将从头开始下载）

//        FileDownloader.detect(url, new OnDetectBigUrlFileListener() {
//            @Override
//            public void onDetectNewDownloadFile(String url, String fileName, String saveDir, long fileSize) {
//                // 如果有必要，可以改变文件名称fileName和下载保存的目录saveDir
//                FileDownloader.createAndStart(url, newFileDir, newFileName);
//            }
//
//            @Override
//            public void onDetectUrlFileExist(String url) {
//                // 继续下载，自动会断点续传（如果服务器无法支持断点续传将从头开始下载）
//                FileDownloader.start(url);
//            }
//
//            @Override
//            public void onDetectUrlFileFailed(String url, DetectBigUrlFileFailReason failReason) {
//                // 探测一个网络文件失败了，具体查看failReason
//            }
//        });

        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionUtils.needCheckPermission()) {
                    requestPermissions(DownloaderActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
                        @Override
                        public void granted() {
                            downBtn.setVisibility(View.GONE);
                            stateLl.setVisibility(View.VISIBLE);
                            openBtn.setVisibility(View.GONE);
                            // 下载文件和管理文件
                            FileDownloader.start(url);
                        }

                        @Override
                        public void denied() {
                            Toast.makeText(DownloaderActivity.this, "获取权限失败，正常功能受到影响", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    downBtn.setVisibility(View.GONE);
                    stateLl.setVisibility(View.VISIBLE);
                    openBtn.setVisibility(View.GONE);
                    // 下载文件和管理文件
                    FileDownloader.start(url);
                }
            }
        });

        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadFileInfo info = FileDownloader.getDownloadFile(url);
                if (info != null) {
                    // 打开文件
                    OpenWays.openFile(DownloaderActivity.this, new File(info.getFilePath()));
                }
            }
        });

    }

    // 下载状态监听
    private OnFileDownloadStatusListener mOnFileDownloadStatusListener = new OnSimpleFileDownloadStatusListener() {
        @Override
        public void onFileDownloadStatusRetrying(DownloadFileInfo downloadFileInfo, int retryTimes) {
            // 正在重试下载（如果你配置了重试次数，当一旦下载失败时会尝试重试下载），retryTimes是当前第几次重试
            Log.e("Status", "正在重试下载");
        }

        @Override
        public void onFileDownloadStatusWaiting(DownloadFileInfo downloadFileInfo) {
            // 等待下载（等待其它任务执行完成，或者FileDownloader在忙别的操作）
            Log.e("Status", "等待下载");
        }

        @Override
        public void onFileDownloadStatusPreparing(DownloadFileInfo downloadFileInfo) {
            // 准备中（即，正在连接资源）
            Log.e("Status", "准备中");
        }

        @Override
        public void onFileDownloadStatusPrepared(DownloadFileInfo downloadFileInfo) {
            // 已准备好（即，已经连接到了资源）
            Log.e("Status", "已准备好");
        }

        @Override
        public void onFileDownloadStatusDownloading(DownloadFileInfo downloadFileInfo, float downloadSpeed, long
                remainingTime) {
            // 正在下载，downloadSpeed为当前下载速度，单位KB/s，remainingTime为预估的剩余时间，单位秒
            Log.e("Status", "正在下载" + downloadSpeed);
            stateTv.setText("文件下载中…");
            int progress = (int) (((double) downloadFileInfo.getDownloadedSizeLong()) / ((double) downloadFileInfo.getFileSizeLong()) * 100);
            Log.e("progress", progress + "");
            statePb.setProgress(progress);
        }

        @Override
        public void onFileDownloadStatusPaused(DownloadFileInfo downloadFileInfo) {
            // 下载已被暂停
            Log.e("Status", "下载已被暂停");
            stateTv.setText("下载已被暂停");
        }

        @Override
        public void onFileDownloadStatusCompleted(DownloadFileInfo downloadFileInfo) {
            // 下载完成（整个文件已经全部下载完成）
            Log.e("Status", "下载完成");
            stateTv.setText("下载完成");
            statePb.setProgress(100);
            openBtn.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFileDownloadStatusFailed(String url, DownloadFileInfo downloadFileInfo, FileDownloadStatusFailReason failReason) {
            // 下载失败了，详细查看失败原因failReason，有些失败原因你可能必须关心

            String failType = failReason.getType();
            String failUrl = failReason.getUrl();// 或：failUrl = url，url和failReason.getType()会是一样的

            if (FileDownloadStatusFailReason.TYPE_URL_ILLEGAL.equals(failType)) {
                // 下载failUrl时出现url错误
                Log.e("Status", "下载failUrl时出现url错误");
            } else if (FileDownloadStatusFailReason.TYPE_STORAGE_SPACE_IS_FULL.equals(failType)) {
                // 下载failUrl时出现本地存储空间不足
                Log.e("Status", "下载failUrl时出现本地存储空间不足");
            } else if (FileDownloadStatusFailReason.TYPE_NETWORK_DENIED.equals(failType)) {
                // 下载failUrl时出现无法访问网络
                Log.e("Status", "下载failUrl时出现无法访问网络");
            } else if (FileDownloadStatusFailReason.TYPE_NETWORK_TIMEOUT.equals(failType)) {
                // 下载failUrl时出现连接超时
                Log.e("Status", "下载failUrl时出现连接超时");
            } else {
                // 更多错误....
                Log.e("Status", "更多错误");
            }

            // 查看详细异常信息
            Throwable failCause = failReason.getCause();// 或：failReason.getOriginalCause()
            Log.e("Status", "" + failCause);

            // 查看异常描述信息
            String failMsg = failReason.getMessage();// 或：failReason.getOriginalCause().getMessage()
            Log.e("Status", "" + failMsg);
            stateTv.setText("" + failMsg);
        }
    };

    // 文件状态修改监听
    private OnDownloadFileChangeListener mOnDownloadFileChangeListener = new OnDownloadFileChangeListener() {

        @Override
        public void onDownloadFileCreated(DownloadFileInfo downloadFileInfo) {
            // 一个新下载文件被创建，也许你需要同步你自己的数据存储，比如在你的业务数据库中增加一条记录
            Log.e("Status", "一个新下载文件被创建");
        }

        @Override
        public void onDownloadFileUpdated(DownloadFileInfo downloadFileInfo, Type type) {
            // 一个下载文件被更新，也许你需要同步你自己的数据存储，比如在你的业务数据库中更新一条记录
            Log.e("Status", "一个下载文件被更新");
        }

        @Override
        public void onDownloadFileDeleted(DownloadFileInfo downloadFileInfo) {
            // 一个下载文件被删除，也许你需要同步你自己的数据存储，比如在你的业务数据库中删除一条记录
            Log.e("Status", "一个下载文件被删除");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadFileInfo fileInfo = FileDownloader.getDownloadFile(url);
        if (fileInfo != null && fileInfo.getStatus() != Status.DOWNLOAD_STATUS_COMPLETED) {
            FileDownloader.pause(url);
            FileDownloader.delete(url, true, null);
        }
        if (mOnDownloadFileChangeListener != null) {
            FileDownloader.unregisterDownloadFileChangeListener(mOnDownloadFileChangeListener);
        }
        if (mOnFileDownloadStatusListener != null) {
            FileDownloader.unregisterDownloadStatusListener(mOnFileDownloadStatusListener);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            DownloaderActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
