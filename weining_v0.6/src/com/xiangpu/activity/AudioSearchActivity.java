package com.xiangpu.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.konecty.rocket.chat.R;
import com.xiangpu.adapter.VoiceSearchResultAdapter;
import com.xiangpu.bean.AudioSearchBean;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.JsonParser;
import com.xiangpu.utils.ToastUtils;
import com.xiangpu.utils.WebServiceUtil;
import com.xiangpu.utils.WebServiceUtil.OnDataListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class AudioSearchActivity extends BaseActivity implements OnClickListener, OnDataListener {
    private String TAG = "VioceSearchActivity";

    private EditText mResultText;
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 语音听写UI
    private RecognizerDialog mIatDialog;

    private LinearLayout voice_search_flag_id;
    private LinearLayout voice_search_input_id;
    private LinearLayout voice_search_result_id;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private List<AudioSearchBean> audioList = new ArrayList<AudioSearchBean>();

    private int tx_audio[] = {R.id.text_audio_1_id, R.id.text_audio_2_id, R.id.text_audio_3_id,
            R.id.text_audio_4_id, R.id.text_audio_5_id, R.id.text_audio_6_id,
            R.id.text_audio_7_id, R.id.text_audio_8_id, R.id.text_audio_9_id};

    private ListView list_view_id;
    private VoiceSearchResultAdapter adapter;

    private TextView[] txt_audio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiosearch);

