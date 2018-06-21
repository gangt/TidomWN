package com.suneee.sepay.core.http;

import android.os.Handler;
import android.os.Looper;

import com.suneee.sepay.core.log.LoggerInterceptor;
import com.suneee.sepay.core.sepay.config.SEPayConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 网络请求类，提供get/post请求方式
 * Created by suneee on 2016/6/24.
 */
public class SEHttpClient {

    /**
     * 网络请求timeout时间
     * 以毫秒为单位
     */
    private static long defaultConnectTimeout = 10*1000;

    private static SEHttpClient instance;
    private OkHttpClient okHttpClient;
    private Handler handler;

    private SEHttpClient(){
        handler = new Handler(Looper.getMainLooper());
        okHttpClient = createOKHttpClicent();

        if(SEPayConfig.getInstance().getHttpConnectTimeout()>0){
            defaultConnectTimeout = SEPayConfig.getInstance().getHttpConnectTimeout();
        }
    }

    public static SEHttpClient getInstance(){
        if(instance == null){
            synchronized (SEHttpClient.class){
                if(instance == null){
                    instance = new SEHttpClient();
                }
            }
        }

        return instance;
    }

    public void httpGet(String url, final HttpCallback callback){
        Request request = new Request.Builder()
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailureResponse(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response.isSuccessful()){
                        sendSuccessResponse(callback, callback.parseNetworkResponse(response));
                    }else{
                        sendFailureResponse(callback, new Exception(response.message()));
                    }
                }catch (Exception e){
                    sendFailureResponse(callback, e);
                }
            }
        });
    }

    public void httpPost(String url, String reqParamsJson, final HttpCallback callback){
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(JSON, reqParamsJson);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailureResponse(callback, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response.isSuccessful()){

                        sendSuccessResponse(callback, callback.parseNetworkResponse(response));
                    }else{
                        sendFailureResponse(callback, new Exception(response.message()));
                    }
                }catch (Exception e){
                    sendFailureResponse(callback, e);
                }
            }
        });
    }

    private void sendSuccessResponse(final HttpCallback callback, final Object object){
        if (callback == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(object);
            }
        });
    }

    private void sendFailureResponse(final HttpCallback callback, final Exception e){
        if (callback == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(e);
            }
        });
    }


    public OkHttpClient createOKHttpClicent(){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(defaultConnectTimeout, TimeUnit.MILLISECONDS)
                //.cookieJar()
                //.hostnameVerifier()
                .build();

        boolean isDebug = SEPayConfig.getInstance().isDebug();
        if(isDebug){
            String tag = SEPayConfig.getInstance().getDebugTag();
            client = client.newBuilder().addInterceptor(new LoggerInterceptor(tag, true)).build();
        }

        return client;
    }
}
