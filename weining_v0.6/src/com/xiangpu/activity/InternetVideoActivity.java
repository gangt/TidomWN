package com.xiangpu.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.konecty.rocket.chat.R;
import com.xiangpu.utils.ToastUtils;

/**
 * Created by Andi on 2017/5/22.
 */
public class InternetVideoActivity extends BaseActivity {

    private final String url = "http://116.10.197.132:2935/live/live2/800k/tzwj_video.m3u8";

    private VideoView videoView;
    private int position;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.video_view_activity);

        Uri uri = Uri.parse(url);
        videoView = (VideoView) this.findViewById(R.id.videoView);
        videoView.setMediaController(new MediaController(this));
        videoView.setOnCompletionListener(new MyPlayerOnCompletionListener());
        videoView.setVideoURI(uri);
        videoView.start();
        videoView.requestFocus();

        loadPoint("", "");
    }

    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            ToastUtils.showCenterToast(InternetVideoActivity.this, "播放完成了");
        }
    }

    protected void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        videoView.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }
}