//        TitleHeaderBar  titleBar = (TitleHeaderBar) findViewById(R.id.titleBar);
//        titleBar.setTitleText("搜索");
//        titleBar.setLeftOnClickListener(this);

        mResultText = (EditText) this.findViewById(R.id.edit_input_idea_id);
        mResultText.setText("");

        this.findViewById(R.id.iat_recognize).setOnClickListener(this);
        this.findViewById(R.id.img_university_text_id).setOnClickListener(this);
        this.findViewById(R.id.img_stop_search_id).setOnClickListener(this);

        mIat = SpeechRecognizer.createRecognizer(AudioSearchActivity.this, mInitListener);

        mIatDialog = new RecognizerDialog(AudioSearchActivity.this, mInitListener);

        mSharedPreferences = getSharedPreferences("com.iflytek.setting",
                Activity.MODE_PRIVATE);

        this.findViewById(R.id.img_university_speech_id).setOnClickListener(this);

        voice_search_input_id = (LinearLayout) this.findViewById(R.id.voice_search_input_id);
        voice_search_flag_id = (LinearLayout) this.findViewById(R.id.voice_search_flag_id);
        voice_search_result_id = (LinearLayout) this.findViewById(R.id.voice_search_result_id);

        list_view_id = (ListView) this.findViewById(R.id.list_view_id);
        adapter = new VoiceSearchResultAdapter(this, audioList);
        list_view_id.setAdapter(adapter);

        TextView txt_a[] = {
                (TextView) this.findViewById(R.id.text_audio_1_id),
                (TextView) this.findViewById(R.id.text_audio_2_id),
                (TextView) this.findViewById(R.id.text_audio_3_id),

                (TextView) this.findViewById(R.id.text_audio_4_id),
                (TextView) this.findViewById(R.id.text_audio_5_id),
                (TextView) this.findViewById(R.id.text_audio_6_id),

                (TextView) this.findViewById(R.id.text_audio_7_id),
                (TextView) this.findViewById(R.id.text_audio_8_id),
                (TextView) this.findViewById(R.id.text_audio_9_id),
        };

        txt_audio = txt_a;

        txt_audio[0].setOnClickListener(this);
        txt_audio[1].setOnClickListener(this);
        txt_audio[2].setOnClickListener(this);

        txt_audio[3].setOnClickListener(this);
        txt_audio[4].setOnClickListener(this);
        txt_audio[5].setOnClickListener(this);

        txt_audio[6].setOnClickListener(this);
        txt_audio[7].setOnClickListener(this);
        txt_audio[8].setOnClickListener(this);

    }

    int ret = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.text_audio_1_id:
                SearchContent(txt_audio[0].getText().toString());
                break;
            case R.id.text_audio_2_id:
                SearchContent(txt_audio[1].getText().toString());
                break;
            case R.id.text_audio_3_id:
                SearchContent(txt_audio[2].getText().toString());
                break;
            case R.id.text_audio_4_id:
                SearchContent(txt_audio[3].getText().toString());
                break;
            case R.id.text_audio_5_id:
                SearchContent(txt_audio[4].getText().toString());
                break;
            case R.id.text_audio_6_id:
                SearchContent(txt_audio[5].getText().toString());
                break;
            case R.id.text_audio_7_id:
                SearchContent(txt_audio[6].getText().toString());
                break;
            case R.id.text_audio_8_id:
                SearchContent(txt_audio[7].getText().toString());
                break;
            case R.id.text_audio_9_id:
                SearchContent(txt_audio[8].getText().toString());
                break;

            case R.id.ly_title_bar_left:
                this.finish();
                break;
            case R.id.img_university_text_id:

                this.hideKeyboard(mResultText);

                if (mResultText.getText().toString().equals("")) {
                    ToastUtils.showCenterToast(this, "请输入搜索关键字");
                } else {
                    mResultText.setText(mResultText.getText().toString());
                    moduleSearchAudio();
                }

                break;
            case R.id.img_university_speech_id:
                showUI(0);
                break;
            case R.id.img_stop_search_id:
                showUI(1);
                break;
            case R.id.iat_recognize:
                FlowerCollector.onEvent(AudioSearchActivity.this, "iat_recognize");

                mResultText.setText("");
                mIatResults.clear();

                setParam();

                if (true) {

                    mIatDialog.setListener(mRecognizerDialogListener);
                    mIatDialog.show();

                } else {

                    ret = mIat.startListening(mRecognizerListener);
                    if (ret != ErrorCode.SUCCESS) {
                        showTip("搜索结果：" + ret);
                    } else {
                        //showTip("锟诫开始说锟斤拷锟斤拷");
                    }
                }
                break;
        }
    }

    private void SearchContent(String content) {
        mResultText.setText(content);
        moduleSearchAudio();
    }

    private void showTip(final String str) {

    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 锟斤拷写UI锟斤拷锟斤拷锟斤拷
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
        }

        /**
         * 识锟斤拷氐锟斤拷锟斤拷锟�.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };


    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;

        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        showUI(1);

        String strRet = resultBuffer.toString().replace("。", "");

        mResultText.setText(strRet);

        moduleSearchAudio();
    }

    /**
     * 锟斤拷写锟斤拷锟斤拷锟斤拷锟斤拷
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            // 如果使用本地功能（语记）需要提示用户开启语记的录音权限。
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            printResult(results);

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    // 锟斤拷锟斤拷锟斤拷锟斤拷
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private SharedPreferences mSharedPreferences;

    /**
     * 锟斤拷锟斤拷锟斤拷锟斤拷
     *
     * @return
     */
    public void setParam() {
        // 清空参数
//        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接,把下面两行代码注释掉后点击返回就不会卡死了
        mIat.cancel();
        mIat.destroy();
    }

    private void moduleSearchAudio() {
        WebServiceUtil.request(Constants.moduleSearchAudio, "", this);
    }

    @Override
    public void onReceivedData(String mode, JSONObject result) {
        // TODO Auto-generated method stub
        if (result == null) {
            return;
        }

        if (mode != null && mode.equals(Constants.moduleSearchAudio)) {
            try {
                JSONObject response = result.getJSONObject("response");

                int numFound = response.getInt("numFound");
                if (numFound <= 0) {
                    ToastUtils.showCenterToast(this, "未搜索到任何结果");
                    return;
                }
                int start = response.getInt("start");
                JSONArray jsonList = response.getJSONArray("docs");

                audioList.clear();

                mResultText.setText("");

                for (int i = 0; i < jsonList.length(); i++) {
                    JSONObject search = jsonList.getJSONObject(i);

                    AudioSearchBean bean = new AudioSearchBean();

                    String id = search.getString("id");
                    String _version_ = search.getString("_version_");
                    String code = search.getString("code");
                    String type = search.getString("type");

                    if (!search.isNull("url")) {
                        String url = search.getString("url");
                        bean.url = url;
                    }

                    if (!search.isNull("remark")) {
                        String remark = search.getString("remark");
                        bean.remark = remark;
                    }

                    String name = search.getString("name");

                    bean.id = id;
                    bean._version_ = _version_;
                    bean.code = code;
                    bean.type = type;

                    bean.name = name;

                    audioList.add(bean);

                    mHandler.sendEmptyMessage(0);
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<NameValuePair> onGetParamData(String mode) {
        // TODO Auto-generated method stub
        if (mode != null && mode.equals(Constants.moduleSearchAudio)) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.addAll(baseParam);
            params.add(new BasicNameValuePair("q", "name:" + mResultText.getText().toString()));
            params.add(new BasicNameValuePair("wt", "json"));
            return params;
        }
        return null;
    }

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            showAudioRecord();
        }
    };

    public void showAudioRecord() {
//		for(int i=0;i<audioList.size();i++){
//			AudioSearchBean bean = audioList.get(i);
//			if(i<9){
//				TextView text = (TextView)this.findViewById(tx_audio[i]);
//				text.setText(bean.name);
//			}
//		}
        showUI(2);
        adapter.notifyDataSetChanged();
    }

    private void showUI(int index) {
        if (index == 0) {
            voice_search_input_id.setVisibility(View.VISIBLE);
            voice_search_flag_id.setVisibility(View.GONE);
            voice_search_result_id.setVisibility(View.GONE);
        } else if (index == 1) {
            voice_search_input_id.setVisibility(View.GONE);
            voice_search_flag_id.setVisibility(View.VISIBLE);
            voice_search_result_id.setVisibility(View.GONE);
        } else if (index == 2) {
            voice_search_input_id.setVisibility(View.GONE);
            voice_search_flag_id.setVisibility(View.GONE);
            voice_search_result_id.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public String onGetParamDataString(String mode) {
        // TODO Auto-generated method stub
        return null;
    }
}
