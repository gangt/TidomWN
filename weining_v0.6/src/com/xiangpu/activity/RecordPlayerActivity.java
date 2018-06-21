package com.xiangpu.activity;

import java.io.File;
import java.io.IOException;

import com.konecty.rocket.chat.R;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.views.RecordPlayer;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class RecordPlayerActivity extends Activity implements OnClickListener {
	// 开始录音
    private Button start;
    // 停止按钮
    private Button stop;
    // 播放按钮
    private Button paly;
    // 暂停播放
    private Button pause_paly;
    // 停止播放
    private Button stop_paly;

    // 录音类
    private MediaRecorder mediaRecorder;
    // 以文件的形式保存
    private File recordFile;

    private RecordPlayer player; 
    private boolean isRecording = false;
    private boolean isPlayer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordplayer);

        recordFile = new File("/mnt/sdcard", "kk.amr");

        initView();
        Listener();
    }

    private void initView() {
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        paly = (Button) findViewById(R.id.paly);
        pause_paly = (Button) findViewById(R.id.pause_paly);
        stop_paly = (Button) findViewById(R.id.stop_paly);
    }

    private void Listener() {
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        paly.setOnClickListener(this);
        pause_paly.setOnClickListener(this);
        stop_paly.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        player = new RecordPlayer(this);
        int Id = v.getId();

        switch (Id) {
        case R.id.start:
        	if(!isRecording){
        		startRecording();
        		isRecording = true;
        		start.setText("录制中");
        	}else{
                ToastUtils.showCenterToast(this,"正在录制..");
        	}
        	
            break;
        case R.id.stop:
        	
        	if(isRecording){
        		stopRecording();
        		isRecording = false;
        		start.setText("开始录制");
        	}else{
        		ToastUtils.showCenterToast(this,"当前无录制..");
        	}
            
            break;
        case R.id.paly:
        	if(!isPlayer){
                playRecording();
                isPlayer = true;
        	}else{
                ToastUtils.showCenterToast(this,"正在播放..");
        	}

            break;
        case R.id.pause_paly:
            pauseplayer();
            break;
        case R.id.stop_paly:
        	if(isPlayer){
        		stopplayer();
        		isPlayer = false;
        	}else{
                ToastUtils.showCenterToast(this,"当前未播放..");
        	}
            
            break;
        }
    }
    
    @Override
	public void onDestroy(){
		super.onDestroy();
		if(isPlayer){
			stopplayer();
    		isPlayer = false;
		}
		
		if(isRecording){
    		stopRecording();
    		isRecording = false;
		}
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        // 判断，若当前文件已存在，则删除
        if (recordFile.exists()) {
            recordFile.delete();
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setOutputFile(recordFile.getAbsolutePath());

        try {
            // 准备好开始录音
            mediaRecorder.prepare();

            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void stopRecording() {
        if (recordFile != null && mediaRecorder!=null) {
            mediaRecorder.stop();
            mediaRecorder.release();
        }
    }

    private void playRecording() {
        player.playRecordFile(recordFile);
    }


    private void pauseplayer() {
        player.pausePalyer();
    }

    private void stopplayer() {
        player.stopPalyer();
    }

}
