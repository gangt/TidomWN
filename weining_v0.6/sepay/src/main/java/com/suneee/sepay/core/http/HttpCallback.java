package com.suneee.sepay.core.http;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * 网络接口结果处理
 * Created by suneee on 2016/6/24.
 */
public abstract class HttpCallback<T> {

    private static final String TAG = HttpCallback.class.getSimpleName();

    public abstract void onFailure(Exception e);

    public abstract void onSuccess(T result);

    public T parseNetworkResponse(Response response) throws Exception{

        String body = response.body().string();
        Log.e(TAG, body );
        //获取T的类型
        Type mySuperClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType)mySuperClass).getActualTypeArguments()[0];
        T result = new Gson().fromJson(body, type);
        return result;
    }

}
